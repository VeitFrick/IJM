package runner.io.db.entities;

import javax.persistence.*;

/**
 * Created by thomas on 07.02.2017.
 */
@Entity
@Table( name = "diffs" )
@NamedQuery(
        name="Diff.findUnique",
        query = "select d from Diff d where d.commitid = :commitId AND d.srcfile = :srcFile AND d.dstfile = :dstFile")
public class Diff {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 500)
    private String srcfile;

    @Column(length = 500)
    private String dstfile;

    private long commitid;

    private long projectid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSrcfile() {
        return srcfile;
    }

    public void setSrcfile(String srcfile) {
        this.srcfile = srcfile;
    }

    public String getDstfile() {
        return dstfile;
    }

    public void setDstfile(String dstfile) {
        this.dstfile = dstfile;
    }

    public long getCommitid() {
        return commitid;
    }

    public void setCommitid(long commitid) {
        this.commitid = commitid;
    }

    public long getProjectid() {
        return projectid;
    }

    public void setProjectid(long projectid) {
        this.projectid = projectid;
    }
}
