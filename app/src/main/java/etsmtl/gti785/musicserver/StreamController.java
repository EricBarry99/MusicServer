package etsmtl.gti785.musicserver;

import android.net.Uri;

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            // gestion par defaut, la requete est pas bonne
               return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "Bad Request");

            // essaie de retourner un FileInputStream pour retourner la chanson a jouer
   /*
            try {
//                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.ACCEPTED, "audio/mpeg", "fis");
//                return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "audio/mpeg", fis);

//                return streamService.getFile();

            }catch(Exception e){
                e.printStackTrace();
            }
            */
     //       return null;
        }

    }
}

