package at.aau.softwaredynamics.diffws.util;

import org.apache.commons.lang3.StringUtils;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpoonMetadataScraper {

    private CtElement spoonNode;
    public SpoonMetadataScraper(CtElement spoonNode) {
        this.spoonNode = spoonNode;
    }

    public Map<String, String> generateMetadataMap() {


        Map<String, String> map = new HashMap<>();
        if (this.spoonNode == null) return map;
        Class<? extends CtElement> nodeClass = this.spoonNode.getClass();
        map.put("type", nodeClass.toString().replaceFirst("class spoon\\.support.reflect\\.", ""));
            if (spoonNode instanceof CtClass) {
                CtClass classNode = (CtClass) spoonNode;
            }
            if (spoonNode instanceof CtType) {
                CtType ctType = (CtType) spoonNode;
//                if(ctType.getActualClass() != null) {
//                    map.put("class", ctType.getActualClass().toString());
//                }

                if(ctType.getFields() != null) map.put("fields", StringUtils.join(ctType.getFields(), "\n---\n"));
                if(ctType.getMethods() != null) {
                    List<String> methodSignatures = new ArrayList<>();
                    for (Object method : ctType.getMethods()) {
                        methodSignatures.add(((CtMethod)method).getSignature()); //TODO maybe add more information like return value, modifiers...
                    }

                    map.put("methods", StringUtils.join(methodSignatures, "\n---\n"));
                }
            }
            if (spoonNode instanceof CtNamedElement) {
                CtNamedElement namedElement = (CtNamedElement) spoonNode;
                if(namedElement != null)  map.put("simpleName", namedElement.getSimpleName());
            } //CtStatement
            if (spoonNode instanceof CtStatement) {
                CtStatement ctStatement = (CtStatement) spoonNode;
                if(ctStatement.getLabel() != null) map.put("label", ctStatement.getLabel());
            }
            if (spoonNode instanceof CtLiteral) {
                CtLiteral ctLiteral = (CtLiteral) spoonNode;
                if(ctLiteral.getValue() != null) map.put("literalValue", ctLiteral.getValue().toString());
            }
            if (spoonNode instanceof CtTypeMember) {
                CtTypeMember ctTypeMember = (CtTypeMember) spoonNode;
                if(ctTypeMember.getDeclaringType() != null && ctTypeMember.getDeclaringType().getPackage() != null)
                    map.put("memberOfPackage", ctTypeMember.getDeclaringType().getPackage().toString());
                if(ctTypeMember.getDeclaringType() != null)
                    map.put("memberOf", ctTypeMember.getDeclaringType().getQualifiedName());
            }
            if(spoonNode != null)
            {
                map.put("stringified", spoonNode.toString());
            }


        return map;
    }
}
