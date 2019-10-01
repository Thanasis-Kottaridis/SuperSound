package com.unipi.kottarido.supersound.supersound;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPoiActivity extends AppCompatActivity {

    //extras
    //gia tin lista me ta preferences tou user
    //tha xrisimopiisoume to extra pou exei oristi stin MusicPreferenceActivity

    //gia to edit poi
    public static final String EXTRA_POI_ID = "com.unipi.kottarido.supersound.EXTRA_POI_ID";
    public static final String EXTRA_POI_NAME = "com.unipi.kottarido.supersound.EXTRA_POI_NAME";
    public static final String EXTRA_POI_CATEGORY = "com.unipi.kottarido.supersound.EXTRA_POI_CATEGORY";
    public static final String EXTRA_POI_LATITUDE = "com.unipi.kottarido.supersound.EXTRA_POI_LATITUDE";
    public static final String EXTRA_POI_LONGITUDE = "com.unipi.kottarido.supersound.EXTRA_POI_LONGITUDE";
    public static final String EXTRA_POI_PREFERENCES = "com.unipi.kottarido.supersound.EXTRA_POI_PREFERENCES";

    private Intent intent;

    //voithitikes metavlites
    private List<MusicPreference> myMusicPreferences;
    boolean[] SetPreferencesSelectedItems;
    //gia to edit
    private List<MusicPreference> oldMusicPreferences;

    //xaraktiristika tou POI
    private String name;
    private String latitude;
    private String longitude;
    private String category;
    private List<MusicPreference> poiPreferences;

    //views
    private TextView title;
    private EditText poiName;
    private EditText poiLatitude;
    private EditText poiLongitude;
    private Button setPoiCategoryButton;
    private Button setPoiPreferencesButton;
    private Button saveButton;
    private Button cancelButton;

    //metavliti gia na kathorisoume an ginete insert i edit enos poi
    private boolean Edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi);


        //kathorizonte ta views
        title = findViewById(R.id.POI_Title_AddPoi);
        poiName = findViewById(R.id.POI_Name_AddPoi);
        poiLatitude = findViewById(R.id.POI_Latitude_AddPoi);
        poiLongitude = findViewById(R.id.POI_Longitude_AddPoi);
        setPoiCategoryButton = findViewById(R.id.POI_SetCategory_AddPoi);
        setPoiPreferencesButton = findViewById(R.id.POI_SetPreferences_AddPoi);
        saveButton = findViewById(R.id.SaveButton_AddPoi);
        cancelButton = findViewById(R.id.CancelButton_AddPoi);

        //elenxoume an auto to activity klithike gia add i gia update a POI
        //pernoume to intent pou klithike to activity
        intent = getIntent();

        //diavazei ta music preferences tou user apo to intent
        //kalontas tin converter class pou dimiourgisame

        Gson gson = new Gson();
        //diavazei tin sting timi pou einai apotikeumenei sto intent
        String json = intent.getStringExtra(MusicPreferencesActivity.EXTRA_MUSIC_PREFERENCES_LIST);
        //dilwnw ton tipo pou tha ginei metatropei to string pou diavasa
        Type type = new TypeToken<List<MusicPreference>>(){}.getType();
        //ftiaxnw to gason metatrepontas to string sto type pou orisa
        myMusicPreferences = gson.fromJson(json,type);

        //orizw to setPreferencesSelectedItems
        //kai to arxikopio me olo false
        SetPreferencesSelectedItems = new boolean[myMusicPreferences.size()];
        for (int i = 0 ; i < myMusicPreferences.size() ; i ++){
            SetPreferencesSelectedItems[i]=false;
        }

        //an sto intent iparxei apothikeumenei i timi tou ID simeni oti klithike gia update
        if (intent.hasExtra(EXTRA_POI_NAME)) {
            //kanei tin metavilti Edit = ture gia na 3eroume oti tha ginei edit stin egrafi
            Edit=true;

            //fortonei ta stixia tou poi apo to intent
            name = intent.getStringExtra(EXTRA_POI_NAME);
            latitude = String.valueOf(intent.getDoubleExtra(EXTRA_POI_LATITUDE,0));
            longitude = String.valueOf(intent.getDoubleExtra(EXTRA_POI_LONGITUDE,0));
            category = intent.getStringExtra(EXTRA_POI_CATEGORY);

            //provalei ta stixeia tou poi sto view item
            poiName.setText(name);
            poiLatitude.setText(latitude);
            poiLongitude.setText(longitude);
            setPoiCategoryButton.setText("Category: "+ category);

            //fortonei ta poi preferences kai ta provalei san text sto koumpi... episis ta dixnei kai san proepilogi
            // sto multiChoice dialog
            setPoiPreferencesButton.setText("Preferences: ");
            int counter = 0;

            //fiaxnw to SetSelectied items gia to preferences
            oldMusicPreferences = gson.fromJson(intent.getStringExtra(EXTRA_POI_PREFERENCES),type);
            poiPreferences = new ArrayList<>(oldMusicPreferences);

            for (MusicPreference mp :myMusicPreferences){
                for (MusicPreference oldMp : oldMusicPreferences){
                    if (mp.getMusicKind().equals(oldMp.getMusicKind()) && counter != 0){
                        setPoiPreferencesButton.setText(setPoiPreferencesButton.getText().toString()+", "+mp.getMusicKind());
                        SetPreferencesSelectedItems[myMusicPreferences.indexOf(mp)]=true;
                        counter++;
                    }
                    else if (mp.getMusicKind().equals(oldMp.getMusicKind())){
                        setPoiPreferencesButton.setText(setPoiPreferencesButton.getText().toString()+ mp.getMusicKind());
                        SetPreferencesSelectedItems[myMusicPreferences.indexOf(mp)]=true;
                        counter++;
                    }
                }
            }


            //alazei to title tou activity
            title.setText("Edit Point Of Interest");
        } else {
            //kanei tin Edit false gia na di3ei oti tha finei insert egrafis
            //alazei to title tou activity
            Edit = false;

            //arxikopoiei to poi preferences
            //gia na min skasi an patisis koumpi xoris na exis simplirosi tin forma isagogis poi
            poiPreferences = new ArrayList<>();
            category="";

            title.setText("Add Point Of Interest");
        }

        // sto onClick tou button setCategory
        //anigei ena list dialog sto opoio o user
        //mporei na epile3ei mia apo tis katigories pou einai apothikeumenes sto sistima
        setPoiCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fortonw to array apo ta resources
                final String[] POIsCategories = getResources().getStringArray(R.array.POIsCategories);
                //kai 3ekinaw na ftiaxnw ton builder tou list dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPoiActivity.this);
                builder.setTitle("Select POI Category")
                        .setItems(POIsCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                category = POIsCategories[which];
                                setPoiCategoryButton.setText("Category: "+category);
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        //sto onClick tou button setPreferences
        //Anigei ena multiChoice dialog pou tha provalei ola ta idi protimiseon tou xristi
        //kai autos tha epilegei 1 i perisotera pou epithimi na akouei
        //otan vriskete se auto to POI

        setPoiPreferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //arxikopio to poiPreferences etsi oste na 3ana dimiourgite kathe fora pou patame set Preferences
                poiPreferences = new ArrayList<>();
                final boolean[] tempSelectedItems = Arrays.copyOf(SetPreferencesSelectedItems,myMusicPreferences.size());


                AlertDialog.Builder builder = new AlertDialog.Builder(AddPoiActivity.this);
                //String array gia ta multichoice items tou dialog
                final String[] preferencesArray = new String[myMusicPreferences.size()];
                for(int i = 0; i < myMusicPreferences.size(); i++){
                    preferencesArray[i] = myMusicPreferences.get(i).getMusicKind();
                }

                //set alert dialog title
                builder.setTitle("Select Music Preferences At That POI");
                //set icon
                builder.setIcon(R.drawable.ic_music_preferences);

                builder.setMultiChoiceItems(preferencesArray, tempSelectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //update poia items ginan check
                        tempSelectedItems[which] = isChecked;
                    }
                });

                //ftiaxnw positive kai negative buttons;
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SetPreferencesSelectedItems = tempSelectedItems;
                        setPoiPreferencesButton.setText("Preferences: ");
                        int counter = 0;
                        for(int i = 0; i < myMusicPreferences.size(); i++){
                            if(SetPreferencesSelectedItems[i]){
                                poiPreferences.add(myMusicPreferences.get(i));
                                if (counter != 0 )
                                    setPoiPreferencesButton.setText(setPoiPreferencesButton.getText().toString()+", "+preferencesArray[i]);
                                else
                                    setPoiPreferencesButton.setText(setPoiPreferencesButton.getText().toString()+preferencesArray[i]);
                                counter++;
                            }
                        }
                        if(poiPreferences.isEmpty())
                            setPoiPreferencesButton.setText("Set Preferences");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                }
           });


        //sto patima tou saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavePOI();
            }
        });


        //sto patima tou cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void SavePOI(){
        name = poiName.getText().toString();
        latitude = poiLatitude.getText().toString();
        longitude = poiLongitude.getText().toString();

        //an kapio apo ta paidia einai keno epistrefei minima anepitixous kataxorisis
        if (name.isEmpty() || category.isEmpty() || latitude.isEmpty() || longitude.isEmpty() || poiPreferences.isEmpty()) {
            Toast.makeText(this, "Invalid arguments", Toast.LENGTH_LONG).show();
            return;
        }

        //an prokite na ginei edit iparxousas eggrafis
        if (Edit){
            //elenxei an ala3e kapia timi sta pedia

            if (name.equals(intent.getStringExtra(EXTRA_POI_NAME)) && category.equals(intent.getStringExtra(EXTRA_POI_CATEGORY))
                    && latitude.equals(String.valueOf(intent.getDoubleExtra(EXTRA_POI_LATITUDE, 0.0)))
                    && longitude.equals(String.valueOf(intent.getDoubleExtra(EXTRA_POI_LONGITUDE, 0.0)))
                    && poiPreferences.equals(oldMusicPreferences)){
                //ean den iparxei alagi stin timi
                Toast.makeText(this, "You have to update at least one field", Toast.LENGTH_LONG).show();
                return;
            }
            else {
                //ean iparxei alagi stin timi
                intent = new Intent(this,POIsActivity.class);
                intent.putExtra(EXTRA_POI_NAME,name);
                intent.putExtra(EXTRA_POI_CATEGORY,category);
                intent.putExtra(EXTRA_POI_LATITUDE,Double.valueOf(latitude));
                intent.putExtra(EXTRA_POI_LONGITUDE,Double.valueOf(longitude));
                intent.putExtra(EXTRA_POI_PREFERENCES,new Gson().toJson(poiPreferences));


                setResult(RESULT_OK,intent);
                finish();

            }
        }
        //ean prokite na ginei insert egrafis
        else {
            intent = new Intent(this,POIsActivity.class);
            intent.putExtra(EXTRA_POI_NAME,name);
            intent.putExtra(EXTRA_POI_CATEGORY,category);
            intent.putExtra(EXTRA_POI_LATITUDE,Double.valueOf(latitude));
            intent.putExtra(EXTRA_POI_LONGITUDE,Double.valueOf(longitude));
            intent.putExtra(EXTRA_POI_PREFERENCES,new Gson().toJson(poiPreferences));


            setResult(RESULT_OK,intent);
            finish();
        }

    }

}
