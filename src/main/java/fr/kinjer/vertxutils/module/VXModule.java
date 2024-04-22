package fr.kinjer.vertxutils.module;

import fr.kinjer.vertxutils.VertxServer;

public abstract class VXModule {

    protected final VertxServer server;

    public VXModule(VertxServer server) {
        this.server = server;
    }

}
