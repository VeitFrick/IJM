package at.aau.softwaredynamics.diffws.util;

public class ChangePair {

    private String filename;
    private String src;
    private String dst;

    public ChangePair(String src, String dst) {
        this.src = src;
        this.dst = dst;
    }

    public ChangePair(String filename, String src, String dst) {
        this.filename = filename;
        this.src = src;
        this.dst = dst;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }
}
