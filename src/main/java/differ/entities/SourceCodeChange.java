package differ.entities;

import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.tree.ITree;
import differ.util.ITreeNodeHelper;
import gen.NodeType;

/**
 * Created by veit on 16.11.2016.
 */
public class SourceCodeChange {
    private Action action;
    private ChangeType changeType = ChangeType.UNCLASSIFIED_CHANGE;

    private String className;
    private String methodName;

    private NodeInfo srcNodeInfo;
    private NodeInfo dstNodeInfo;

    public SourceCodeChange(Action action, NodeInfo srcNodeInfo, NodeInfo dstNodeInfo) {
        this.action = action;
        this.srcNodeInfo = srcNodeInfo;
        this.dstNodeInfo = dstNodeInfo;
    }

    public boolean isUnclassified() {
        return changeType == ChangeType.UNCLASSIFIED_CHANGE;
    }

    public Action getAction() {
        return this.action;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public String getActionType() {
        if (action instanceof Insert)
            return "INS";
        if (action instanceof Delete)
            return "DEL";
        if (action instanceof Update)
            return "UPD";
        if (action instanceof Move)
            return "MOV";

        return "";
    }

    public ITree getNode() { return  this.action.getNode(); }

    public int getNodeType(){
        return this.action.getNode().getType();
    }

    public int getPosition(){
        return this.action.getNode().getPos();
    }

    public int getLength(){
        return this.action.getNode().getLength();
    }

    public ITree getContainingType() {
        return ITreeNodeHelper.getContainingType(this.getNode());
    }

    public String getClassName() {
        if (this.className == null)
            this.className = ITreeNodeHelper.getClassName(this.getContainingType());

        return this.className;
    }

    public ITree getContainingMethod() {
        return ITreeNodeHelper.getContainingMethod(this.getNode());
    }

    public String getMethodName() {
        if (this.methodName == null)
            this.methodName = ITreeNodeHelper.getMethodName(this.getContainingMethod());

        return  this.methodName;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public NodeInfo getSrcInfo() { return this.srcNodeInfo; }
    public NodeInfo getDstInfo() { return this.dstNodeInfo; }

    @Override
    public String toString() {
        return this.changeType+"";
    }

    public String getType(){
        int nodeType = 0;
        if(srcNodeInfo.getNodeType()!=0)
            nodeType=srcNodeInfo.getNodeType();
        else
            nodeType=dstNodeInfo.getNodeType();
        return NodeType.getEnum(nodeType)+"";
    }

    public String additionalInfoString(){
        return "Src: " + srcNodeInfo.getStartLineNumber() + ":" + srcNodeInfo.getEndLineNumber() + ":" + srcNodeInfo.getLabel()+" Dst: " + dstNodeInfo.getStartLineNumber() + ":" + dstNodeInfo.getEndLineNumber() + ":" + dstNodeInfo.getLabel();
        //return "Src: " + srcNodeInfo.getStartLineNumber() + ":" + srcNodeInfo.getEndLineNumber() + " Dst: " + dstNodeInfo.getStartLineNumber() + ":" + dstNodeInfo.getEndLineNumber() +" Class: " + this.className + " Method: " + this.methodName;

    }
}

