package etsmtl.gti785.musicserver;

import android.content.res.AssetFileDescriptor;
import java.io.FileInputStream;
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

//        return getFile(streamController);
    }

//    https://github.com/NanoHttpd/nanohttpd/issues/232
    // fonction de test, a etre retiree
    public Response getFile(StreamController streamController){
        int resID = mainActivity.getResources().getIdentifier("heart", "raw", mainActivity.getPackageName());
        String path = streamController.streamService.getPathWithSongId(resID);
        AssetFileDescriptor assetFileDescriptor = mainActivity.getResources().openRawResourceFd(resID);
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(assetFileDescriptor.getFileDescriptor());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.ACCEPTED,  "audio/mpeg-url", fis, assetFileDescriptor.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", "File Problem");
    }

}
