package fr.kinjer.vertxutils.module.request;

import fr.kinjer.vertxutils.request.MethodHttp;

public @interface SubRequest {

    String value();

    MethodHttp method() default MethodHttp.GET;

}
