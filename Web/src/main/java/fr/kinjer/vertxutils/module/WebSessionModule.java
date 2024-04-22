package fr.kinjer.vertxutils.module;

import fr.kinjer.vertxutils.VertxServer;
import fr.kinjer.vertxutils.session.IWebSession;
import fr.kinjer.vertxutils.manager.WebSessionManager;
import fr.kinjer.vertxutils.module.request.Request;
import fr.kinjer.vertxutils.module.request.Response;
import fr.kinjer.vertxutils.request.MethodHttp;
import fr.kinjer.vertxutils.utils.ErrorUtil;
import fr.kinjer.vertxutils.utils.SessionException;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

public class WebSessionModule extends Request {

    public WebSessionModule(VertxServer server, String pathBaseRequest, long expirationTimeSession, Function<Response, String> sessionGetter) {
        super(MethodHttp.GET, pathBaseRequest, response -> true);

        this.addSubRequest(new Request(MethodHttp.POST, "create") {
            @Override
            public String onRequest(Response response) {
                WebSessionManager sessionManager = ((WebSessionManager) server.getManager(pathBaseRequest));
                String session = sessionGetter.apply(response);

                try {
                    if (session != null && sessionManager.getSession(session) == null) {
                        session = null;
                    }
                } catch (SessionException e) {
                    session = null;
                }

                if (session == null) {
                    IWebSession webSession = sessionManager.createSession(expirationTimeSession);

                    session = webSession.getSession();
                }

                return new JsonObject().put("session", session).encode();
            }
        });

        this.addSubRequest(new Request(MethodHttp.POST, "remove") {
            @Override
            public String onRequest(Response response) throws Exception {
                String session = sessionGetter.apply(response);

                if (session == null) {
                    return ErrorUtil.e400("Missing session id");
                }

                return ((WebSessionManager) server.getManager(pathBaseRequest)).removeSession(session);
            }
        });
    }

    @Override
    public String onRequest(Response response) throws Exception {
        return ErrorUtil.e404("Bad path");
    }
}
