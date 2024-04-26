package fr.kinjer.vertxutils.module.request;

import io.vertx.core.http.HttpServerRequest;

public interface IRequestPermission {

    String getName();

    boolean isAuthorized(HttpServerRequest request);

}
