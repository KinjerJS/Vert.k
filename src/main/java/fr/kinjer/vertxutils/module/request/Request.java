package fr.kinjer.vertxutils.module.request;

import fr.kinjer.vertxutils.request.MethodHttp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Request<R extends Response> implements IRequest<R> {
    private final MethodHttp method;
    private final String path;
    private final Predicate<R> isAuthorized;

    private final List<IRequest<R>> subRequests = new ArrayList<>();

    public Request(MethodHttp method, String path, Predicate<R> isAuthorized) {
        this.method = method;
        this.path = path;
        this.isAuthorized = isAuthorized;
    }

    public Request(MethodHttp method, String path) {
        this(method, path, (__) -> true);
    }

    @Override
    public MethodHttp getMethod() {
        return method;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isAuthorized(R response) {
        return isAuthorized.test(response);
    }

    @Override
    public <T extends IRequest<R>> T addSubRequests(T... subRequest) {
        this.subRequests.addAll(List.of(subRequest));
        return (T) this;
    }

    @Override
    public <T extends IRequest<R>> T addSubRequest(T subRequest) {
        return this.addSubRequests(subRequest);
    }

    @Override
    public <T extends IRequest<R>> List<T> getSubRequests() {
        return (List<T>) this.subRequests;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", isAuthorized=" + isAuthorized +
                ", subRequests=" + subRequests +
                '}';
    }
}
