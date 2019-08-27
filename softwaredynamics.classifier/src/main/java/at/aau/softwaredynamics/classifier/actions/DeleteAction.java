package at.aau.softwaredynamics.classifier.actions;

public class DeleteAction extends ActionType {
    public DeleteAction() {
        this.changeTypeID = DELETE_ACTION;
    }

    public String toHumanReadable() {
        return "Deleted";
    }
}
