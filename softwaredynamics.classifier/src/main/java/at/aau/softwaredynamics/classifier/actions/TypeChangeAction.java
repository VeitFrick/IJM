package at.aau.softwaredynamics.classifier.actions;

public class TypeChangeAction extends UpdateAction {
    public TypeChangeAction() {
        this.changeTypeID = TYPE_CHANGE_ACTION;
    }

    public String toHumanReadable() {
        return "Changed type of";
    }
}
