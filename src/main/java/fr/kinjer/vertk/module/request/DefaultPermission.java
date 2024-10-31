package fr.kinjer.vertk.module.request;

import io.vertx.core.http.HttpServerRequest;

public class DefaultPermission implements IRequestPermission {
    @Override
    public String getName() {
        return "default";
    }

    @Override
    public boolean isAuthorized(HttpServerRequest request) {
        return true;
    }
}
