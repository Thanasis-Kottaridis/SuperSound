package com.unipi.kottarido.supersound.supersound;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MusicPreferencesActivity extends AppCompatActivity implements TextViewDialogClass.DialogListener {

    public static final String EXTRA_MUSIC_PREFERENCES_LIST = "com.unipi.kottarido.supersound.EXTRA_MUSIC_PREFERENCES_LIST";

    private Intent intent;

    private UserProfile myProfile;
    private List<MusicPreference> myMusicPreferences;

    //gia to RecycleView
    private RecyclerView musicPreferenceView;
    private RecyclerView.Adapter musicPreferenceAdapter;
    private RecyclerView.LayoutManager mlayoutManager;

    //FAB
    private FloatingActionButton AddMusicPreferenceButton;

    //metavliti gia na 3exorisoume an einai proto set up i oxi
    private boolean firstSetUp;
    //metavliti gia na katalavenoume an egine update i oxi
    private boolean updated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_preferences);

        //gia na emfanistei to back sto menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent =getIntent();
        //elenxos an to intent exei extras tote den einai to proto setup
        if(intent.hasExtra(MainActivity.EXTRA_PROFILE_UPDATE)){
            firstSetUp = false;
            updated = false;
            myProfile = SetUpProfileActivity.getUserProfile(intent);
            myMusicPreferences = myProfile.getMyMusicPreferences();
            getSupportActionBar().setTitle("Song Preferences");
        }
        else {
            firstSetUp = true;
            myProfile = SetUpProfileActivity.getUserProfile(intent);
            myMusicPreferences = new ArrayList<>();
            getSupportActionBar().setTitle("Step 1: Set Song Preferences");
        }

        //vriskoume to music preference view
        musicPreferenceView = findViewById(R.id.MusicPreferenceView);

        mlayoutManager = new LinearLayoutManager(this);
        musicPreferenceView.setLayoutManager(mlayoutManager);

        //kanoume instantiate ton adapter pou dimiourgisame
        //kai pername ton adapter sto preference view
        //me auton ton tropo leme pos na xiristei ta items
        musicPreferenceAdapter = new MusicPreferenceAdapter(myMusicPreferences);
        musicPreferenceView.setAdapter(musicPreferenceAdapter);

        //ftiaxnei to FAB
        //arxikopiei to instance tou FAB kai sti sinexia ftiaxnw
        // ena onClick event pou tha emfanizei to Dialog gia na grapsei to idos mousikis o user

        AddMusicPreferenceButton = findViewById(R.id.AddMusicPreferenceButton);
        AddMusicPreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextViewDialogClass dialog = new TextViewDialogClass();
                dialog.setTitle("Add Song Preference!");
                dialog.show(getSupportFragmentManager(),"Add new Song Preference");
            }
        });

//        //on Swipe left event
//        //event gia diagrafi item
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//
//               if(firstSetUp){
//                   //diagrafi to preference pou vriskete stin thesi i
//                   myMusicPreferences.remove(viewHolder.getAdapterPosition());
//               }
//               else {
//                   //diagrafi ola ta songs autou tou idous
//                   List<Song> mySongs = myProfile.getMySong();
//                   for (Song s : mySongs){
//                       if (s.getMusicKind().equals(myMusicPreferences.get(viewHolder.getAdapterPosition()).getMusicKind()))
//                           mySongs.remove(s);
//                   }
//                   //enimeronei to my profile
//                   myProfile.setMySong(mySongs);
//
//                   //diagafi auto to idos apo ola ta pois
//                   List<POI> myPOIs = myProfile.getMyPois();
//                   for (POI poi : myPOIs){
//                       for (MusicPreference mp : poi.getPoiPreferences()) {
//                           if (mp.getMusicKind().equals(myMusicPreferences.get(viewHolder.getAdapterPosition()).getMusicKind())){
//                               List<MusicPreference> poiPrefs = poi.getPoiPreferences();
//                               poiPrefs.remove(mp);
//                               poi.setPoiPreferences(poiPrefs);
//                               break;
//                           }
//                       }
//                   }
//                   myProfile.setMyPois(myPOIs);
//                   //diagrafi to tragoudi apo tin playlist
//
//                   //diagrafi to preference pou vriskete stin thesi i
//                   myMusicPreferences.remove(viewHolder.getAdapterPosition());
//
//                   updated = true;
//               }
//            }
//        }).attachToRecyclerView(musicPreferenceView);


    }

    //sto patima tou apply tou custom message box
    @Override
    public void applyText(String Answer) {
        MusicPreference newPref = new MusicPreference(Answer);
        myMusicPreferences.add(newPref);

        //an einai sto update kai gini isagogi simeni oti ta preferences enimerothikan
        if (!firstSetUp)
            updated=true;

        //enimeronei ta messages ston adapter gia na ta kanei desplay sto Recycle view
        ((MusicPreferenceAdapter)musicPreferenceAdapter).setMyMusicPreferences(myMusicPreferences);
        musicPreferenceView.setAdapter(musicPreferenceAdapter);
    }

    //ftiaxnei to menu sto activity

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    //ti tha ginei sto onclick kathe katigorias tou menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //sto patima tou next button
            case R.id.NextOrSaveButton:

                if(firstSetUp){
                    intent = new Intent(this,AddSongsActivity.class);
                    if(myMusicPreferences.size() > 0){
                        myProfile.setMyMusicPreferences(myMusicPreferences);
                        intent.putExtra(MainActivity.EXTRA_USER_PROFILE,myProfile.getProfileToString());
                        startActivity(intent);
                    }
                    else {
                        ShowMessage("You need to set at least one music preference kind");
                    }
                }
                else {
                    //ti tha kanei sto patima tou save an den einai proto setup!
                    intent = new Intent(this,MainActivity.class);
                    if(myMusicPreferences.size() > 0){
                        if(updated){
                            myProfile.setMyMusicPreferences(myMusicPreferences);
                            intent.putExtra(MainActivity.EXTRA_USER_PROFILE,myProfile.getProfileToString());

                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                    else {
                        ShowMessage("You need to set at least one music preference kind");
                    }

                }
                return true;
            //sto patima tou back button
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("If you do so all the settings at this step will reset")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Are you sure you want to go to a previous step?" );
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ShowMessage(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Alert!");
        builder.setMessage(s);
        builder.show();
    }

}
