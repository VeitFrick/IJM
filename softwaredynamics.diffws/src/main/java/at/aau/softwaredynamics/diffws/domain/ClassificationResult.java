package at.aau.softwaredynamics.diffws.domain;

import java.util.List;

public class ClassificationResult {
    private Metrics metrics;
    private List<Result> results;

    public ClassificationResult(Metrics metrics, List<Result> results) {
        this.metrics = metrics;
        this.results = results;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
