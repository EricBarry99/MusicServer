package etsmtl.gti785.bean;

import android.graphics.Bitmap;

public class Song {

    public Artist artist;
    public Album album;
    public String duration;
    public String title;
    public Bitmap albumArt;
    public String composer;
    public String genre;

    public Song() {
    }


    public Song(Artist artist, Album album, String duration, String title, Bitmap albumArt, String composer, String genre) {
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.title = title;
        this.albumArt = albumArt;
        this.composer = composer;
        this.genre = genre;
    }


    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
