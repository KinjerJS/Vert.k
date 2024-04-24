package fr.kinjer.vertxutils.module.request;

import fr.kinjer.vertxutils.request.MethodHttp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SubRequest {

    String value();

    String parent() default "";

    MethodHttp method() default MethodHttp.GET;

}
