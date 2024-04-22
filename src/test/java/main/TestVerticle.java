package main;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestVerticle extends AbstractVerticle {

    int number = 0;
    private List<String> messages = new ArrayList<>();
    private List<Session> sessions = new ArrayList<>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        System.out.println("Starting verticle on port " + 788 + " (http://localhost:" + 788 + "/)");
        Router router = Router.router(this.vertx);

        this.vertx.createHttpServer().webSocketHandler(event -> {
            System.out.println(sessions);
            MultiMap headers = event.headers();
            System.out.println("WebSocket connect : " + event.path());

            Session session = new Session(UUID.randomUUID().toString(), event);
            sessions.add(session);
            number++;
            event.writeTextMessage("Loading...");
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                event.writeTextMessage("Hello " + number);
            }).start();
            event.textMessageHandler(message -> {
                messages.add(message);
                for (Session session1 : sessions) {
                    session1.getEvent().writeTextMessage("Message received : " + message);
                }
            });
            event.closeHandler(event1 -> {
                this.sessions.remove(session);
                System.out.println("WebSocket close : " + event.path());
                System.out.println(sessions);
            });
        }).listen(988);
//        router.route("/files/upload/").handler(BodyHandler.create().setUploadsDirectory("uploads"));
//
//        router.get("/tes/").handler(ctx -> {
//            System.out.println("test");
//            ctx.response().end("test");
//        });
//
////        router.get().handler(event -> {
////            System.out.println("test");
////            event.response().end("test");
////        });
//        router.post("/files/upload/").handler(routingContext -> {
//            System.out.println("qsd");
//            for (FileUpload fileUpload : routingContext.fileUploads()) {
//                String uploadedFileName = fileUpload.uploadedFileName();
//                System.out.println("File dl : " + uploadedFileName);
//            }
//            routingContext.response().end("Upload successful");
//        });
//        this.vertx.createHttpServer().requestHandler(router).listen(789);
    }

    private static class Session {

        private final String id;
        private final ServerWebSocket event;

        public Session(String id, ServerWebSocket event) {
            this.id = id;
            this.event = event;
        }

        public String getId() {
            return id;
        }

        public ServerWebSocket getEvent() {
            return event;
        }
    }
}
