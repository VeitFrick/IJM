package at.aau.softwaredynamics.dependency;

/**
 * Represents a strength change in a dependency on a specific Class
 * ie. From one revision to another the dependency from a class to another one went up/down or
 * stayed the same.
 */
public class ClassDependencyStrengthChange {
    private String className;
    private Integer dstStrength;
    private Integer deltaStrength;
    private boolean addedDependency = false;
    private boolean deletedDependency = false;

    public ClassDependencyStrengthChange(String className, Integer dstStrength, Integer deltaStrength) {
        this.className = className;
        this.dstStrength = dstStrength;
        this.deltaStrength = deltaStrength;
    }

    public String getClassName() {
        return className;
    }

    public Integer getDstStrength() {
        return dstStrength;
    }

    public Integer getDeltaStrength() {
        return deltaStrength;
    }

    public boolean isAddedDependency() {
        return addedDependency;
    }

    public void setAddedDependency(boolean addedDependency) {
        this.addedDependency = addedDependency;
    }

    public boolean isDeletedDependency() {
        return deletedDependency;
    }

    public void setDeletedDependency(boolean deletedDependency) {
        this.deletedDependency = deletedDependency;
    }

    public char getDeltaSymbol(){
        if(deltaStrength>0){
            return '+';
        } else if(deltaStrength<0){
            return '-';
        } else{
            return '=';
        }
    }

    @Override
    public String toString() {
        String retString = getDeltaSymbol() + " " + className + "(" + dstStrength + ") " + deltaStrength;
        if(this.isDeletedDependency()){
            retString += " [-]";
        } else if(this.isAddedDependency()){
            retString += " [+]";
        }
        return retString;
    }
}
