package fr.kinjer.vertxutils.utils;

public class SessionException extends HttpVertxException {

    private final Type type;

    public SessionException(Type type) {
        super(400, type.name());
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        BAD_SESSION,
        SESSION_EXPIRED,
        SESSION_NOT_FOUND
    }
}
