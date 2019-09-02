package at.aau.softwaredynamics.dependency.meta;

/**
 * Node of a dependency
 * ONLY USED FOR VISUALIZATIONS
 */
public class Node {

    String name;
    Integer size;

    public Node(String name) {
        this(name, 1);
    }

    public Node(String name, Integer size) {
        this.name = name;
        this.size = size;
    }

    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Node)) return false;
        Node otherNode = (Node) other;
        return otherNode.getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
