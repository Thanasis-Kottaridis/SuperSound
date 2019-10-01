package com.unipi.kottarido.supersound.supersound;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SetUpProfileActivity extends AppCompatActivity {

    //REQUEST CODES

    Intent intent;
    private UserProfile myProfile;
    private Button NextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);

        intent = getIntent();
        myProfile = getUserProfile(intent);
        NextButton = findViewById(R.id.NextButton_SetUpProfile);


        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(),MusicPreferencesActivity.class);
                intent.putExtra(MainActivity.EXTRA_USER_PROFILE,myProfile.getProfileToString());
                startActivity(intent);
            }
        });
    }

    //methodos gia na diavazei to userProfile apo to intent
    public static UserProfile getUserProfile(Intent intent){
        //fortwnw apo to to intent to userProfile
        Gson gson = new Gson();
        //diavazei tin sting timi pou einai apotikeumenei sto intent
        String json = intent.getStringExtra(MainActivity.EXTRA_USER_PROFILE);
        //dilwnw ton tipo pou tha ginei metatropei to string pou diavasa
        Type type = new TypeToken<UserProfile>(){}.getType();
        //ftiaxnw to gason metatrepontas to string sto type pou orisa
        return gson.fromJson(json,type);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}
