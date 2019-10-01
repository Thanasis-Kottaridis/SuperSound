package com.unipi.kottarido.supersound.supersound;

public class OnlineSong {
    private String Title;
    private String Artist;
    private String Url;

    //no argument constructor!!! einai aparetitos gia na fortothei playlist obj apo tin firebase
    //prepei na exw dilomeno enan no-argument constructor gia na xristimopoiithei
    //otan tou anathetoume to value enos snapshot obj tis firebase
    public OnlineSong(){

    }

    public OnlineSong(String title, String artist, String url) {
        Title = title;
        Artist = artist;
        Url = url;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
