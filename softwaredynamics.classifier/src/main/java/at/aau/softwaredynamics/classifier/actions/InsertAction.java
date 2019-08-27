package at.aau.softwaredynamics.classifier.actions;

public class InsertAction extends ActionType {
    public InsertAction() {
        this.changeTypeID = INSERT_ACTION;
    }

    public String toHumanReadable() {
        return "Inserted";
    }
}
