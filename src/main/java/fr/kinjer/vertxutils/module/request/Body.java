package fr.kinjer.vertxutils.module.request;

import fr.kinjer.vertxutils.module.request.value.ParamValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Body {

    /**
     * The real key name get from a body in jso
     * @return
     */
    String value() default "";

    ParamValue defaultValue();

}
