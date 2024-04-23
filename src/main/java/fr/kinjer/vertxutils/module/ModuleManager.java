package fr.kinjer.vertxutils.module;

import fr.kinjer.vertxutils.module.request.ModuleRequest;
import fr.kinjer.vertxutils.module.request.Request;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager<T> {

    private final List<T> modules = new ArrayList<>();

    public ModuleManager() {
    }

    public void createModule(T module) {
        this.modules.add(module);
    }

    public List<T> getModules() {
        return modules;
    }

    public T getModule(String path) {
        for (T module : modules) {
            Class<?> classModule = module.getClass();

            if (classModule.isAnnotationPresent(ModuleRequest.class)
                    && classModule.getAnnotation(ModuleRequest.class).value().equals(path)) {
                return module;
            }
        }
        return null;
    }
}
