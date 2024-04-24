package fr.kinjer.vertxutils.utils;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class ConvertorPrimitive {

    public static Object convert(Class<?> targetType, String text) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return editor.getValue();
    }

}
