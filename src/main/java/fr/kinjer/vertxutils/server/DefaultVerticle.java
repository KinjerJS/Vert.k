package fr.kinjer.vertxutils.server;

import fr.kinjer.vertxutils.VertxServer;
import fr.kinjer.vertxutils.module.request.*;
import fr.kinjer.vertxutils.request.MethodHttp;
import fr.kinjer.vertxutils.utils.ConvertorPrimitive;
import fr.kinjer.vertxutils.utils.ErrorUtil;
import fr.kinjer.vertxutils.utils.HttpVertxException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class DefaultVerticle<T extends VertxServer<O>, O> extends AbstractVerticle {

    protected final T vertxServer;

    public DefaultVerticle(T vertxServer) {
        this.vertxServer = vertxServer;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        System.out.println("Starting verticle on port " + this.vertxServer.getServerPort() + " (http://localhost:" + this.vertxServer.getServerPort() + this.vertxServer.getApiPath() + ")");

        HttpServer server = this.vertx.createHttpServer();

        server.requestHandler(this::requestHandler);
        this.preInit(server);

        server.listen(this.vertxServer.getServerPort());
    }

    /**
     * This method is called before the server is started. <br>
     * Register request handlers is before the execution of this method
     *
     * @param server The http server
     */
    protected void preInit(HttpServer server) {}

    private void requestHandler(HttpServerRequest request) {
        System.out.println("Request");
        MethodHttp method = MethodHttp.fromHttpMethod(request.method());
        String[] paths = (request.path().startsWith(this.vertxServer.getApiPath())
                ? request.path().substring(this.vertxServer.getApiPath().length())
                : "").split("/");

        Object requestModule = this.vertxServer.getModuleManager().getModule(paths[0]);

        if (requestModule != null) {
            try {
                //TODO Cant work, need to find a way to get the subrequest with "root/subrequest/subsubrequest/sameNameSubRequest"
                Class<?> requestModuleClass = requestModule.getClass();
                if (requestModuleClass.isAnnotationPresent(SubRequest.class)) {
                    String subRequest = paths.length > 1 ? paths[1] : null;
                    SubRequest subRequestAnnotation = requestModuleClass.getAnnotation(SubRequest.class);
                    if (subRequestAnnotation.method() == method && subRequestAnnotation.value().equals(subRequest)) {
                        this.executeRequest(request, requestModule, paths, subRequest, method);
                    }
                }
                this.executeRequest(request, requestModule, paths, paths.length > 1 ? paths[1] : null, method);
            } catch (Exception e) {
                e.printStackTrace();
                request.response().setStatusCode(500).end(ErrorUtil.e500("An error occurred"));
                return;
            }
        }
        request.response().setStatusCode(404).end(ErrorUtil.e404("Path not found"));

    }

    private void executeRequest(HttpServerRequest request, Object requestModule, String[] paths, String subRequest, MethodHttp method) {
        for (Method met : requestModule.getClass().getDeclaredMethods()) {
            System.out.println(met.getName());
            if(this.isRequest(met, paths, method) || this.isSubRequest(met, subRequest, method)) {
                request.bodyHandler(buffer -> {
                    try {
                        List<Object> params = this.getBindValues(met, request, buffer);
                        String result = met.invoke(requestModule, params.toArray()).toString();
                        request.response().setStatusCode(200).end(result);
                    } catch (HttpVertxException e) {
                        int code = e.getCode();
                        request.response().setStatusCode(code).end(ErrorUtil.e(code, e.getMessage()));
                    } catch (ClassCastException | NumberFormatException e) {
                        e.printStackTrace();
                        request.response().setStatusCode(400).end(ErrorUtil.e(400, "BAD_TYPE"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.response().setStatusCode(500).end(ErrorUtil.e500("An error occurred"));
                    }
                });
                return;
            }
        }
    }

    private boolean isSubRequest(Method met, String subRequest, MethodHttp method) {
        return met.isAnnotationPresent(SubRequest.class)
                && met.getAnnotation(SubRequest.class).method() == method
                && met.getAnnotation(SubRequest.class).value().equals(subRequest);
    }

    private boolean isRequest(Method met, String[] paths, MethodHttp method) {
        return paths.length == 1 && met.isAnnotationPresent(Request.class)
                && met.getAnnotation(Request.class).method() == method;
    }

    private List<Object> getBindValues(Method met, HttpServerRequest request, Buffer buffer) {
        List<Object> params = new ArrayList<>();
        for (Parameter parameterType : met.getParameters()) {
            Class<?> classType = parameterType.getType();
            Object valueTypeClass = this.getTypedValue(classType, request, buffer);
            if (valueTypeClass != null) {
                params.add(valueTypeClass);
                continue;
            }
            params.add(ConvertorPrimitive.convert(classType, this.filterParam(parameterType, buffer, request.params())));
        }
        return params;
    }

    private Object getTypedValue(Class<?> classType, HttpServerRequest request, Buffer buffer) {
        if (classType == Response.class) {
            return Response.create(request.params(), buffer.length() > 0
                    ? buffer.toJsonObject() : new JsonObject(), request.headers(), request.response());
        }
        if (classType == Buffer.class) {
            return buffer;
        }
        if (classType == MultiMap.class) {
            return request.params();
        }
        if (classType == JsonObject.class) {
            return buffer.length() > 0 ? buffer.toJsonObject() : new JsonObject();
        }
        return null;
    }

    private String filterParam(Parameter parameterType, Buffer body, MultiMap param) {
        if (parameterType.isAnnotationPresent(Body.class)) {
            Body paramBody = parameterType.getAnnotation(Body.class);
            if (body.length() > 0) {
                return body.toJsonObject().getString(
                        !paramBody.value().isEmpty()
                        ? paramBody.value()
                        : parameterType.getName());
            }
        }
        Param paramAKey = parameterType.getAnnotation(Param.class);
        String paramKey = parameterType.isAnnotationPresent(Param.class)
                && !paramAKey.value().isEmpty() ? paramAKey.value() : parameterType.getName();
        return param.get(paramKey);
    }

//    private void checkRequest(String[] paths, R requestModule, HttpServerRequest httpServerRequest, Re response) {
//        if (paths.length == 1) {
//            try {
//                if(!httpServerRequest.response().ended()) {
//                    if(requestModule.isAuthorized(response)) {
//                        httpServerRequest.response().setStatusCode(200).end(this.onRequest(response, requestModule));
//                        return;
//                    }
//                    httpServerRequest.response().setStatusCode(401).end(ErrorUtil.e401("Unauthorized"));
//                }
//            }catch (HttpVertxException e) {
//                int code = e.getCode();
//
//                if(!httpServerRequest.response().ended()) {
//                    httpServerRequest.response().setStatusCode(code).end(ErrorUtil.e(code, e.getMessage()));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return;
//        }
//        R request = requestModule.getSubRequest(paths[1]);
//        R subRequest = this.getSubRequest(request, Arrays.copyOfRange(paths, 2, paths.length));
//
//        if (subRequest == null) {
//            if(!httpServerRequest.response().ended()) {
//                httpServerRequest.response().setStatusCode(404).end(ErrorUtil.e404("Path not found"));
//            }
//            return;
//        }
//        try {
//            if(!subRequest.isAuthorized(response)) {
//                if(!httpServerRequest.response().ended()) {
//                    httpServerRequest.response().setStatusCode(401).end(ErrorUtil.e401("Unauthorized"));
//                }
//                return;
//            }
//            if(!httpServerRequest.response().ended()) {
//                httpServerRequest.response().setStatusCode(200).end(this.onRequest(response, subRequest));
//            }
//        } catch (HttpVertxException e) {
//            int code = e.getCode();
//
//            if(!httpServerRequest.response().ended()) {
//                httpServerRequest.response().setStatusCode(code).end(ErrorUtil.e(code, e.getMessage()));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            if(!httpServerRequest.response().ended()) {
//                httpServerRequest.response().setStatusCode(500).end(ErrorUtil.e500("An error occurred"));
//            }
//        }
//    }
//
//    private String onRequest(Re response, R request) throws Exception {
//
//        return request.onRequest(response);
//    }

//    private R getSubRequest(R request, String[] paths) {
//        if(request == null) {
//            return null;
//        }
//        if (paths.length == 0) {
//            return request;
//        }
//        for (R subRequest : request.<R>getSubRequests()) {
//            if (subRequest.getPath().equals(paths[0])) {
//                return getSubRequest(subRequest, Arrays.copyOfRange(paths, 1, paths.length));
//            }
//        }
//        return null;
//    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
    }
}
