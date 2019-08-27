package at.aau.softwaredynamics.classifier.actions;

public class UpdateAction extends ActionType {
    public UpdateAction() {
        this.changeTypeID = UPDATE_ACTION;
    }

    public String toHumanReadable() {
        return "Updated";
    }

    @Override
    public boolean isUpdate() {
        return true;
    }
}
