package etsmtl.gti785.musicserver;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class MyServer extends NanoHTTPD {
    // https://github.com/NanoHttpd/nanohttpd
// https://stackoverflow.com/questions/14309256/using-nanohttpd-in-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
// un peu de backend, peut etre un peu trop low level: https://www.programcreek.com/java-api-examples/?code=weiwenqiang/GitHub/GitHub-master/MVP/BookReader-master/app/src/main/java/com/justwayward/reader/wifitransfer/NanoHTTPD.java

    StreamController streamController;
    public static final int PORT = 8765;

    public MyServer(StreamController musicController) throws IOException {
        super(PORT);
        this.streamController = musicController;
    }

    @Override
    public  serve(IHTTPSession session) {
        String uri = session.getUri();
        String response;


        if (uri.equals("/hello")) {
//
//            String msg = "<html><body><h1>Hello server</h1>\n";
//            msg += "<p>We serve " + session.getUri() + " !</p>";
//            return newFixedLengthResponse( msg + "</body></html>\n" );

            String response = "HelloWorld111";
            return newFixedLengthResponse(response);
        }
        return  null;
    }

}
