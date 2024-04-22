package main;

import fr.kinjer.vertxutils.VertxServer;
import fr.kinjer.vertxutils.server.DefaultVerticle;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import modules.TestModule;

public class Main {

    public static void main(String[] args) {
        new Server();
    }

    public static class Server extends VertxServer {

        public Server() {
            super(789, "");
//            this.deployVerticle(new TestVerticle());
            this.deployVerticle(new DefaultVerticle<>(this).withPreInitServer(httpServer -> {
                httpServer.webSocketHandler(ws -> {
                    ws.textMessageHandler(event -> {
                        System.out.println("WebSocket message : " + event);
                    });
                });
            }));
            this.getModuleManager().createModule(new TestModule(this));

        }

    }

}
