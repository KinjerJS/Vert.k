package fr.kinjer.vertxutils.utils;

import io.vertx.core.VertxException;

public class HttpVertxException extends VertxException {

    private final int code;

    public HttpVertxException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
