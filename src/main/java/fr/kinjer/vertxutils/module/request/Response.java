package fr.kinjer.vertxutils.module.request;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

public class Response {

    private final MultiMap params;
    private final JsonObject body;
    private final MultiMap headers;
    private final HttpServerResponse response;

    public Response(MultiMap params, JsonObject body, MultiMap headers, HttpServerResponse response) {
        this.params = params;
        this.body = body;
        this.headers = headers;
        this.response = response;
    }

    public MultiMap params() {
        return params;
    }

    public JsonObject body() {
        return body;
    }

    public MultiMap headers() {
        return headers;
    }

    public HttpServerResponse response() {
        return response;
    }

    @SuppressWarnings("unchecked")
    public static <R extends Response> R create(MultiMap params, JsonObject body, MultiMap headers, HttpServerResponse response) {
        return (R) new Response(params, body, headers, response);
    }
}
