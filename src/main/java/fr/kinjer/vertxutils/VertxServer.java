package fr.kinjer.vertxutils;

import fr.kinjer.vertxutils.module.ModuleManager;
import fr.kinjer.vertxutils.module.request.DefaultPermission;
import fr.kinjer.vertxutils.module.request.IRequestPermission;
import fr.kinjer.vertxutils.server.DefaultVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;

public class VertxServer<Module> {

    private final int port;
    protected final String apiPath;
    protected final Vertx vertx;

    private final ModuleManager<Module> moduleManager;
    private final List<IRequestPermission> requestPermissions = new ArrayList<>();

    public VertxServer(int port, String apiPath) {
        this.port = port;
        this.apiPath = apiPath.endsWith("/") ? apiPath : apiPath + "/";
        this.vertx = Vertx.vertx();

        this.moduleManager = new ModuleManager<>(this);
    }

    public VertxServer<Module> deployVerticle(Verticle verticle) {
        this.vertx.deployVerticle(verticle);
        return this;
    }

    public VertxServer<Module> addPermission(IRequestPermission permission) {
        this.requestPermissions.add(permission);
        return this;
    }

    public VertxServer<Module> deployDefaultVerticle() {
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
