package fr.kinjer.vertxutils.utils;

import io.vertx.core.json.JsonObject;

public class ErrorUtil {

    public static String e404() {
        return e404("Not found");
    }

    public static String e404(String message) {
        return e(404, message);
    }

    public static String e400() {
        return e400("Bad request");
    }

    public static String e400(String message) {
        return e(400, message);
    }

    public static String e500() {
        return e500("Internal server error");
    }

    public static String e500(String message) {
        return e(500, message);
    }

    public static String e401() {
        return e401("Unauthorized");
    }

    public static String e401(String message) {
        return e(401, message);
    }

    public static String e403() {
        return e403("Forbidden");
    }

    public static String e403(String message) {
        return e(403, message);
    }

    public static String e405() {
        return e405("Method not allowed");
    }

    public static String e405(String message) {
        return e(405, message);
    }

    public static String e(int code, String message) {
        return new JsonObject().put("code", code).put("message", message).encode();
    }
}
