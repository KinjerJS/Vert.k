package fr.kinjer.vertxutils.module.request;

import fr.kinjer.vertxutils.request.MethodHttp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Request {

    MethodHttp method() default MethodHttp.GET;

    String value();

}
