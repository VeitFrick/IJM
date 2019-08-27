package at.aau.softwaredynamics.classifier.actions;

public class MoveAction extends ActionType {
    public MoveAction() {
        this.changeTypeID = MOVE_ACTION;
    }

    public String toHumanReadable() {
        return "Moved";
    }

    @Override
    public boolean isMove() {
        return true;
    }

}
