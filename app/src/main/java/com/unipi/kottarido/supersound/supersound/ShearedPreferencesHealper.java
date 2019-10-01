package com.unipi.kottarido.supersound.supersound;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShearedPreferencesHealper {

    //preference file
    public static final String PREF_FILE = "shared preferences";
    //user profile key
    public static final String USERS_PROFILE_KEY = "users profile";


    //tin kanw static gia na mporw na tin kalesw xoris antikimeno
    //etsi oste na apofigo to instantiate tou helper kathe fora pou thelw na kanw read i write
    static void saveUserProfile(Context context,UserProfile newProfile){

        //fortonei tin lista me ta profile toun xriston
        List<UserProfile> deviceUsers = loadUserProfile(context);

        //an einai o protos user pou egrafete apo auti tin siskeui
        if (deviceUsers == null){
            deviceUsers = new ArrayList<>();
        }

        //prosthetei stin lista to neo profile
        deviceUsers.add(newProfile);

        //kai tin 3anakanei save sta preferences
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(deviceUsers);
        editor.putString(USERS_PROFILE_KEY,json);
        editor.commit();

    }

    static UserProfile loadUserProfile(Context context,String Email){

        List<UserProfile> deviceUsers = loadUserProfile(context);

        if(deviceUsers != null){
            for (UserProfile dp: deviceUsers) {
                if(dp.getUserEmail().equals(Email))
                    return dp;
            }
        }
        return null;
    }

    static private List<UserProfile> loadUserProfile(Context context){
        //fortonei to pref file
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
        //ftisxnei ena neo gson obj
        Gson gson = new Gson();
        //diavazei tin sting timi pou einai apotikeumenei sto pref file
        String json = preferences.getString(USERS_PROFILE_KEY,null);
        //dilwnw ton tipo pou tha ginei metatropei to string pou diavasa
        Type type = new TypeToken<ArrayList<UserProfile>>(){}.getType();
        //ftiaxnw to gason metatrepontas to string sto type pou orisa
        return gson.fromJson(json,type);
    }

    static public void updateUserProfile(Context context,UserProfile userProfile){
        String userEmail = userProfile.getUserEmail();
        List<UserProfile> deviceUsers = loadUserProfile(context);
        for (UserProfile user: deviceUsers) {
            if (user.getUserEmail().equals(userEmail)){
                deviceUsers.remove(user);
                deviceUsers.add(userProfile);
                break;
            }
        }

        //kai tin 3anakanei save sta preferences
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(deviceUsers);
        editor.putString(USERS_PROFILE_KEY,json);
        editor.commit();
    }


}
