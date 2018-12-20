package com.belaku.media;

/**
 * Created by naveenprakash on 05/11/18.
 */

public class AudioSong {

    long id;
    String albumId;
    String path;
    String artist;
    String title;
    String album;
    String albumArt;
    Boolean isFavorite;
    long duration;

    public AudioSong(long id, String albumArt, String path, String artist, String title, String album, long duration, Boolean isFav) {
        this.albumId = albumId;
        this.albumArt = albumArt;
        this.id = id;
        this.path = path;
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.duration = duration;
        this.isFavorite = isFav;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
