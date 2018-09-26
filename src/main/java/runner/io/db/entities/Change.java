package runner.io.db.entities;

import javax.persistence.*;

/**
 * Created by thomas on 07.02.2017.
 */
@Entity
@Table( name = "changes" )
public class Change {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long diffid;
    private long matcherid;
    private long projectid;

    private int nodetype;

    private int dstid;

    private int srcid;

    @Column(length = 3)
    private String action;

    @Column(length = 50)
    private String changetype;

    private int srcstart;

    private int srcend;

    private int dststart;

    private int dstend;

    @Column(length = 500)
    private String srclabel;

    @Column(length = 500)
    private String dstlabel;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDiffid() {
        return diffid;
    }

    public void setDiffid(long diffid) {
        this.diffid = diffid;
    }

    public long getMatcherid() {
        return matcherid;
    }

    public void setMatcherid(long matcherid) {
        this.matcherid = matcherid;
    }

    public int getDstid() {
        return dstid;
    }

    public void setDstid(int dstid) {
        this.dstid = dstid;
    }

    public int getSrcid() {
        return srcid;
    }

    public void setSrcid(int srcid) {
        this.srcid = srcid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getChangetype() {
        return changetype;
    }

    public void setChangetype(String changetype) {
        this.changetype = changetype;
    }

    public int getSrcstart() {
        return srcstart;
    }

    public void setSrcstart(int srcstart) {
        this.srcstart = srcstart;
    }

    public int getSrcend() {
        return srcend;
    }

    public void setSrcend(int srcend) {
        this.srcend = srcend;
    }

    public int getDststart() {
        return dststart;
    }

    public void setDststart(int dststart) {
        this.dststart = dststart;
    }

    public int getDstend() {
        return dstend;
    }

    public void setDstend(int dstend) {
        this.dstend = dstend;
    }

    public String getSrclabel() {
        return srclabel;
    }

    public void setSrclabel(String srclabel) {
        this.srclabel = srclabel;
    }

    public String getDstlabel() {
        return dstlabel;
    }

    public void setDstlabel(String dstlabel) {
        this.dstlabel = dstlabel;
    }

    public long getProjectid() {
        return projectid;
    }

    public void setProjectid(long projectid) {
        this.projectid = projectid;
    }

    public int getNodetype() {
        return nodetype;
    }

    public void setNodetype(int nodetype) {
        this.nodetype = nodetype;
    }
}
