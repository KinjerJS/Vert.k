package fr.kinjer.vertk.verticle;

import fr.kinjer.vertk.VertkServer;
import fr.kinjer.vertk.module.request.*;
import fr.kinjer.vertk.module.request.value.ParamValue;
import fr.kinjer.vertk.request.MethodHttp;
import fr.kinjer.vertk.utils.ConvertorPrimitive;
import fr.kinjer.vertk.utils.ErrorUtil;
import fr.kinjer.vertk.utils.HttpVertxException;
import fr.kinjer.vertk.utils.Pair;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class DefaultVerticle<S extends VertkServer<O>, O, R extends Response> extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultVerticle.class);

    protected final S vertxServer;

    public DefaultVerticle(S vertxServer) {
        this.vertxServer = vertxServer;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOGGER.info("Starting verticle on port {} (http://localhost:{}{})",
                this.vertxServer.getServerPort(), this.vertxServer.getServerPort(), this.vertxServer.getApiPath());

        HttpServer server = this.vertx.createHttpServer();

        server.requestHandler(this::requestHandler);
        this.preInit(server);

        server.listen(this.vertxServer.getServerPort());
    }

    /**
     * This method is called before the server is started. <br>
     * The register of requests handlers is before the execution of this method
     *
     * @param server The http server
     */
    protected void preInit(HttpServer server) {}

    protected void requestHandler(HttpServerRequest request) {
        LOGGER.debug("[Request] ({}) <-", request.path());
        String[] paths = (request.path().startsWith(this.vertxServer.getApiPath())
                ? request.path().substring(this.vertxServer.getApiPath().length())
                : "").split("/");

        Pair<Object, Method> requestModule = this.vertxServer.getModuleManager().getModuleMethod(request, paths);

        if (requestModule != null) {
            try {
                this.executeRequest(request, requestModule.getKey(), requestModule.getValue());
                return;
            } catch (Exception e) {
                LOGGER.error("[Request] A wtf error occurred : {}", e.getMessage(), e);
                request.response().setStatusCode(500).end(ErrorUtil.e("An error occurred"));
                return;
            }
        }
        request.response().setStatusCode(404).end(ErrorUtil.e("Path not found"));

    }

    private void executeRequest(HttpServerRequest request, Object requestModule, Method methodRequest) {
        request.bodyHandler(buffer -> {
            try {
                String result = methodRequest.invoke(requestModule, this.getBindValues(methodRequest, request, buffer))
                        .toString();
                LOGGER.debug("[Response] ({}) -> {}", request.path(), result);
                request.response().setStatusCode(200).end(result);
            } catch (InvocationTargetException | IllegalAccessException e) {
                try {
                    throw e.getCause();
                } catch (HttpVertxException ex) {
                    int code = ex.getCode();
                    request.response().setStatusCode(code).end(ErrorUtil.e(ex.getMessage()));
                } catch (ClassCastException | NumberFormatException ex) {
                    LOGGER.error("[Request] Bad type : {}", e.getMessage(), e);
                    request.response().setStatusCode(400).end(ErrorUtil.e("BAD_TYPE"));
                } catch (Throwable ex) {
                    LOGGER.error("[Request] An error occurred : {}", e.getMessage(), e);
                    request.response().setStatusCode(500).end(ErrorUtil.e("An error occurred"));
                }
            }
        });
    }

    protected R createResponse(HttpServerRequest request, Buffer buffer) {
        return Response.create(request.params(), buffer.length() > 0
                ? buffer.toJsonObject() : new JsonObject(), request.headers(), request.response());
    }

    private Object[] getBindValues(Method met, HttpServerRequest request, Buffer buffer) {
        List<Object> params = new ArrayList<>();
        for (Parameter parameterType : met.getParameters()) {
            Class<?> classType = parameterType.getType();
            Object valueTypeClass = this.getTypedValue(classType, request, buffer);
            if (valueTypeClass != null) {
                params.add(valueTypeClass);
                continue;
            }
            Pair<String, ParamValue> param = this.filterParam(parameterType,
                    MethodHttp.fromHttpMethod(request.method()), buffer, request.params());
            params.add(
                    ConvertorPrimitive.convert(classType,
                            param.getKey() != null ? param.getKey() : this.getParamValue(param.getValue()))
            );
        }
        return params.toArray();
    }

    private String getParamValue(ParamValue value) {
        return value != null ? "" + switch (value.typeValue()) {
            case INTEGER -> value.intValue();
            case LONG -> value.longValue();
            case FLOAT -> value.floatValue();
            case DOUBLE -> value.doubleValue();
            case BOOLEAN -> value.booleanValue();
            default -> value.stringValue();
        } : null;
    }

    private Object getTypedValue(Class<?> classType, HttpServerRequest request, Buffer buffer) {
        if (Response.class.isAssignableFrom(classType)) {
            return this.createResponse(request, buffer);
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
        if (classType.isEnum()) {
            return request;
        }
        return null;
    }

    /**
     * Return the value by the parameter type and the method http
     * or if the parameter has {@link Param} or {@link Body} annotation
     *
     * @param parameterType The parameter type
     * @param methodHttp The method http
     * @param body The body of the request
     * @param param The parameters of the request
     * @return The value of the parameter
     */
    private Pair<String, ParamValue> filterParam(Parameter parameterType, MethodHttp methodHttp, Buffer body, MultiMap param) {

        if (parameterType.isAnnotationPresent(Body.class) || methodHttp == MethodHttp.POST) {
            Body paramBody = parameterType.getAnnotation(Body.class);
            if (body.length() > 0) {
                String key = paramBody != null && !paramBody.value().isEmpty()
                        ? paramBody.value()
                        : parameterType.getName();
                return new Pair<>(body.toJsonObject().getString(key), paramBody != null ? paramBody.defaultValue() : null);
            }
        }
        Param paramAKey = parameterType.getAnnotation(Param.class);
        String paramKey = paramAKey != null && !paramAKey.value().isEmpty()
                ? paramAKey.value() : parameterType.getName();
        return new Pair<>(param.get(paramKey), paramAKey != null ? paramAKey.defaultValue() : null);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
    }
}
