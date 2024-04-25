package main;

import fr.kinjer.vertxutils.VertxServer;
import fr.kinjer.vertxutils.module.request.Request;
import fr.kinjer.vertxutils.module.request.Response;
import fr.kinjer.vertxutils.server.DefaultVerticle;
import modules.SubAutreModule;
import modules.SubTestModule;
import modules.TestModule;

public class Main {

    public static void main(String[] args) {
        new Server();
    }

    public static class Server extends VertxServer<Object> {

        public Server() {
            super(789, "");
//            this.deployVerticle(new TestVerticle());
            this.deployVerticle(new DefaultVerticle<>(this));
            this.getModuleManager().createModule(new TestModule());
            this.getModuleManager().createModule(new SubTestModule());
            this.getModuleManager().createModule(new SubAutreModule());

        }

    }

}
