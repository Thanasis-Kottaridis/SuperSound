package com.unipi.kottarido.supersound.supersound;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddSongsActivity extends AppCompatActivity implements  SpinnerDialogClass.DialogListener{

    //RequestCodes
    public static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;

    //Bundle codes
    public static final String BUNDLE_CODE_TITLE="com.unipi.kottarido.supersound.BUDNLE_CODE_TITLE";
    public static final String BUNDLE_CODE_PREFERENCES_NAME = "com.unipi.kottarido.supersound.BUDLE_CODE_PREFERENCES_NAME";
    public static final String BUNDLE_CODE_SONG_ID = "com.unipi.kottarido.supersound.BUDLE_SONG_ID";

    //EXTRAS
    public static final String EXTRA_SONG_LIST = "com.unipi.kottarido.supersound.EXTRA_SONG_LIST";

    private Intent intent;

    private UserProfile myProfile;
    private List<Song> phoneSongs;
    private List<Song> mySongs;

    //gia to RecycleView
    private RecyclerView songsView;
    private RecyclerView.Adapter addSongsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //gia to spinner
    private Spinner mSpinner;

    //metavliti gia na 3exorisoume an einai proto set up i oxi
    private boolean firstSetUp;
    //gia na 3eroume an exei ginei update i oxi
    private boolean updated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_songs);

        //gia na emfanistei to back sto menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        //elenxos an to intent exei extras tote den einai to proto setup
        if(intent.hasExtra(MainActivity.EXTRA_PROFILE_UPDATE)){
            firstSetUp = false;
            updated = false;
            myProfile = SetUpProfileActivity.getUserProfile(intent);
            mySongs = myProfile.getMySong();
            phoneSongs = new ArrayList<>();
            CheckForPerrmision();
            getSupportActionBar().setTitle("Songs");
        }
        else {
            firstSetUp = true;
            myProfile = SetUpProfileActivity.getUserProfile(intent);
            phoneSongs = new ArrayList<>();
            mySongs = new ArrayList<>();
            CheckForPerrmision();
            getSupportActionBar().setTitle("Step 2: Set Up Your Songs");
        }

        //vriskoume to SongsView
        songsView = findViewById(R.id.SongsView);

        mLayoutManager = new LinearLayoutManager(this);
        songsView.setLayoutManager(mLayoutManager);

        //kanoume instantiate ton adapter pou dimiourgisame
        //kai pername ton adapter sto songs view
        //me auton ton tropo leme pos na xiristei ta items
        addSongsAdapter = new AddSongsAdapter(phoneSongs);
        songsView.setAdapter(addSongsAdapter);

        //ftiaxnw event sto onItemClick tou Recycle view (dil tou interface pou ftia3ame ston adapter)
        //to xrisimopoiooume opos xrisimopioume ta apla onclick

        ((AddSongsAdapter)addSongsAdapter).setOnItemClickListener(new AddSongsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song) {
                ArrayList<String> preferences = new ArrayList<>();
                for (MusicPreference mp : myProfile.getMyMusicPreferences()) {
                    preferences.add(mp.getMusicKind());
                }

                SpinnerDialogClass dialog = new SpinnerDialogClass();
                Bundle args = new Bundle();
                args.putString(BUNDLE_CODE_TITLE,"Set song's music kind: "+song.getMusicKind().getMusicKind());
                args.putStringArrayList(BUNDLE_CODE_PREFERENCES_NAME,preferences);
                args.putLong(BUNDLE_CODE_SONG_ID,song.getId());
                dialog.setArguments(args);

                dialog.show(getSupportFragmentManager(),"Set song's music kind");
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                phoneSongs.get(viewHolder.getAdapterPosition()).setLoadedInApp(false);
                updated = true;

                //show dialog
                ((AddSongsAdapter)addSongsAdapter).setPhoneSongs(phoneSongs);
                songsView.setAdapter(addSongsAdapter);

            }
            }).attachToRecyclerView(songsView);
    }
    //methodos pou elenxei gia permission
    //kai an iparxei to permission kalei tin getSongList
    private void CheckForPerrmision(){
        //elenxos gia permission
        //an exei dothei to read external storage permission apo ton xristi
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            getSongList();
        else
            ActivityCompat.requestPermissions(this,
                    new  String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }

    //TO String[] permission einai ena array pou periexei ola ta permission pou exoume zitisei(sti periptosi mas 1)
    // to int[] grantResults periexei tis apantisis tou xristi sxetika me tin adia ton permition pou zitisame
    // kai einai panta oses kai ta permissions pou zitisame


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongList();
                ((AddSongsAdapter)addSongsAdapter).setPhoneSongs(phoneSongs);
                songsView.setAdapter(addSongsAdapter);
            }
            else{
                Toast.makeText(this,
                        "Read External Stroage permission is nesassary in order to load the songs from your device",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    //methodos pou fortonei ta tragoudia tou kinitou
    private void getSongList(){

        //apotikeuw se ena temp list ta id ton tragoudion
        //pou exoun fortothei stin euarmogi

        List<String> temp = new ArrayList<>();
        for (Song s : mySongs) {
            temp.add(String.valueOf(s.getId()));
        }

        String [] IdArray = new String[temp.size()];
        temp.toArray(IdArray);

        //o content resolver xriazete gia na kanw query ta tragoudia apo tin siskeui
        ContentResolver musicResolver = getContentResolver();
        //to uri den exw katalavi akrivos ti einai
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //ftiaxnei enan cursor me ta apotelesmata pou tha epistrepei to query
        //sto opoio exei oristi sto selection poia tha einai i were sinthiki tou
        //kai sto selectionArgs poies tha einai oi times pou tha parei to erotimatiko
//        IdArray = new String[1];
//        IdArray[0]="1";
//        Cursor musicCursor = musicResolver.query(musicUri,null,MediaStore.Audio.Media._ID +" != ?",IdArray,null);
        Cursor musicCursor = musicResolver.query(musicUri,null,null,null,null,null);

        //i domi tou cursor miazei me pinaka sql
        //elenxoume an ta data pou peirame apo ton cursor einai egkira
        if(musicCursor != null && musicCursor.moveToFirst()){
            //pare tis stiles
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                phoneSongs.add(new Song(thisId, thisTitle, thisArtist));
            }while (musicCursor.moveToNext());

        }


        musicCursor.close();

        //ean den einai proto setUp
        if(!firstSetUp){
            //elenxei pia tragoudia tou kinitou einai idi fortomena stin efarmogi
            for(Song song : phoneSongs){
                for (Song mySong :mySongs){
                    if (song.getId() == mySong.getId()) {
                        song.setLoadedInApp(true);
                        song.setMusicKind(mySong.getMusicKind());
                        break;
                    }
                }
            }
        }


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
            case R.id.NextOrSaveButton:
                if(firstSetUp){
                    for(Song s:phoneSongs){
                        if(s.isLoadedInApp())
                            mySongs.add(s);
                    }
                    if(mySongs.size() > 0){
                        intent = new Intent(this,POIsActivity.class);
                        myProfile.setMySong(mySongs);
                        intent.putExtra(MainActivity.EXTRA_USER_PROFILE,myProfile.getProfileToString());
                        startActivity(intent);
                    }
                    else {
                        ShowMessage("You need to set at least one song to the system");
                    }
                }
                else {
                    mySongs = new ArrayList<>();
                    List<MusicPreference> myMusicPreferences = myProfile.getMyMusicPreferences();
                    for(Song s:phoneSongs){
                        if(s.isLoadedInApp())
                            mySongs.add(s);
                    }

                    for (MusicPreference mp : myMusicPreferences){
                        int counter = 0;
                        for (Song s : mySongs){
                            String s1 = mp.getMusicKind();
                            String s2 = s.getMusicKind().getMusicKind();
                            if (s1.equals(s2)) {
                                counter++;
                            }
                        }
                        mp.setTotalSongs(counter);
                    }
                    if(mySongs.size() > 0){
                        if (updated){
                            intent = new Intent(this,MainActivity.class);
                            myProfile.setMySong(mySongs);
                            myProfile.setMyMusicPreferences(myMusicPreferences);
                            intent.putExtra(MainActivity.EXTRA_USER_PROFILE,myProfile.getProfileToString());

                            setResult(RESULT_OK,intent);
                            finish();
                        }
                        else {
                            ShowMessage("You need to add or update at least one song to the system");
                        }
                    }
                    else {
                        ShowMessage("You need to set at least one song to the system");
                    }
                }

                return true;
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

    //sto patima tou apply tou custom dialog
    @Override
    public void applyText(String[] Answers, long songID) {

        Toast.makeText(this,Answers[0],Toast.LENGTH_LONG).show();
        List<MusicPreference> preferences = myProfile.getMyMusicPreferences();
        for(MusicPreference mp : preferences){
            if(mp.getMusicKind().equals(Answers[0])){
                for (Song s : phoneSongs){
                    if (s.getId() == songID){
                        s.setMusicKind(mp);
                        s.setLoadedInApp(true);
                        break;
                    }
                }
                break;
            }
        }

        if(!this.isFinishing())
        {
            //an den einai sto proto setup tote exei ginei update
            if (!firstSetUp)
                updated = true;

            //show dialog
            ((AddSongsAdapter)addSongsAdapter).setPhoneSongs(phoneSongs);
            songsView.setAdapter(addSongsAdapter);
        }
        else
        {
            Toast.makeText(this,"something went wrong!",Toast.LENGTH_LONG).show();
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
