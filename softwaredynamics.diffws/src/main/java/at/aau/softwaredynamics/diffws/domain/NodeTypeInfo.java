package at.aau.softwaredynamics.diffws.domain;

import at.aau.softwaredynamics.gen.NodeType;

public class NodeTypeInfo {
    private Integer id;
    private String name;

    public NodeTypeInfo(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public NodeTypeInfo(NodeType nt) {
        this.id = nt.getValue();
        this.name = NodeType.getEnum(nt.getValue()).toString();
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
