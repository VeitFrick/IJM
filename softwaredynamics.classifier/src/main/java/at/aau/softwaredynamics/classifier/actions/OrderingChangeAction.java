package at.aau.softwaredynamics.classifier.actions;

public class OrderingChangeAction extends MoveAction {
    public OrderingChangeAction() {
        this.changeTypeID = ORDERING_CHANGE_ACTION;
    }

    public String toHumanReadable() {
        return "Reordered";
    }
}
