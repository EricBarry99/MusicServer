package etsmtl.gti785.musicserver;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.json.JSONObject;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class MyServer extends NanoHTTPD {

    MainActivity mainActivity;
    ArrayList<String> playList = new ArrayList();
    AudioFile audioFile;

    public MyServer(int port, MainActivity mainActivity) {
        super(port);
        this.mainActivity = mainActivity;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if (uri.equals("/play")) {
            this.playList = listElemRaw();
            JSONObject songMetadata = retrieveSongMetadata(this.playList.get(0));

            Gson gson = new GsonBuilder().create();
            String jsonSongMetadata = gson.toJson(songMetadata);

            return NanoHTTPD.newFixedLengthResponse(Response.Status.ACCEPTED,"application/json", jsonSongMetadata);
        }
        return newFixedLengthResponse("outOfIf");
    }


    public JSONObject retrieveSongMetadata(String songName){

        int resID = mainActivity.getResources().getIdentifier(songName, "raw", mainActivity.getPackageName());
        String path = getPathWithSongId(resID);

        // on initialise le metadataretriever avec un assetfiledescriptor pour l'aider a traiter le fichier
        //  source: http://book2s.com/java/api/android/media/mediametadataretriever/setdatasource-3.html
        AssetFileDescriptor assetFileDescriptor = mainActivity.getResources().openRawResourceFd(resID);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

        String artist =  mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String duration =  mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String album =  mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String title =  mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        // encoder l'image en base64 pour la faire passer en string
        // source: https://stackoverflow.com/questions/36492084/how-to-convert-an-image-to-base64-string-in-java
        byte[] artByteArray =  mediaMetadataRetriever.getEmbeddedPicture();
        String artString = Base64.encodeToString(artByteArray,0);

        JSONObject songMetadata = new JSONObject();

        try {
            songMetadata.put("title", title);
            songMetadata.put("artist", artist);
            songMetadata.put("album", album);
            songMetadata.put("albumArt", artString);
            songMetadata.put("duration", duration);
            songMetadata.put("path", path);

        }catch (Exception e){
            System.out.println("EXCEPTION: " + e.getMessage());
            e.getStackTrace();
        }
        return songMetadata;
    }


    public ArrayList<String> listElemRaw(){
        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            Log.d("Raw Asset: ", fields[count].getName());
            String field = fields[count].getName();
            playList.add(field);
        }
        return playList;
    }

    private String getPathWithSongId(int id){
        return mainActivity.getResources().getResourceName(id);
    }
}
