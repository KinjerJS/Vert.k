package fr.kinjer.vertxutils.manager;

import fr.kinjer.vertxutils.session.IWebSession;
import fr.kinjer.vertxutils.session.WebSessionImpl;
import fr.kinjer.vertxutils.utils.ErrorUtil;
import fr.kinjer.vertxutils.utils.SessionException;
import fr.kinjer.vertxutils.utils.SessionUtil;

import java.util.ArrayList;
import java.util.List;

public class WebSessionManager implements VertxManager {

    private final String name;

    private final List<IWebSession> sessions = new ArrayList<>();

    public WebSessionManager(String name) {
        this.name = name;
    }

    public IWebSession createSession() {
        return this.createSession(21600);
    }

    public IWebSession createSession(long expirationTime) {
        String session = SessionUtil.generateSessionId();
        IWebSession webSession = new WebSessionImpl(session, expirationTime);

        this.sessions.add(webSession);

        return webSession;
    }

    public IWebSession getSession(String session) throws SessionException {
        IWebSession webSession = this.sessions.stream().filter(s -> s.getSession().equals(session))
                .findFirst().orElse(null);
        if(webSession == null)
            throw new SessionException(SessionException.Type.SESSION_NOT_FOUND);
        if(webSession.isExpired()) {
            this.sessions.remove(webSession);
            throw new SessionException(SessionException.Type.SESSION_EXPIRED);
        }
        return webSession;
    }

    public String removeSession(String session) throws SessionException {
        try {
            IWebSession webSession = this.getSession(session);
            if(webSession != null) {
                this.sessions.remove(webSession);
                return "{}";
            }
        } catch (SessionException ignored) {}

        return ErrorUtil.e404("Session not found");
    }

    public List<IWebSession> getSessions() {
        return this.sessions;
    }

    public List<IWebSession> getSessionsByKeyData(String keyData) {
        return this.sessions.stream().filter(webSession -> webSession.hasData(keyData)).toList();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
