package at.aau.softwaredynamics.runner.io.db.entities;

import javax.persistence.*;

/**
 * Created by thomas on 07.02.2017.
 */
@Entity
@Table( name = "matchers")
@NamedQuery(name="Matcher.findByName", query = "select m from Matcher m where m.name = :name")
public class Matcher {
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
