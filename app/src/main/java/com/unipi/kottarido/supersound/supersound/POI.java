package com.unipi.kottarido.supersound.supersound;

import android.support.design.widget.NavigationView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class POI {
    private int Id;
    private String Name;
    private String Category;
    private List<MusicPreference> PoiPreferences;
    private double Latitude;
    private double Longitude;
    private boolean near;

    //kathe obj autis tis ta3is tha antiprosopeuei ena fav POI tou xristi

    public POI(String name, String category, List<MusicPreference> poiPreferences, double latitude, double longitude) {
        Name = name;
        Category = category;
        PoiPreferences = poiPreferences;
        Latitude = latitude;
        Longitude = longitude;
        near = false;

    }

    public POI (String name, String category, String poiPreferences, double latitude, double longitude){
        updatePOI(name,category,poiPreferences,latitude,longitude);
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public List<MusicPreference> getPoiPreferences() {
        return PoiPreferences;
    }

    public void setPoiPreferences(List<MusicPreference> poiPreferences) {
        PoiPreferences = poiPreferences;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public boolean isNear() {
        return near;
    }

    public void setNear(boolean near) {
        this.near = near;
    }

    //epistrefei enan string buffer o opoios periexei
    //oles tis protimisis tou user se ena sigkekrimeno POI
    public  StringBuffer getPoiPreferencesToString(){
        StringBuffer sb = new StringBuffer();
        for (MusicPreference mp : PoiPreferences){
            if (PoiPreferences.indexOf(mp) == 0)
                sb.append(mp.getMusicKind()+",");
            else if (PoiPreferences.indexOf(mp) == PoiPreferences.size()-1)
                sb.append(" "+mp.getMusicKind());
            else
                sb.append(" "+mp.getMusicKind()+",");
        }

        return sb;
    }


    // methodos pou kaleite gia na ginei update ena poi
    public void updatePOI (String name, String category, String poiPreferences, double latitude, double longitude){
        Name = name;
        Category = category;
        Latitude = latitude;
        Longitude = longitude;

        //epanaferi tin poiPreferences list apo jason se kanoniki
        Gson gson = new Gson();
        Type type = new TypeToken<List<MusicPreference>>(){}.getType();
        PoiPreferences = gson.fromJson(poiPreferences,type);
        near = false;
    }
}
