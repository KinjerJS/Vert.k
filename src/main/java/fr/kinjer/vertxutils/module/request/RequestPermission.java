package fr.kinjer.vertxutils.module.request;

import fr.kinjer.vertxutils.manager.VertxManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestPermission {

    /**
     * The name of the permission stored in the {@link VertxManager}
     *
     * @return the name of the permission
     */
    String value();

}
