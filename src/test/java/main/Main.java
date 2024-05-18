package main;

import fr.kinjer.vertxutils.VertxServer;
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
            this.addModules(new TestModule());
            this.addModules(new SubTestModule());
            this.addModules(new SubAutreModule());

        }

    }

}
