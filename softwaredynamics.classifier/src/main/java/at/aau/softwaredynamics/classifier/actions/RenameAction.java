package at.aau.softwaredynamics.classifier.actions;

public class RenameAction extends UpdateAction {
    public RenameAction() {
        this.changeTypeID = RENAME_ACTION;
    }

    public String toHumanReadable() {
        return "Renamed";
    }
}
