package fr.kinjer.vertxutils;

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

    private final ModuleManager<M> moduleManager;
    private final List<IRequestPermission> requestPermissions = new ArrayList<>();

    public VertxServer(int port, String apiPath) {
        this.port = port;
        this.apiPath = apiPath.endsWith("/") ? apiPath : apiPath + "/";
        this.vertx = Vertx.vertx();

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

    @SuppressWarnings("unchecked")
    public void addModules(M... module) {
        this.moduleManager.addModules(module);
    }

    public void addModule(M module) {
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
}
