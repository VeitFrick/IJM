package at.aau.softwaredynamics.runner.util;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import spoon.support.compiler.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VirtualSpoonPom {

    //    List<VirtualSpoonPom> modules = new ArrayList<>();
    Model model;
    VirtualSpoonPom parent;
    VirtualFile pomFile;
    String directory;
    List<VirtualSpoonPom> modules;

    public VirtualSpoonPom(VirtualFile pom) throws IOException, XmlPullParserException {
        this(pom, null);
    }

    public VirtualSpoonPom(VirtualFile pom, VirtualSpoonPom parent) throws IOException, XmlPullParserException {
        this.parent = parent;
        this.modules = new ArrayList<>();
        this.pomFile = pom;
        this.directory = "";
        if(Paths.get(pom.getPath()).getParent()!=null){
            this.directory = Paths.get(pom.getPath()).getParent().toString();
        }

        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        InputStream pomFileContent = pomFile.getContent();
        this.model = pomReader.read(pomFileContent);
    }

    public void addModule(VirtualSpoonPom module) {
        modules.add(module);
    }

    public String getDirectory() {
        return directory;
    }

    public List<VirtualSpoonPom> getModules() {
        return modules;
    }

    /**
     * Get the SnapshotGenerator Object Model
     *
     * @return the SnapshotGenerator Object Model
     */
    public Model getModel() {
        return model;
    }

    /**
     * Get the list of source directories of the project
     *
     * @return the list of source directories
     */
    public List<String> getSourceDirectories() {
        List<String> output = new ArrayList<>();
        String sourcePath = null;

        Build build = model.getBuild();
        if (build != null && build.getSourceDirectory() != null) {
            sourcePath = Paths.get(directory, build.getSourceDirectory()).toString();
        }
        if (sourcePath == null) {
            // look up if source path is defined in parent!
            if (parent != null) sourcePath = Paths.get(directory, parent.getSourceDirectories().get(0)).toString();
                // fallback for default mvn structure if not defined
            else sourcePath = Paths.get(directory, "src", "main", "java").toString();
        }
        if (sourcePath != null) output.add(sourcePath);

        return output;
    }
}
