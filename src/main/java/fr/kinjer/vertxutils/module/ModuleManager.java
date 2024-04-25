package fr.kinjer.vertxutils.module;

import fr.kinjer.vertxutils.module.request.*;
import fr.kinjer.vertxutils.request.MethodHttp;
import fr.kinjer.vertxutils.utils.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager<T> {

    private final List<T> modules = new ArrayList<>();

    public ModuleManager() {
    }

    public void createModule(T module) {
        if (module.getClass().isAnnotationPresent(ModuleRequest.class)) {
            this.modules.add(module);
        }
    }

    public List<T> getModules() {
        return modules;
    }

    public Pair<Object, Method> getModuleMethod(MethodHttp method, String[] paths) {
        for (T module : this.modules) {
            Class<?> classModule = module.getClass();
            ModuleRequest moduleRequest = classModule.getAnnotation(ModuleRequest.class);
            if (moduleRequest == null) continue;
            String[] modulePath = this.checkModulePath(moduleRequest);
            int i;
            for (i = 0; i < modulePath.length; i++) {
                if (!modulePath[i].equals(paths[i]))
                    break;
                if (i+1 >= paths.length) {
                    Method met = getRequestMethod(classModule, paths[i], method);
                    if (met != null)
                        return new Pair<>(module, met);
                    return null;
                }
            }
            Method met = getModuleContainsSubRequest(classModule, method, paths[i]);
            if (met != null)
                return new Pair<>(module, met);
        }
        return null;
    }

    private static Method getModuleContainsSubRequest(Class<?> classModule, MethodHttp method, String path) {
        for (Method met : classModule.getDeclaredMethods()) {
            if(ModuleManager.isSubRequest(met, path, method)) {
                return met;
            }
        }
        return null;
    }

    private static Method getRequestMethod(Class<?> classModule, String path, MethodHttp method) {
        for (Method met : classModule.getDeclaredMethods()) {
            if(ModuleManager.isRequest(met, method)) {
                return met;
            }
        }
        return null;
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
