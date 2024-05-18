package fr.kinjer.vertxutils.utils;

import io.vertx.core.json.JsonObject;

public class ErrorUtil {

    public static String e(String message) {
        return new JsonObject().put("error", message).encode();
    }

}
