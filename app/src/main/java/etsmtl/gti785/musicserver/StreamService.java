package etsmtl.gti785.musicserver;

import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import fi.iki.elonen.NanoHTTPD;

import static fi.iki.elonen.NanoHTTPD.newChunkedResponse;

public class StreamService {

    public MainActivity mainActivity;

    public StreamService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public NanoHTTPD.Response initPlayer(){
       String nextSong = mainActivity.getPlayList().get(0);
        JsonObject songMetadata = retrieveSongMetadata(nextSong);
        Gson gson = new GsonBuilder().create();
        String jsonSongMetadata = gson.toJson(songMetadata);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.ACCEPTED,"application/json", jsonSongMetadata);
    }

    public NanoHTTPD.Response getNextSong(String currentSong){

        // find the next song
        int currentSongIndex = mainActivity.playList.indexOf(currentSong);
        int playlistSize = mainActivity.playList.size();
        String nextSong;

        if(currentSongIndex+1 >= playlistSize){
            nextSong = mainActivity.getPlayList().get(0);
        }
        else{
            nextSong = mainActivity.getPlayList().get(currentSongIndex+1);
        }

        // get the next song metadata
        JsonObject songMetadata = retrieveSongMetadata(nextSong);
        Gson gson = new GsonBuilder().create();
        String jsonSongMetadata = gson.toJson(songMetadata);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.ACCEPTED,"application/json", jsonSongMetadata);
    }

    public NanoHTTPD.Response getPreviousSong(){


        return null;
    }

    public NanoHTTPD.Response getRandomSong(){

        return null;
    }

    public NanoHTTPD.Response getFile(){

        int resID = mainActivity.getResources().getIdentifier("heart", "raw", mainActivity.getPackageName());
        String path = getPathWithSongId(resID);
        AssetFileDescriptor assetFileDescriptor = mainActivity.getResources().openRawResourceFd(resID);
        FileInputStream fis = null;

        try {
            fis =  assetFileDescriptor.createInputStream();
            return newChunkedResponse(NanoHTTPD.Response.Status.OK, "audio/mpeg",fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/text", "File Problem");
    }


    public JsonObject retrieveSongMetadata(String nextSong){

//        String nextSong = mainActivity.getPlayList().get(1);
        int resID = mainActivity.getResources().getIdentifier(nextSong, "raw", mainActivity.getPackageName());
        String path = getPathWithSongId(resID);
        String[] parts = path.split("/");
        path = parts[parts.length-1];

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

        JsonObject songMetadata = new JsonObject();

        try {
            songMetadata.addProperty("title", title);
            songMetadata.addProperty("artist", artist);
            songMetadata.addProperty("album", album);
            songMetadata.addProperty("albumArt", artString);
            songMetadata.addProperty("duration", duration);
            songMetadata.addProperty("path", path);

        }catch (Exception e){
            System.out.println("EXCEPTION: " + e.getMessage());
            e.getStackTrace();
        }
        return songMetadata;
    }

    public String getPathWithSongId(int id){
        return mainActivity.getResources().getResourceName(id);
    }
}
