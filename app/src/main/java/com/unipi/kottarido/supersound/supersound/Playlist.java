package com.unipi.kottarido.supersound.supersound;

import java.util.List;

public class Playlist {
    private String coverURL;
    private String Title;
    private String musicKind;
    private List<OnlineSong> OnlineSongList;

    //no argument constructor!!! einai aparetitos gia na fortothei playlist obj apo tin firebase
    //prepei na exw dilomeno enan no-argument constructor gia na xristimopoiithei
    //otan tou anathetoume to value enos snapshot obj tis firebase
    public Playlist() {

    }

    public Playlist(String coverURL, String title, String musicKind, List<OnlineSong> OnlineSongList) {
        this.coverURL = coverURL;
        Title = title;
        this.musicKind = musicKind.toLowerCase();
        this.OnlineSongList = OnlineSongList;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public List<OnlineSong> getOnlineSongList() {
        return OnlineSongList;
    }

    public void setOnlineSongList(List<OnlineSong> onlineSongList) {
        this.OnlineSongList = onlineSongList;
    }

    public String getMusicKind() {
        return musicKind;
    }

    public void setMusicKind(String musicKind) {
        this.musicKind = musicKind;
    }
}