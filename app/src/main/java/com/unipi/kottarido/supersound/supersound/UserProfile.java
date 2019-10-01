package com.unipi.kottarido.supersound.supersound;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    private String userEmail;
    private List<POI> myPois;
    private List<MusicPreference> myMusicPreferences;
    private List<Song> mySong;

    public UserProfile(){
        myPois = new ArrayList<>();
        myMusicPreferences = new ArrayList<>();
        mySong = new ArrayList<>();
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<POI> getMyPois() {
        return myPois;
    }

    public void setMyPois(List<POI> myPois) {
        this.myPois = myPois;
    }

    public List<MusicPreference> getMyMusicPreferences() {
        return myMusicPreferences;
    }

    public void setMyMusicPreferences(List<MusicPreference> myMusicPreferences) {
        this.myMusicPreferences = myMusicPreferences;
    }

    public List<Song> getMySong() {
        return mySong;
    }

    public void setMySong(List<Song> mySong) {
        this.mySong = mySong;
    }

    //epistreuei to instance se morfi string
    public String getProfileToString(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

}
