package modules;

import fr.kinjer.vertxutils.module.request.*;

@ModuleRequest("test")
public class TestModule {

    @Request()
    public String onRequest(String id, @Body("toto") Integer kaka) throws Exception {
        return "Hello World! " + id + " " + kaka;
    }

    @SubRequest(value = "autre")
    public String onAutre() {
        return "Attt";
    }
}
