package etsmtl.gti785.musicserver;

import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import etsmtl.gti785.bean.Song;
import fi.iki.elonen.NanoHTTPD;
import static fi.iki.elonen.NanoHTTPD.newChunkedResponse;

public class StreamService {

    public MainActivity mainActivity;
    public ArrayList<Song> playList;

    public StreamService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.playList = createPlaylist();
    }

    public ArrayList<Song> getPlayList() {
        return playList;
    }

    public ArrayList<Song> createPlaylist() {
        ArrayList<Song> playList = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            Song song = createSongFromFileName(fields[count].getName());
            playList.add(song);
        }
        return playList;
    }

    public NanoHTTPD.Response initPlayer(){
        return generateResponse(getPlayList().get(0).getTitle());
    }

    public NanoHTTPD.Response getNextSong(String currentSong){

        int currentSongIndex = 0;
        Song nextSong;

        // TODO: change contains for equals after cleaning the url params String
        for (Song song: playList) {
            if(currentSong.contains(song.getTitle())){
                currentSongIndex = playList.indexOf(song);
            }
        }

        if(currentSongIndex == playList.size()-1){
            nextSong = playList.get(0);
        }
        else{
            nextSong = playList.get(currentSongIndex+1);
        }
        return generateResponse(nextSong.getTitle());
    }

    public NanoHTTPD.Response getPreviousSong(String currentSong){

        int currentSongIndex = 0;
        Song previousSong;

        // TODO: change contains for equals after cleaning the url params String
        for (Song song: playList) {
            if(currentSong.contains(song.getTitle())){
                currentSongIndex = playList.indexOf(song);
            }
        }

        if(currentSongIndex == 0){
            previousSong = playList.get(playList.size()-1);
        }
        else{
            previousSong = playList.get(currentSongIndex-1);
        }

        return generateResponse(previousSong.getTitle());
    }

    // TODO: make sure that the selected song is not the one that is playing already
    public NanoHTTPD.Response getRandomSong(){
        int randomNum = ThreadLocalRandom.current().nextInt(0, playList.size());
        return generateResponse(playList.get(randomNum).getTitle());
    }

    public NanoHTTPD.Response generateResponse(String songName){
        int songIndex = 0;

        for (Song song: playList) {
            if(songName == song.getTitle()){
                songIndex = playList.indexOf(song);
            }
        }

        JsonObject songMetadata = retrieveSongMetadata(playList.get(songIndex));
        Gson gson = new GsonBuilder().create();
        String jsonSongMetadata = gson.toJson(songMetadata);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.ACCEPTED,"application/json", jsonSongMetadata);
    }

    public NanoHTTPD.Response getFileStream(String uri){

        String fileName = uri.substring(5,uri.length()-4);
        int resID = mainActivity.getResources().getIdentifier(fileName, "raw", mainActivity.getPackageName());
        String path = getPathWithSongId(resID);
        AssetFileDescriptor assetFileDescriptor = mainActivity.getResources().openRawResourceFd(resID);

        try {
            FileInputStream fis = assetFileDescriptor.createInputStream();
            return newChunkedResponse(NanoHTTPD.Response.Status.OK, "audio/mpeg",fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/text", "File Problem");
    }

    public JsonObject retrieveSongMetadata(Song song){

        JsonObject songMetadata = new JsonObject();
        songMetadata.addProperty("title", song.title);
        songMetadata.addProperty("artist", song.artist);
        songMetadata.addProperty("album", song.album);
        songMetadata.addProperty("albumArt", song.albumArt);
        songMetadata.addProperty("duration", song.duration);
        songMetadata.addProperty("path", song.path);

        return songMetadata;
    }

        public Song createSongFromFileName(String songFileName){

        int resID = mainActivity.getResources().getIdentifier(songFileName, "raw", mainActivity.getPackageName());
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
        byte[] artByteArray;
        String artString;

        if( mediaMetadataRetriever.getEmbeddedPicture() == null){
            artString = null;        }
        else{
            artByteArray = mediaMetadataRetriever.getEmbeddedPicture();
            artString = Base64.encodeToString(artByteArray,0);
        }

        Song song = new Song(title, artist, album, artString, duration, path);
        return song;
    }

    public String getPathWithSongId(int id){
        return mainActivity.getResources().getResourceName(id);
    }
}
