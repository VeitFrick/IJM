package at.aau.softwaredynamics.classifier.actions;

public class ActionType {


    public static final int NO_ACTION = 0;
    public static final int DELETE_ACTION = 1;
    public static final int INSERT_ACTION = 2;
    public static final int UPDATE_ACTION = 3;
    public static final int MOVE_ACTION = 4;
    public static final int ORDERING_CHANGE_ACTION = 5;
    public static final int PARENT_CHANGE_ACTION = 6;
    public static final int RENAME_ACTION = 7;
    public static final int TYPE_CHANGE_ACTION = 8;

    public int changeTypeID;

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public ActionType() {
        this.changeTypeID = NO_ACTION;
    }

    public boolean isOfExactType(int type){
        return type == this.changeTypeID;
    }

    public boolean isNoAction(){
        return (NO_ACTION == this.changeTypeID);
    }

    public boolean isUpdate() {
        return false;
    }

    public boolean isMove() {
        return false;
    }

    public String toHumanReadable() {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ActionType) || obj == null)
            return false;
        ActionType type = (ActionType) obj;
        return type.changeTypeID == this.changeTypeID;
    }
}
