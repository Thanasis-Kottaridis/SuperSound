package com.unipi.kottarido.supersound.supersound;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class POIsActivity extends AppCompatActivity {

    //request codes
    public static final int ADD_POI_REQUEST = 1;
    public static final int EDIT_POI_REQUEST = 2;

    //extras
    public static final String EXTRA_POIS_LIST = "com.unipi.kottarido.supersound.EXTRA_POIS_LIST";

    private Intent intent;

    private UserProfile myProfile;
    private List<POI> myPOIs;

    //gia to RecycleView
    private RecyclerView poisView;
    private RecyclerView.Adapter poisAdapter;
    private RecyclerView.LayoutManager mlayoutManager;

    //FAB
    private FloatingActionButton AddPOIsButton;


    //metavliti gia na 3exorisoume an einai proto set up i oxi
    private boolean firstSetUp;
    //metavliti gia na 3eroume poio poi ginete edit
    private POI modifyingPoi;
    //gia na 3eroume an ta pois exoun ginei update i oxi
    private boolean updated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pois);

        //gia na emfanistei to back sto menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent =getIntent();
        //elenxos an to intent exei extras tote den einai to proto setup
        if(intent.hasExtra(MainActivity.EXTRA_PROFILE_UPDATE)){
            firstSetUp = false;
            myProfile = SetUpProfileActivity.getUserProfile(intent);
            myPOIs = myProfile.getMyPois();
            getSupportActionBar().setTitle("Points Of Interest");
        }
        else {
            firstSetUp = true;
            myProfile = SetUpProfileActivity.getUserProfile(intent);
            myPOIs = new ArrayList<>();
            getSupportActionBar().setTitle("Step 3: Set Points Of Interest");
        }

        //vriskoume to music preference view
        poisView = findViewById(R.id.POIsView);

        mlayoutManager = new LinearLayoutManager(this);
        poisView.setLayoutManager(mlayoutManager);

        //kanoume instantiate ton adapter pou dimiourgisame
        //kai pername ton adapter sto preference view
        //me auton ton tropo leme pos na xiristei ta items
        poisAdapter = new POIsAdapter(myPOIs);
        poisView.setAdapter(poisAdapter);

        //ftiaxnei to FAB
        //arxikopiei to instance tou FAB kai sti sinexia ftiaxnw
        // ena onClick event pou tha pigenei ton user sto add poi activity

        AddPOIsButton = findViewById(R.id.AddPOIsButton);
        AddPOIsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(),AddPoiActivity.class);
                //kanw to music preference list string xrisimopiontas json
                // gia na to apothikeusw sto intent
                Gson gson = new Gson();
                String json = gson.toJson(myProfile.getMyMusicPreferences());
                intent.putExtra(MusicPreferencesActivity.EXTRA_MUSIC_PREFERENCES_LIST,json);
                startActivityForResult(intent,ADD_POI_REQUEST);
            }
        });

        //on Recycle view item click event einai to interface pou ftia3ame ston adapter
        //to xirizomaste opos xirizomaste kai ta apla onclick event

        ((POIsAdapter) poisAdapter).setOnItemClickListener(new POIsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(POI poi) {

                Intent intent = new Intent(getApplicationContext(),AddPoiActivity.class);
                //pernaei sto intent ta preferences tou user
                Gson gson = new Gson();
                String json = gson.toJson(myProfile.getMyMusicPreferences());
                intent.putExtra(MusicPreferencesActivity.EXTRA_MUSIC_PREFERENCES_LIST,json);

                //pernaei ta stixia tou poi
                intent.putExtra(AddPoiActivity.EXTRA_POI_ID, poi.getId());
                intent.putExtra(AddPoiActivity.EXTRA_POI_NAME, poi.getName());
                intent.putExtra(AddPoiActivity.EXTRA_POI_CATEGORY, poi.getCategory());
                intent.putExtra(AddPoiActivity.EXTRA_POI_PREFERENCES, gson.toJson(poi.getPoiPreferences()));
                intent.putExtra(AddPoiActivity.EXTRA_POI_LATITUDE, poi.getLatitude());
                intent.putExtra(AddPoiActivity.EXTRA_POI_LONGITUDE, poi.getLongitude());

                modifyingPoi = poi;

                startActivityForResult(intent, EDIT_POI_REQUEST);
            }
        });

        //on swipe left
        //event gia na ginete diagrafi ton pois
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i){
                myPOIs.remove(viewHolder.getAdapterPosition());
                updated = true;
            }
    }).attachToRecyclerView(poisView);


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
                    if(myPOIs.size() > 0){
                        myProfile.setMyPois(myPOIs);
                        ShearedPreferencesHealper.saveUserProfile(this,myProfile);
                        startActivity(new Intent(this,MainActivity.class));
                    }
                    else {
                        ShowMessage("You need to add at least one POI");
                    }
                }
                else {
                    if(myPOIs.size() > 0){
                        if (updated){
                            intent = new Intent(this,MainActivity.class);
                            myProfile.setMyPois(myPOIs);
                            intent.putExtra(MainActivity.EXTRA_USER_PROFILE,myProfile.getProfileToString());

                            setResult(RESULT_OK,intent);
                            finish();

                        }
                        else {
                            ShowMessage("You need to add or update at least one POI");
                        }
                    }
                    else {
                        ShowMessage("You need to add at least one POI");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_POI_REQUEST && resultCode == RESULT_OK){

            POI poi = new POI(data.getStringExtra(AddPoiActivity.EXTRA_POI_NAME),
                    data.getStringExtra(AddPoiActivity.EXTRA_POI_CATEGORY),
                    data.getStringExtra(AddPoiActivity.EXTRA_POI_PREFERENCES),
                    data.getDoubleExtra(AddPoiActivity.EXTRA_POI_LATITUDE,0),
                    data.getDoubleExtra(AddPoiActivity.EXTRA_POI_LONGITUDE,0));

            myPOIs.add(poi);

            //an den einai to first setup kanei to updated true
            if(!firstSetUp)
                updated = true;

            //prosthetw to POI sto Recycle View
            ((POIsAdapter) poisAdapter).setMyPOIs(myPOIs);
            poisView.setAdapter(poisAdapter);

        }
        if (requestCode == EDIT_POI_REQUEST && resultCode == RESULT_OK){
            modifyingPoi.updatePOI(data.getStringExtra(AddPoiActivity.EXTRA_POI_NAME),
                    data.getStringExtra(AddPoiActivity.EXTRA_POI_CATEGORY),
                    data.getStringExtra(AddPoiActivity.EXTRA_POI_PREFERENCES),
                    data.getDoubleExtra(AddPoiActivity.EXTRA_POI_LATITUDE,0),
                    data.getDoubleExtra(AddPoiActivity.EXTRA_POI_LONGITUDE,0));

            //an den einai to first setup kanei to updated true
            if(!firstSetUp)
                updated = true;

            //prosthetw to POI sto Recycle View
            ((POIsAdapter) poisAdapter).setMyPOIs(myPOIs);
            poisView.setAdapter(poisAdapter);
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
