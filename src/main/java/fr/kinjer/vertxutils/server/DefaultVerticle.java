package fr.kinjer.vertxutils.server;

import fr.kinjer.vertxutils.VertxServer;
import fr.kinjer.vertxutils.module.request.IRequest;
import fr.kinjer.vertxutils.module.request.Response;
import fr.kinjer.vertxutils.request.MethodHttp;
import fr.kinjer.vertxutils.utils.ErrorUtil;
import fr.kinjer.vertxutils.utils.HttpVertxException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.function.Consumer;

public class DefaultVerticle<T extends VertxServer, R extends IRequest<Re>, Re extends Response> extends AbstractVerticle {

    protected final T vertxServer;
    private Consumer<HttpServer> preInitServer = (__) -> {};

    public DefaultVerticle(T vertxServer) {
        this.vertxServer = vertxServer;
    }

    public DefaultVerticle<T, R, Re> withPreInitServer(Consumer<HttpServer> preInitServer) {
        this.preInitServer = preInitServer;
        return this;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        System.out.println("Starting verticle on port " + this.vertxServer.getServerPort() + " (http://localhost:" + this.vertxServer.getServerPort() + this.vertxServer.getApiPath() + ")");

        HttpServer server = this.vertx.createHttpServer();

        server.requestHandler(this::requestHandler);
        preInitServer.accept(server);

        server.listen(this.vertxServer.getServerPort());
    }

    @SuppressWarnings("unchecked")
    private void requestHandler(HttpServerRequest request) {
        MethodHttp method = MethodHttp.fromHttpMethod(request.method());
        String[] paths = (request.path().startsWith(this.vertxServer.getApiPath())
                ? request.path().substring(this.vertxServer.getApiPath().length())
                : "").split("/");

        R requestModule = (R) this.vertxServer.getModuleManager().getModule(paths[0]);

        if (requestModule == null) {
            request.response().setStatusCode(404).end(ErrorUtil.e404("Path not found"));
            return;
        }

        try {
            switch (method) {
                case HEAD, GET -> {
                    try {
                        Re response = (Re) Response.create(request.params(), new JsonObject(), request.headers(), request.response());
                        this.checkRequest(paths, requestModule, request, response);
                    } catch (HttpVertxException e) {
                        int code = e.getCode();

                        request.response().setStatusCode(code).end(ErrorUtil.e(code, e.getMessage()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.response().setStatusCode(500).end(ErrorUtil.e500("An error occurred"));
                    }
                }
                default -> {
                    request.bodyHandler(buffer -> {
                        try {
                            JsonObject body = buffer.length() == 0 ? new JsonObject() : buffer.toJsonObject();
                            Re response = Response.create(request.params(), body, request.headers(), request.response());
                            this.checkRequest(paths, requestModule, request, response);
                        } catch (HttpVertxException e) {
                            int code = e.getCode();

                            request.response().setStatusCode(code).end(ErrorUtil.e(code, e.getMessage()));
                        } catch (Exception e) {
                            request.response().setStatusCode(500).end(ErrorUtil.e500("An error occurred, maybe bad body ?"));
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.response().setStatusCode(500).end(ErrorUtil.e500("An error occurred"));
        }

    }

    private void checkRequest(String[] paths, R requestModule, HttpServerRequest httpServerRequest, Re response) {
        if (paths.length == 1) {
            try {
                if(requestModule.isAuthorized(response)) {
                    if(!httpServerRequest.response().ended()) {
                        httpServerRequest.response().setStatusCode(200).end(requestModule.onRequest(response));
                    }
                    return;
                }
                if(!httpServerRequest.response().ended()) {
                    httpServerRequest.response().setStatusCode(401).end(ErrorUtil.e401("Unauthorized"));
                }
            }catch (HttpVertxException e) {
                int code = e.getCode();

                if(!httpServerRequest.response().ended()) {
                    httpServerRequest.response().setStatusCode(code).end(ErrorUtil.e(code, e.getMessage()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        R request = requestModule.getSubRequest(paths[1]);
        R subRequest = this.getSubRequest(request, Arrays.copyOfRange(paths, 2, paths.length));

        if (subRequest == null) {
            if(!httpServerRequest.response().ended()) {
                httpServerRequest.response().setStatusCode(404).end(ErrorUtil.e404("Path not found"));
            }
            return;
        }
        try {
            if(!subRequest.isAuthorized(response)) {
                if(!httpServerRequest.response().ended()) {
                    httpServerRequest.response().setStatusCode(401).end(ErrorUtil.e401("Unauthorized"));
                }
                return;
            }
            if(!httpServerRequest.response().ended()) {
                httpServerRequest.response().setStatusCode(200).end(subRequest.onRequest(response));
            }
        } catch (HttpVertxException e) {
            int code = e.getCode();

            if(!httpServerRequest.response().ended()) {
                httpServerRequest.response().setStatusCode(code).end(ErrorUtil.e(code, e.getMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(!httpServerRequest.response().ended()) {
                httpServerRequest.response().setStatusCode(500).end(ErrorUtil.e500("An error occurred"));
            }
        }
    }

    private <U extends IRequest<Re>> U getSubRequest(U request, String[] paths) {
        if(request == null) {
            return null;
        }
        if (paths.length == 0) {
            return request;
        }
        for (U subRequest : request.<U>getSubRequests()) {
            if (subRequest.getPath().equals(paths[0])) {
                return getSubRequest(subRequest, Arrays.copyOfRange(paths, 1, paths.length));
            }
        }
        return null;
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
    }
}
