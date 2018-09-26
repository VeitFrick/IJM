package runner;

import runner.git.RepositoryAnalyzer;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by thomas on 19.12.2016.
 */
public class GitAnalysisWorker extends SwingWorker implements Observer {
    // runner elements
    JButton startButton;
    JProgressBar progressBar;

    private final RepositoryAnalyzer analyzer;

    public GitAnalysisWorker(RepositoryAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public void setStartButton(JButton button) { this.startButton = button; }
    public void setProgressBar(JProgressBar bar) { this.progressBar = bar; }

    @Override
    protected Object doInBackground() throws Exception {
        try {
            if (startButton != null)
                startButton.setEnabled(false);

            analyzer.addObserver(this);
            analyzer.analyzeRepository();
        } catch (Exception ex) {
            // ... for the moment we do nothing here ...
            ex.printStackTrace();
        }

        analyzer.deleteObserver(this);
        return null;
    }

    @Override
    public void done() {
        if (startButton != null)
            startButton.setEnabled(true);
        if (progressBar != null)
            progressBar.setValue(progressBar.getMaximum());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (progressBar != null) {

            int total = analyzer.getTotalCommits();

            int processed = analyzer.getCurrentCommit();

            int percentage = Math.max(1, (int) ((processed / (double)total) * 100) );

            progressBar.setValue(percentage);
            progressBar.setString(percentage + " %");
        }
    }
}
