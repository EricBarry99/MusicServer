package etsmtl.gti785.musicserver;

import java.util.ArrayList;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MyServer extends NanoHTTPD {

    MainActivity mainActivity;

    public MyServer(int port, MainActivity mainActivity) {
        super(port);
        this.mainActivity = mainActivity;
    }

    @Override
    public Response serve(IHTTPSession session) {
        StreamController streamController = new StreamController(this, mainActivity);
        Response response = streamController.manage(session);
        return response;
    }
}
