package modules;

import fr.kinjer.vertxutils.module.request.ModuleRequest;
import fr.kinjer.vertxutils.module.request.Request;
import fr.kinjer.vertxutils.module.request.SubRequest;

@ModuleRequest({TestModule.PATH, "auqsdtre", "ads"})
public class SubAutreModule {

    @Request
    public String onRequest() {
        return "sqd";
    }

    @SubRequest("aaa")
    public String onAutre() {
        return "aaaaaa";
    }

}
