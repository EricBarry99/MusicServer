package etsmtl.gti785.musicserver;

import java.util.Map;
import fi.iki.elonen.NanoHTTPD;

public class StreamController {

    public MyServer myServer;
    public StreamService streamService;

    public StreamController(MyServer myServer, MainActivity mainActivity) {
        this.myServer = myServer;
        this.streamService = new StreamService(mainActivity);
    }

    public NanoHTTPD.Response manage(NanoHTTPD.IHTTPSession session) {

        String uri = session.getUri();
        Map params = session.getParameters();

        if (uri.equals("/initPlayer")) {
            return streamService.initPlayer();
        } else if (uri.equals("/nextSong") && params.containsKey("song")) {
            return streamService.getNextSong(params.get("song").toString());
        } else if (uri.equals("/previousSong")) {
            return streamService.getPreviousSong();
        } else if (uri.equals("/shuffleSong")) {
            return streamService.getRandomSong();
        } else {
            return streamService.getFile();
        }
    }
}

