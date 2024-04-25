package fr.kinjer.vertxutils.module.request;

import fr.kinjer.vertxutils.request.MethodHttp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If the method is annotated with this annotation,
 * it will be called in priority and cancel the call to a class which is annotated by the same value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubRequest {

    String value();

    String parent() default "";

    MethodHttp method() default MethodHttp.GET;

}
