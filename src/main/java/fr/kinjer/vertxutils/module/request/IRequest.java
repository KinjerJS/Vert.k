package fr.kinjer.vertxutils.module.request;

import fr.kinjer.vertxutils.request.MethodHttp;

import java.util.List;

public interface IRequest<R extends Response> {

    MethodHttp getMethod();

    String getPath();

    String onRequest(R response) throws Exception;

    boolean isAuthorized(R response);

    <T extends IRequest<R>> T addSubRequests(T... subRequest);

    <T extends IRequest<R>> T addSubRequest(T subRequest);

    <T extends IRequest<R>> List<T> getSubRequests();

    @SuppressWarnings("unchecked")
    default <T extends IRequest<R>> T getSubRequest(String path) {
        return (T) this.getSubRequests().stream().filter(request -> request.getPath().equals(path)).findFirst().orElse(null);
    }
}
