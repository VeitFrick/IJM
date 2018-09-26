package runner.io.db.entities;

import javax.persistence.*;

/**
 * Created by thomas on 07.02.2017.
 */
@Entity
@Table(name = "commits")
@NamedQuery(name="Commit.findByCommitHash", query = "select c from Commit c where c.commit = :commit and c.projectId = :projectId")
public class Commit {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 41)
    private String commit;

    @Column(length = 41)
    private String parentCommit;

    private long projectId;

    public long getId() {
        return id;
    }

    public String getCommit() {
        return commit;
    }

    public String getParentCommit() {
        return parentCommit;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public void setParentCommit(String parentCommit) {
        this.parentCommit = parentCommit;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
