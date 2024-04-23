package fr.kinjer.vertxutils;

import fr.kinjer.vertxutils.manager.VertxManager;
import fr.kinjer.vertxutils.module.ModuleManager;
import fr.kinjer.vertxutils.server.DefaultVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;

public class VertxServer<T> {

    private final int port;
    protected final String apiPath;
    protected final Vertx vertx;

    private final ModuleManager moduleManager;
    private final List<VertxManager> managers;

    public VertxServer(int port, String apiPath) {
        this.port = port;
        this.apiPath = apiPath.endsWith("/") ? apiPath : apiPath + "/";
        this.vertx = Vertx.vertx();
        this.managers = new ArrayList<>();

        this.moduleManager = new ModuleManager<>();
    }

    public VertxServer<T> deployVerticle(Verticle verticle) {
        this.vertx.deployVerticle(verticle);
        return this;
    }

    public VertxServer<T> deployDefaultVerticle() {
        return this.deployVerticle(new DefaultVerticle<>(this));
    }

    public ModuleManager<T> getModuleManager() {
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

    public VertxServer addManager(VertxManager manager) {
        this.managers.add(manager);
        return this;
    }

    public VertxManager getManager(String name) {
        return this.managers.stream().filter(manager -> manager.getName().equals(name)).findFirst()
                .orElseThrow(() -> new NullPointerException("Manager not found"));
    }
}
