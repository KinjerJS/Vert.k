package modules;

import fr.kinjer.vertk.module.request.ModuleRequest;
import fr.kinjer.vertk.module.request.Request;
import fr.kinjer.vertk.module.request.SubRequest;

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
