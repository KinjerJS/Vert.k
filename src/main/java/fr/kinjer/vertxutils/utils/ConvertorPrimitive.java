package fr.kinjer.vertxutils.utils;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class ConvertorPrimitive {

    /**
     * Convert a string to a primitive number type
     *
     * @param targetType
     * @param text
     * @return
     * @throws NumberFormatException if the string does not contain a parsable number
     */
    public static Object convert(Class<?> targetType, String text) {
        try {
            PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
            editor.setAsText(text);
            return editor.getValue();
        } catch (Exception e) {
            System.err.println(targetType.getName() + ":" + text);
            throw new RuntimeException(e);
        }
    }

}
