package fr.kinjer.vertxutils;

import fr.kinjer.vertxutils.manager.VertxManager;
import fr.kinjer.vertxutils.module.ModuleManager;
import fr.kinjer.vertxutils.module.request.IRequestPermission;
import fr.kinjer.vertxutils.server.DefaultVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;

public class VertxServer<M> {

    private final int port;
    protected final String apiPath;
    protected final Vertx vertx;

    private final ModuleManager moduleManager;
    private final List<VertxManager> managers;
    private List<IRequestPermission> requestPermissions;

    public VertxServer(int port, String apiPath) {
        this.port = port;
        this.apiPath = apiPath.endsWith("/") ? apiPath : apiPath + "/";
        this.vertx = Vertx.vertx();
        this.managers = new ArrayList<>();
        this.requestPermissions = new ArrayList<>();

        this.moduleManager = new ModuleManager<>(this);
    }

    public VertxServer<M> deployVerticle(Verticle verticle) {
        this.vertx.deployVerticle(verticle);
        return this;
    }

    public VertxServer<M> addPermission(IRequestPermission permission) {
        this.requestPermissions.add(permission);
        return this;
    }

    public VertxServer<M> deployDefaultVerticle() {
        return this.deployVerticle(new DefaultVerticle<>(this));
    }

    public ModuleManager<M> getModuleManager() {
        return moduleManager;
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

    public List<VertxManager> getManagers() {
        return managers;
    }

    public VertxServer<M> addManager(VertxManager manager) {
        this.managers.add(manager);
        return this;
    }

    public VertxManager getManager(String name) {
        return this.managers.stream().filter(manager -> manager.getName().equals(name)).findFirst()
                .orElseThrow(() -> new NullPointerException("Manager not found"));
    }

    public IRequestPermission getPermission(String value) {
        return this.requestPermissions.stream().filter(permission -> permission.getName().equals(value))
                .findFirst().orElse(null);
    }
}
