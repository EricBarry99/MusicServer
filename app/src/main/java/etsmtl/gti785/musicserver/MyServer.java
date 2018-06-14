package etsmtl.gti785.musicserver;

import android.content.res.AssetFileDescriptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.ResponseBody;
import java.io.FileInputStream;
import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;
import okio.Buffer;

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

    public String getPathWithSongId(int id){
        return mainActivity.getResources().getResourceName(id);
    }

    public com.squareup.okhttp.Response getFile(){
        int resID = mainActivity.getResources().getIdentifier("heart", "raw", mainActivity.getPackageName());
        String path = getPathWithSongId(resID);
        AssetFileDescriptor assetFileDescriptor = mainActivity.getResources().openRawResourceFd(resID);
        Buffer buf = new Buffer();
        FileInputStream fis = null;

        try {
            buf.readFrom(assetFileDescriptor.createInputStream());
            return fileToResponse(assetFileDescriptor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private com.squareup.okhttp.Response fileToResponse(AssetFileDescriptor afd) throws IOException {

        FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
        com.squareup.okhttp.Response res = new com.squareup.okhttp.Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(MediaType.parse("audio/mpeg"), afd.getLength(),fileToBytes(fis)) )
                .header("foo", "bar")
                .build();

        return res;
    }

    private Buffer fileToBytes(FileInputStream fs) throws IOException {
        Buffer result = new Buffer();
        result.writeAll(result.readFrom(fs));
        return result;
    }

}
