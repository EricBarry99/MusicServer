package etsmtl.gti785.musicserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import fi.iki.elonen.NanoHTTPD;

public class MyServer extends NanoHTTPD {

    public static final int PORT = 8765;

    public MyServer() throws IOException {
        super(PORT);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if (uri.equals("/hello")) {
            System.out.println(session.getUri());
            System.out.println(session.getHeaders());

            String res = "CA VA TU MARCHER";
            return NanoHTTPD.newFixedLengthResponse(Response.Status.ACCEPTED,"application/json", res);
        }

        return newFixedLengthResponse("outOfIf");
    }
}

/*
@TODO
ENvoyer la requete dans le controler. le controler se charge de  verifier le contenu et de caller la bonne methode du service en reponse
il renvoie enfin les bonnes informations / fait les bons appels de methodes
 */