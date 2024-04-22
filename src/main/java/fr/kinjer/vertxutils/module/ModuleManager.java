package fr.kinjer.vertxutils.module;

import fr.kinjer.vertxutils.VertxServer;
import fr.kinjer.vertxutils.module.request.IRequest;
import fr.kinjer.vertxutils.module.request.Response;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager<R extends IRequest<Re>, Re extends Response> {

    private final List<R> modules = new ArrayList<>();

    public ModuleManager() {
    }

    public void createModule(R module) {
        this.modules.add(module);
    }

    public List<R> getModules() {
        return modules;
    }

    public R getModule(String modulePath) {
        return this.getModule(modulePath, null);
    }

    public R getModule(String modulePath, R defaultModule) {
        return this.modules.stream().filter(requestModule -> requestModule.getPath().equals(modulePath))
                .findFirst().orElse(defaultModule);
    }

}
