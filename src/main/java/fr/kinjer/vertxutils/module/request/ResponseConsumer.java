package fr.kinjer.vertxutils.module.request;

public interface ResponseConsumer {
    String respond(Response response) throws Exception;
}
