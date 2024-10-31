package modules;

import fr.kinjer.vertk.module.request.ModuleRequest;
import fr.kinjer.vertk.module.request.Request;
import fr.kinjer.vertk.module.request.SubRequest;

@ModuleRequest({TestModule.PATH, "auqsdtre"})
public class SubTestModule {

    @Request
    public String onAutre() {
        return "AAUTREd";
    }

    @SubRequest("adss")
    public String onAutre2() {
        return "Attqsdt222";
    }


}
