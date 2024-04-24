package modules;

import fr.kinjer.vertxutils.module.request.*;

@ModuleRequest(TestModule.PATH)
public class TestModule {

    public static final String PATH = "test";

    @Request
    public String onRequest(String id, @Param("toto") Integer kaka) throws Exception {
        return "Hello Wsdqorld! " + id + " " + kaka;
    }

    @SubRequest("autre")
    public String onAutre() {
        return "Attt";
    }

    @SubRequest("ads")
    public String onAutre2() {
        return "Attqsdt";
    }
}
