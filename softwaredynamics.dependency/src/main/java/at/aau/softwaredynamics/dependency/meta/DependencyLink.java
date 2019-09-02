package at.aau.softwaredynamics.dependency.meta;

public class DependencyLink {
    String sourceClass;
    String dependentOnClass;
    Integer strength;
    Integer delta;

    String sourceClassShort;
    String dependentOnClassShort;

    public DependencyLink(String sourceClass, String dependentOnClass, Integer strength, Integer delta) {
        this.sourceClass = sourceClass;
        this.dependentOnClass = dependentOnClass;
        this.strength = strength;
        this.delta = delta;
    }

    public String getSource() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    public String getTarget() {
        return dependentOnClass;
    }

    public void setDependentOnClass(String dependentOnClass) {
        this.dependentOnClass = dependentOnClass;
    }

    public Integer getStrength() {
        return strength;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public Integer getDelta() {
        return delta;
    }

    public boolean hasChanged() {
        return delta != 0;
    }
}
