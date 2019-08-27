package at.aau.softwaredynamics.util;

import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

public class LabelHelper {
    public static String getTypeLabel(Type type) {
        if (type instanceof ParameterizedType)
            return getTypeLabel((ParameterizedType) type);
        if (type instanceof SimpleType)
            return getTypeLabel((SimpleType) type);
        if (type instanceof PrimitiveType)
            return getTypeLabel((PrimitiveType) type);

        return null;
    }

    private static String getTypeLabel(ParameterizedType type) {
        String retVal = type.getType().toString() + "<";

        String[] typeArguments = new String[type.typeArguments().size()];

        for(int i = 0; i < type.typeArguments().size(); i++)
            typeArguments[i] = getTypeLabel((Type)type.typeArguments().get(i));

        retVal+= String.join(",", typeArguments);
        retVal+= ">";

        return retVal;
    }

    private static String getTypeLabel(SimpleType type) {
        return type.toString();
    }

    private static String getTypeLabel(PrimitiveType type) {
        return type.toString();
    }
}
