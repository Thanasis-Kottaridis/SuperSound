package com.unipi.kottarido.supersound.supersound;

public class Song {
    private long id;
    private String title;
    private String artist;
    private MusicPreference musicKind;
    private boolean loadedInApp;

    public Song(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        musicKind = new MusicPreference("UNKNOWN");
        loadedInApp = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isLoadedInApp() {
        return loadedInApp;
    }

    public void setLoadedInApp(boolean loadedInApp) {
        this.loadedInApp = loadedInApp;
    }

    public MusicPreference getMusicKind() {
        return musicKind;
    }

    public void setMusicKind(MusicPreference musicKind) {
        this.musicKind = musicKind;
    }
}
