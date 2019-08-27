package at.aau.softwaredynamics.diffws.domain;

public class Metrics {
    private long matchingTime;
    private long classificationTime;

    public Metrics() {}

    public long getMatchingTime() {
        return matchingTime;
    }

    public void setMatchingTime(long matchingTime) {
        this.matchingTime = matchingTime;
    }

    public long getClassificationTime() {
        return classificationTime;
    }

    public void setClassificationTime(long classificationTime) {
        this.classificationTime = classificationTime;
    }
}
