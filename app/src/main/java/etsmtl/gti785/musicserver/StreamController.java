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

        //@TODO nettoyer les string recues pour éviter d'avoir des caracteres inutiles/superflus dans les comparaisons

        if (uri.equals("/initPlayer")) {
            return streamService.initPlayer();

        } else if (uri.equals("/nextSong") && params.containsKey("song")) {
            return streamService.getNextSong(params.get("song").toString());

        } else if (uri.equals("/previousSong") && params.containsKey("song")) {
            return streamService.getPreviousSong(params.get("song").toString());

        } else if (uri.equals("/shuffleSong")&& params.containsKey("song") && params.containsKey("shuffle")) {
            return streamService.getRandomSong();

        } else {
            if(uri.contains("/raw/")) {
                return streamService.getFileStream(uri);
            }
            else{
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/text", "File Problem");
            }
        }
    }
}
