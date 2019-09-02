package at.aau.softwaredynamics.dependency;

/**
 * Holds information about a single dependency:
 * - On which class does it depend (dependentOnClass)
 * - Where does the dependency originate (fullyQualifiedName)
 * - Which type of dependency is it (CALL, READ, WRITE, INHERITANCE...) (type)
 * - Does it depend on itself (selfDependency)
 */
public class Dependency {

    private String dependentOnClass;
    private String fullyQualifiedName;
    private DependencyType type;
    private boolean selfDependency;

    public Dependency(String fullyQualifiedName, DependencyType type, String dependentOnClass, boolean selfDependency) {
        this.dependentOnClass = dependentOnClass;
        this.fullyQualifiedName = fullyQualifiedName;
        this.type = type;
        this.selfDependency = selfDependency;
    }

    public Dependency(String fullyQualifiedName, DependencyType type, String dependentOnClass) {
        this(fullyQualifiedName, type, dependentOnClass, false);
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public DependencyType getType() {
        return type;
    }

    public void setType(DependencyType type) {
        this.type = type;
    }

    public String getDependentOnClass() {
        return dependentOnClass;
    }

    public void setDependentOnClass(String dependentOnClass) {
        this.dependentOnClass = dependentOnClass;
    }

    public boolean getSelfDependency() {
        return selfDependency;
    }

    public void setSelfDependency(boolean selfDependency) {
        this.selfDependency = selfDependency;
    }

    @Override
    public String toString() {
        String self = selfDependency ? "<SELF> " : "";
        return self + "[" + this.getType().toString() + "] " + getDependentOnClass() + "  " + getFullyQualifiedName();
    }

    public String toOutputString(String separator) {
        return this.getType().toString() + separator + getDependentOnClass() + separator + getFullyQualifiedName() + separator + this.selfDependency;
    }
}
