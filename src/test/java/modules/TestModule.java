package modules;

import fr.kinjer.vertxutils.module.request.Request;
import fr.kinjer.vertxutils.module.request.Response;
import fr.kinjer.vertxutils.module.request.ResponseConsumer;
import fr.kinjer.vertxutils.request.MethodHttp;
import main.Main;

public class TestModule extends Request {
    public TestModule(Main.Server vertxServer) {
        super(MethodHttp.GET, "test", (__) -> true);
        this.addSubRequests(
                new Request(MethodHttp.GET, "hello", (__) -> false) {
                    @Override
                    public String onRequest(Response response) throws Exception {
                        return "Hello World two!";
                    }
                }.addSubRequests(new Request(MethodHttp.GET, "subhello", (__) -> true) {
                    @Override
                    public String onRequest(Response response) throws Exception {
                        return "Hello World three!";
                    }
                }.addSubRequest(new Request(MethodHttp.GET, "subsubhello", (__) -> true) {
                    @Override
                    public String onRequest(Response response) throws Exception {
                        return "Hello World four!";
                    }
                }))
        );
    }

    @Override
    public String onRequest(Response response) throws Exception {
        return "Hello World!";
    }
}
