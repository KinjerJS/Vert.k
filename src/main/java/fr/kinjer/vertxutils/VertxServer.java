package fr.kinjer.vertxutils;

import fr.kinjer.vertxutils.manager.VertxManager;
import fr.kinjer.vertxutils.module.ModuleManager;
import fr.kinjer.vertxutils.module.request.IRequest;
import fr.kinjer.vertxutils.module.request.Response;
import fr.kinjer.vertxutils.server.DefaultVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;

public class VertxServer<R extends IRequest<Re>, Re extends Response> {

    private final int port;
    protected final String apiPath;
    protected final Vertx vertx;

    private final ModuleManager<R, Re> moduleManager;
    private final List<VertxManager> managers;

    public VertxServer(int port, String apiPath) {
        this.port = port;
        this.apiPath = apiPath.endsWith("/") ? apiPath : apiPath + "/";
        this.vertx = Vertx.vertx();
        this.managers = new ArrayList<>();

        this.moduleManager = new ModuleManager<R, Re>();
    }

    public VertxServer<R, Re> deployVerticle(Verticle verticle) {
        this.vertx.deployVerticle(verticle);
        return this;
    }

    public VertxServer<R, Re> deployDefaultVerticle() {
        return this.deployVerticle(new DefaultVerticle<>(this));
    }

    public ModuleManager<R, Re> getModuleManager() {
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

    public VertxServer<R, Re> addManager(VertxManager manager) {
        this.managers.add(manager);
        return this;
    }

    public VertxManager getManager(String name) {
        return this.managers.stream().filter(manager -> manager.getName().equals(name)).findFirst()
                .orElseThrow(() -> new NullPointerException("Manager not found"));
    }
}
