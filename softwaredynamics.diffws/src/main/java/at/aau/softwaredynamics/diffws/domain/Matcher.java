package at.aau.softwaredynamics.diffws.domain;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("matchers")
public class Matcher {

    private Integer id;
    private String name;

    public Matcher(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
