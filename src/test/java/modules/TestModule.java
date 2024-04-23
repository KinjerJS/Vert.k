package modules;

import fr.kinjer.vertxutils.module.request.Request;
import fr.kinjer.vertxutils.module.request.Response;
import fr.kinjer.vertxutils.request.MethodHttp;
import main.Main;

@Request("test")
public class TestModule {

    public String onRequest(Response response) throws Exception {
        return "Hello World!";
    }
}
