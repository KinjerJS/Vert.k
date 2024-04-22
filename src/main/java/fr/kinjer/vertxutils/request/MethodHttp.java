package fr.kinjer.vertxutils.request;

import io.vertx.core.http.HttpMethod;

public enum MethodHttp {
    GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH;

    public static MethodHttp fromHttpMethod(HttpMethod method) {
        return MethodHttp.valueOf(method.name());
    }
}
