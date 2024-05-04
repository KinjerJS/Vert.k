package fr.kinjer.vertxutils.module.request;

import fr.kinjer.vertxutils.module.request.value.ParamValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    String value() default "";

    ParamValue defaultValue() default @ParamValue(typeValue = ParamValue.Type.NONE);

}
