package modules;

import fr.kinjer.vertxutils.module.request.Request;
import fr.kinjer.vertxutils.module.request.Response;
import fr.kinjer.vertxutils.module.request.SubRequest;
import fr.kinjer.vertxutils.module.request.ModuleRequest;

@ModuleRequest("test")
public class TestModule {

    @Request
    public String onRequest(Response response) throws Exception {
        return "Hello World!";
    }

    @SubRequest(value = "autre")
    public String onAutre() {
        return "Attt";
    }
}
