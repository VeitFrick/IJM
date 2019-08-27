package at.aau.softwaredynamics.runner.io.db.entities;

import javax.persistence.*;

/**
 * Created by thomas on 07.02.2017.
 */
@Entity
@Table(name = "projects")
@NamedQuery(name="Project.findByName", query = "select p from Project p where p.name = :name")
public class Project {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 255, unique = true)
    private String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
