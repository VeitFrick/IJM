package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.runner.io.ProgressWritable;
import at.aau.softwaredynamics.runner.meta.Commit;
import at.aau.softwaredynamics.runner.meta.CommitPair;

import java.io.*;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import static at.aau.softwaredynamics.runner.git.FullProjectAnalyzer.logToConsole;

public class CoordinatorProgressRunner implements Runnable {
    private File progressFile;
    private FileWriter writer;
    private BlockingQueue<ProgressWritable> queue;
    private int numOfProducers;
    private boolean append = false;

    public CoordinatorProgressRunner(BlockingQueue<ProgressWritable> queue, String projectName, int numOfProducers) throws IOException {
        this.queue = queue;
        this.numOfProducers = numOfProducers;

        Date date = new Date();
        progressFile = new File(projectName + "_progress_" + date.getTime() + ".txt"); // creates the file
    }

    public void run() {
        try {
            // creates a FileWriter Object
            progressFile.createNewFile();
            writer = new FileWriter(progressFile, append);
            logToConsole("Progress Runner is started and writing to progress file: " + progressFile, "ProgressRunner", "INFO");

            while (true) {
                if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                ProgressWritable progressWritable = queue.take();
                if (progressWritable.isNull()) {
                    logToConsole("Worker terminated.", "ProgressRunner", "INFO");
                    numOfProducers--;
                    if (numOfProducers <= 0) {
                        logToConsole("All CoordinatorProgress Consumer terminated.", "ProgressRunner", "INFO");
                        break;
                    }
                    continue;
                }
                logToConsole("Wrote " + progressWritable.getProgressString() + " to progress file", "ProgressRunner", "VERBOSE");
                writer.write(progressWritable.getProgressString());
                writer.flush();
            }
            writer.flush();
            writer.close();
        } catch (InterruptedException ex) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            logToConsole("Consumer interrupted", "", "ERROR");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getProgressFile() {
        return progressFile;
    }

    /**
     * Removes commit pairs in file from the worklist and on success copies the
     * work done into the current newer progress file
     * @param file the old progress file to load from
     * @param commitPairs the worklist of commit pairs
     */
    void applyProgressToCommitPairs(File file, Collection<CommitPair> commitPairs) throws IOException {

        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader);
             FileWriter writer = new FileWriter(progressFile, false)) {

            // read line by line
            String line;
            int cnt = 0;
            while ((line = br.readLine()) != null) {
                String[] commitIds = line.split("\\|");
                if (commitPairs.removeIf(
                        (commitPair -> commitPair.getSrcCommit().getName().equals(commitIds[0])
                                && commitPair.getDstCommit().getName().equals(commitIds[1])))) {
                    logToConsole("Successfully applied " + line, "progressApply", "VERBOSE");
                    cnt++;
                    writer.write(line + '\n');
                    writer.flush();
                } else {
                    logToConsole("Couldn't find pair. Wrong progress file? " + line, "progressApply", "ERROR");
                }
            }
            writer.close();
            logToConsole("Applied " + cnt + " (work left: " + commitPairs.size() + ") commit pairs from progress file " + file.getName(), "progressApply", "INFO");
            this.append = true; // so we append future progress

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            throw e;
        }
    }

    /**
     * Removes commits in file from the worklist and on success copies the
     * work done into the current newer progress file
     *
     * @param file    the old progress file to load from
     * @param commits the worklist of commits
     */
    void applyProgressToCommits(File file, Collection<Commit> commits) throws IOException {

        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader);
             FileWriter writer = new FileWriter(progressFile, false)) {

            // read line by line
            String line;
            int cnt = 0;
            while ((line = br.readLine()) != null) {
                String commitName = line;
                if (commits.removeIf(
                        (commit -> commit.getRevCommit().getName().equals(commitName)))) {
                    logToConsole("Successfully applied " + line, "progressApply", "VERBOSE");
                    cnt++;
                    writer.write(line + '\n');
                    writer.flush();

                } else {
                    logToConsole("Couldn't find commit. Wrong progress file? " + line, "progressApply", "ERROR");

                }
            }
            writer.close();
            logToConsole("Applied " + cnt + " (work left: " + commits.size() + ") commits from progress file " + file.getName(), "progressApply", "INFO");
            this.append = true; // so we append future progress

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            throw e;
        }
    }
}
