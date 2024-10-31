package fr.kinjer.vertk;

import fr.kinjer.vertk.module.ModuleManager;
import fr.kinjer.vertk.module.request.DefaultPermission;
import fr.kinjer.vertk.module.request.IRequestPermission;
import fr.kinjer.vertk.verticle.DefaultVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;

public class VertkServer<Module> {

    private final int port;
    protected final String apiPath;
    protected final Vertx vertx;

    private final ModuleManager<Module> moduleManager;
    private final List<IRequestPermission> requestPermissions = new ArrayList<>();

    public VertkServer(int port, String apiPath) {
        this.port = port;
        this.apiPath = apiPath.endsWith("/") ? apiPath : apiPath + "/";
        this.vertx = Vertx.vertx();

        this.moduleManager = new ModuleManager<>(this);
    }

    public VertkServer<Module> deployVerticle(Verticle verticle) {
        this.vertx.deployVerticle(verticle);
        return this;
    }

    public VertkServer<Module> addPermission(IRequestPermission permission) {
        this.requestPermissions.add(permission);
        return this;
    }

    public VertkServer<Module> deployDefaultVerticle() {
        return this.deployVerticle(new DefaultVerticle<>(this));
    }

    public ModuleManager<Module> getModuleManager() {
        return moduleManager;
    }

    @SuppressWarnings("unchecked")
    public void addModules(Module... module) {
        this.moduleManager.addModules(module);
    }

    public void addModule(Module module) {
        this.moduleManager.addModule(module);
    }

    public int getServerPort() {
        return port;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public String getApiPath() {
        return apiPath;
    }

    public IRequestPermission getPermission(String value) {
        return this.requestPermissions.stream().filter(permission -> permission.getName().equals(value))
                .findFirst().orElse(null);
    }

    /**
     * Get the default permission.<br>
     * The default permission is the permission with the name "default".<br>
     * If no default permission is found, return a new {@link DefaultPermission}.<br>
     * It will always be verified before any other permission.
     *
     * @return the default permission
     */
    public IRequestPermission getDefaultPermission() {
        return this.requestPermissions.stream().filter(permission -> permission.getName().equals("default"))
                .findFirst().orElse(new DefaultPermission());
    }
}
