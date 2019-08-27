package at.aau.softwaredynamics.runner.classification;

public class ClassifyCommitRunner {

    public static void main(String[] args) {
        //BasicConfigurator.configure();
        new ClassifyCli(args).parse();

    }

}
