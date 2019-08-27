package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.classifier.util.LineNumberHelper;
import at.aau.softwaredynamics.runner.util.GitHelper;
import at.aau.softwaredynamics.runner.util.VirtualSpoonPom;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.*;
import spoon.Launcher;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.VirtualFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static at.aau.softwaredynamics.runner.git.FullProjectAnalyzer.logToConsole;

/**
 * Holds a snapshot cache and can generate snapshots of a project
 * using its commit on demand. Tries to avoid running into a OutOfMemory Exception.
 */
public class SnapshotGenerator {

    private String projectName;
    private Map<ObjectId, SoftReference<ProjectSnapshot>> snapshotCache;
    private Map<RevCommit, Map<String, VirtualSpoonPom>> pomMap = new HashMap<>();
    private Repository repository;
    private String treeName;
    private String subPath;
    private boolean checkForMaven;
    Map<ObjectId, AtomicInteger> commitUsages;


    public SnapshotGenerator(Repository repository, String treeName, String subPath, Map<ObjectId, AtomicInteger> commitUsages) {
        this.repository = repository;
        this.projectName = repository.getWorkTree().getName();
        this.treeName = treeName;
        this.snapshotCache = new ConcurrentHashMap<>();
        this.subPath = subPath;
        this.checkForMaven = true;
        this.commitUsages = commitUsages;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSubPath() {
        return subPath;
    }

    public boolean isCheckForMaven() {
        return checkForMaven;
    }

    public void setCheckForMaven(boolean checkForMaven) {
        this.checkForMaven = checkForMaven;
    }

    public boolean isInCache(RevCommit commitId) {
        return snapshotCache.containsKey(commitId)
                && snapshotCache.get(commitId).get() != null;
    }

    /**
     * @return null if no elements in cache
     */
    public ObjectId getCachedCommitWithLowestRemainingUsages() {
        ObjectId candidate = null;
        int minUsage = Integer.MAX_VALUE;
        for (Map.Entry<ObjectId, SoftReference<ProjectSnapshot>> entry : snapshotCache.entrySet()) {
            if (entry.getValue().get() == null) continue;
            if (commitUsages.get(entry.getKey()).get() < minUsage)
                candidate = entry.getKey();
        }

        return candidate;
    }

    /**
     * Reduces the amount of remaining usages for a specified commit
     * the snapshot of the commit will be deleted if this number hits 0
     *
     * @param id the sha1 {@link ObjectId} hash of the commit
     */
    public void useCommit(ObjectId id) {
        int usages = commitUsages.get(id).decrementAndGet();
        if (usages == 0) {
//            System.out.println("Commit " + id.getName() + " not needed anymore.");
            snapshotCache.get(id).clear();
        } else if (usages < 0) {
            System.out.println("Commit has already hit it's usage limit.");
        }
    }

    public Map<RevCommit, Map<String, VirtualSpoonPom>> getPomMap() {
        return pomMap;
    }

    public void setPomMap(Map<RevCommit, Map<String, VirtualSpoonPom>> pomMap) {
        this.pomMap = pomMap;
    }

    /**
     * Walks to the specified commit and extracts all files in the project as VirtualFiles that end with fileEnding.
     *
     * @param commit The {@link RevCommit} to look at
     * @param repository The repository where the commit is located
     * @return A Set of {@link VirtualFile}s of all java files at the commit.
     * @throws IOException when there is an error reading the files
     */
    private static Set<VirtualFile> getSpoonResourcesForCommit(Repository repository, RevCommit commit, List<String> pathFilter, String fileEnding) throws IOException {
        Set<VirtualFile> virtualFiles = new HashSet<>();
        RevTree tree = commit.getTree();

        // now use a TreeWalk to iterate over all files in the Tree recursively
        // you can set Filters to narrow down the results if needed
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);

            TreeFilter allowedPath;

            // remove all empty paths
            List<String> paths = pathFilter.stream()
                    .filter(p -> !p.isEmpty())
                    .collect(Collectors.toList());

            // remove all invalid paths
//            for (String path : paths) {
//                if (path.isEmpty() || File.separator.equals(path)) {
//                    paths.remove(path);
//                }
//            }

            if (paths.isEmpty()) {
                allowedPath = PathFilter.ALL;
            } else if (paths.size() == 1) {
                allowedPath = PathFilter.create(paths.get(0));
            } else {
                List<TreeFilter> orPaths = new ArrayList<>();
                for (String path : paths) {
                    orPaths.add(PathFilter.create(path));
                }
                allowedPath = OrTreeFilter.create(orPaths);
            }
            TreeFilter suffixFilter;
            if (fileEnding.isEmpty()) {
                suffixFilter = PathSuffixFilter.ALL;
            } else {
                suffixFilter = PathSuffixFilter.create(fileEnding);
            }
            TreeFilter filter = AndTreeFilter.create(allowedPath, suffixFilter);
            treeWalk.setFilter(filter);

            while (treeWalk.next()) {

                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = repository.open(objectId);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                // and then one can the loader to read the file
                loader.copyTo(outputStream);

                virtualFiles.add(new VirtualFile(LineNumberHelper.unfiyLineEndings(new String(outputStream.toByteArray(), StandardCharsets.UTF_8)), treeWalk.getPathString()));
            }
        }
        return virtualFiles;
    }

    Set<VirtualFile> getSpoonResourcesForCommit(RevCommit commit, List<String> pathFilter, String fileEnding) throws IOException {
        return getSpoonResourcesForCommit(this.repository, commit, pathFilter, fileEnding);
    }

    /**
     * Spawns a executor single thread and generates a snapshot
     * of the current repository at the selected commit
     *
     * @param commit selected commit
     * @return the generated snapshot
     */
    public ProjectSnapshot generateSnapshot(RevCommit commit) {
        CancelableCallable commitCallable = new CancelableCallable() {

            RevCommit commit;
            List<String> paths;
            Launcher launcher;

            public RevCommit getCommit() {
                return commit;
            }

            public void setCommit(RevCommit commit) {
                this.commit = commit;
            }

            public List<String> getPaths() {
                return paths;
            }

            public void setPaths(List<String> paths) {
                this.paths = paths;
            }

            @Override
            public ProjectSnapshot call() {
                Launcher launcher = new Launcher();
                launcher.getEnvironment().setNoClasspath(true);
                launcher.getEnvironment().setLevel("FATAL"); // try to generate no warnings in the log output
                List<VirtualFile> files = new ArrayList<>();

                try {
                    for (VirtualFile file : getSpoonResourcesForCommit(repository, commit, paths, ".java")) {
                        launcher.addInputResource(file);
                        files.add(file);
                    }
                    launcher.buildModel();
                    return generateSnapshotFromSpoonModel(commit, launcher.getFactory(), files);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            public void cancel() {
                launcher = null;
                // usually here you'd have inputStream.close() or connection.disconnect()
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        });
        commitCallable.setCommit(commit);
        commitCallable.setPaths(getSourceDirectories(commit, subPath, checkForMaven));
        Future<ProjectSnapshot> future = executor.submit(commitCallable);

        ProjectSnapshot snapshot = null;
        try {
            snapshot = future.get(1000, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException e) {
            future.cancel(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdownNow();
        return snapshot;
    }

    private ProjectSnapshot generateSnapshotFromSpoonModel(RevCommit commit, Factory spoonFactory, List<VirtualFile> files) throws IOException {
        ProjectSnapshot projectSnapshot = new ProjectSnapshot(commit, spoonFactory);

        // get renamed files from diffs
        if (commit.getParents().length > 0) {
            List<DiffEntry> diffs = GitHelper.getDiffs(repository, commit, commit.getParents()[0]);
            if (diffs.size() > 0) {
                for (DiffEntry diff : diffs) {
                    if (diff.getChangeType().equals(DiffEntry.ChangeType.RENAME)) {
                        projectSnapshot.fileRenames.add(new ImmutablePair<>(diff.getOldPath(), diff.getNewPath()));
                    }
                }
            }
        }

        for (VirtualFile file : files) {
            projectSnapshot.fileToContentMap.put(file.getName(), IOUtils.toString(file.getContent(), Charset.defaultCharset()));
        }

        return projectSnapshot;
    }

    private List<String> getSourceDirectories(RevCommit commit, String subPath, boolean checkForMaven) {
        VirtualSpoonPom pom = null;
        if (checkForMaven) {
            Map<String, VirtualSpoonPom> spoonPomMap = pomMap.get(commit);
            if (spoonPomMap != null) {
                pom = spoonPomMap.get(subPath);
                if (pom == null) {
                    logToConsole("No POM was found for module '" + subPath + "' at " + commit.getName(), Thread.currentThread().getName(), "WARNING");
                }
            }
        }
        // default path to current subPath when no pom is found with source directories defined ie. all files in this directory
        List<String> paths = pom != null && pom.getSourceDirectories().size() > 0 ? pom.getSourceDirectories() : Arrays.asList(subPath);
        logToConsole("Module '" + subPath + "' at " + commit.getName() + " got source path: " + paths, Thread.currentThread().getName(), "VERBOSE");
        return paths;
    }

    /**
     * Gets the snapshot out of the cache or generate it if it doesn't exist yet
     *
     * @param commit
     * @return
     */
    public ProjectSnapshot getSnapshot(RevCommit commit) {
        SoftReference<ProjectSnapshot> snapshot = snapshotCache.get(commit.getId());
        if (snapshot == null || snapshot.get() == null) {
            snapshot = new SoftReference<>(generateSnapshot(commit));
            snapshotCache.put(commit, snapshot);
        }
        return snapshot.get();
    }

    public Repository getRepository() {
        return repository;
    }
}
