package fr.kinjer.vertxutils.module;

import fr.kinjer.vertxutils.VertxServer;
import fr.kinjer.vertxutils.module.request.*;
import fr.kinjer.vertxutils.request.MethodHttp;
import fr.kinjer.vertxutils.utils.Pair;
import io.vertx.core.http.HttpServerRequest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager<M> {

    private final List<M> modules = new ArrayList<>();
    private final VertxServer<M> server;

    public ModuleManager(VertxServer<M> server) {
        this.server = server;
    }

    public void createModule(M module) {
        if (module.getClass().isAnnotationPresent(ModuleRequest.class)) {
            this.modules.add(module);
        }
    }

    public List<M> getModules() {
        return modules;
    }

    public Pair<Object, Method> getModuleMethod(HttpServerRequest request, String[] paths) {
        MethodHttp methodHttp = MethodHttp.fromHttpMethod(request.method());
        for (M module : this.modules) {
            Class<?> classModule = module.getClass();
            ModuleRequest moduleRequest = classModule.getAnnotation(ModuleRequest.class);
            if (moduleRequest == null) continue;
            String[] modulePath = this.checkModulePath(moduleRequest);
            int i;
            for (i = 0; i < modulePath.length; i++) {
                if (!modulePath[i].equals(paths[i]))
                    break;
                if (i+1 >= paths.length) {
                    Method met = getRequestMethod(classModule, request, methodHttp);
                    if (met != null)
                        return new Pair<>(module, met);
                    return null;
                }
            }
            Method met = getModuleContainsSubRequest(classModule, request, methodHttp, paths[i]);
            if (met != null)
                return new Pair<>(module, met);
        }
        return null;
    }

    private Method getModuleContainsSubRequest(Class<?> classModule, HttpServerRequest request, MethodHttp method, String path) {
        for (Method met : classModule.getDeclaredMethods()) {
            if(ModuleManager.isSubRequest(met, path, method) && this.hasPermission(met, request)) {
                return met;
            }
        }
        return null;
    }

    private Method getRequestMethod(Class<?> classModule, HttpServerRequest request, MethodHttp method) {
        for (Method met : classModule.getDeclaredMethods()) {
            if(ModuleManager.isRequest(met, method) && this.hasPermission(met, request)) {
                return met;
            }
        }
        return null;
    }

    private boolean hasPermission(Method met, HttpServerRequest request) {
        RequestPermission requestPermission = met.getAnnotation(RequestPermission.class);
        return requestPermission == null || this.server.getPermission(requestPermission.value()).isAuthorized(request);
    }

    public static boolean isSubRequest(Method met, String subRequest, MethodHttp method) {
        return met.isAnnotationPresent(SubRequest.class)
                && met.getAnnotation(SubRequest.class).method() == method
                && met.getAnnotation(SubRequest.class).value().equals(subRequest);
    }

    public static boolean isRequest(Method met, MethodHttp method) {
        return met.isAnnotationPresent(Request.class)
                && met.getAnnotation(Request.class).method() == method;
    }

    private String[] checkModulePath(ModuleRequest moduleRequest) {
        String firstArg = moduleRequest.value()[0];
        if (firstArg.startsWith("/"))
            firstArg = firstArg.substring(1);

        return firstArg.contains("/")
                ? firstArg.split("/") : moduleRequest.value();
    }
}
