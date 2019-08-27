package at.aau.softwaredynamics.classifier.actions;

public class ParentChangeAction extends MoveAction {

    public ParentChangeAction() {
        this.changeTypeID = PARENT_CHANGE_ACTION;
    }

    public String toHumanReadable() {
        return "Changed parent of";
    }
}
