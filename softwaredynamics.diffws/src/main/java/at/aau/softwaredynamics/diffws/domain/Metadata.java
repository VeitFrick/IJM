package at.aau.softwaredynamics.diffws.domain;

import java.util.Map;

public class Metadata {
    private NodeTypeInfo nodeType;
    private String containingMethodSrc;
    private String containingClassSrc;
    private String containingMethodDst;
    private String containingClassDst;
    private String changeType;
    private String childrenChangeInfo;
    private String parentChangeInfo;
    private String labelSrc;
    private String labelDst;

    private String dependencies;
    private Map<String, String> spoonSrc;

    public Map<String, String> getSpoonSrc() {
        return spoonSrc;
    }

    public void setSpoonSrc(Map<String, String> spoonSrc) {
        this.spoonSrc = spoonSrc;
    }

    public Map<String, String> getSpoonDst() {
        return spoonDst;
    }

    public void setSpoonDst(Map<String, String> spoonDst) {
        this.spoonDst = spoonDst;
    }

    private Map<String, String> spoonDst;

    public Metadata() {

    }

    public Metadata(NodeTypeInfo nodeType, String containingMethodSrc, String containingClassSrc, String containingMethodDst, String containingClassDst) {
        this.nodeType = nodeType;
        this.containingMethodSrc = containingMethodSrc;
        this.containingClassSrc = containingClassSrc;
        this.containingMethodDst = containingMethodDst;
        this.containingClassDst = containingClassDst;
    }

    public NodeTypeInfo getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeTypeInfo nodeType) {
        this.nodeType = nodeType;
    }

    public String getContainingMethodSrc() {
        return containingMethodSrc;
    }

    public void setContainingMethodSrc(String containingMethodSrc) {
        this.containingMethodSrc = containingMethodSrc;
    }

    public String getContainingClassSrc() {
        return containingClassSrc;
    }

    public void setContainingClassSrc(String containingClassSrc) {
        this.containingClassSrc = containingClassSrc;
    }

    public String getContainingMethodDst() {
        return containingMethodDst;
    }

    public void setContainingMethodDst(String containingMethodDst) {
        this.containingMethodDst = containingMethodDst;
    }

    public String getContainingClassDst() {
        return containingClassDst;
    }

    public void setContainingClassDst(String containingClassDst) {
        this.containingClassDst = containingClassDst;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getChildrenChangeInfo() {
        return childrenChangeInfo;
    }

    public void setChildrenChangeInfo(String childrenChangeInfo) {
        this.childrenChangeInfo = childrenChangeInfo;
    }

    public String getParentChangeInfo() {
        return parentChangeInfo;
    }

    public void setParentChangeInfo(String parentChangeInfo) {
        this.parentChangeInfo = parentChangeInfo;
    }

    public String getLabelSrc() {
        return labelSrc;
    }

    public void setLabelSrc(String labelSrc) {
        this.labelSrc = labelSrc;
    }

    public String getLabelDst() {
        return labelDst;
    }

    public void setLabelDst(String labelDst) {
        this.labelDst = labelDst;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }
}
