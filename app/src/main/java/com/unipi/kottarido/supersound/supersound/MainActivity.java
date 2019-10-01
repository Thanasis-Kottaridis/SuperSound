package com.unipi.kottarido.supersound.supersound;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

//import static com.unipi.kottarido.supersound.supersound.ShearedPreferencesHealper.loadUserProfile;

public class MainActivity extends AppCompatActivity implements HorizontalRecyclerViewAdapter.OnItemClickListener{

    //EXTRAS FOR INTENT
    public static final String EXTRA_USER_PROFILE = "com.unipi.kottarido.supersound.supersound.EXTRA_EMAIL";
    public static final String EXTRA_PROFILE_UPDATE = "com.unipi.kottarido.supersound.supersound.EXTRA_PROFILE_UPDATE";
    public static final String EXTRA_ONLINE_PLAYLIST = "com.unipi.kottarido.supersound.supersound.EXTRA_ONLINE_PLAYLIST";
    public static final String EXTRA_PLAY_DYNAMIC_PLAYLIST = "com.unipi.kottarido.supersound.supersound.EXTRA_PLAY_DYNAMIC_PLAYLIST";
    //REQUEST CODES
    public static final int GO_TO_MUSIC_PREFERENCES_REQUEST_CODE = 1;
    public static final int GO_TO_ADD_SONG_REQUEST_CODE = 2;
    public static final int GO_TO_POIS_REQUEST_CODE = 3;

    //user Info
    private String Username;
    private String Email;
    private UserProfile myProfile;

    //Online playlists
    private List<Playlist> OnLinePlaylists;
    private List<Playlist> OnLinePlaylistsRecomendation;
    private List<Playlist> NewOnlinePlaylist;

    //Firebase vars
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    //gia to Navication View
    private NavigationView navigationView;
    private Toolbar CustomToolbar;
    private DrawerLayout drawer;

    //Dynamic Playlist view
    private RelativeLayout LocalDynamicPlaylistLayout;

    //Dynamic Online Playlist view
    private RelativeLayout OnlineDynamicPlaylistLayout;

    //OnLinePlaylist HorizontalRecycleView
    private RecyclerView onlinePlaylisRecyclerView;
    private RecyclerView.Adapter onlinePlaylistRecyclerViewAdapter;
    private RecyclerView.LayoutManager onlinePlaylistLayoutManager;

    //online playlist Recommendation HorizontalRecycleView
    private RecyclerView onlinePlaylisRecomendationRecyclerView;
    private RecyclerView.Adapter onlinePlaylistRecomendationRecyclerViewAdapter;
    private RecyclerView.LayoutManager onlinePlaylistRecomendationLayoutManager;

    //recently added online playlist HorizontalRecycleView
    private RecyclerView newOnlinePlaylisRecyclerView;
    private RecyclerView.Adapter newOnlinePlaylistRecyclerViewAdapter;
    private RecyclerView.LayoutManager newOnlinePlaylistLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //arxikopoiw tin lista me tis online playlists
        OnLinePlaylists = new ArrayList<>();
        OnLinePlaylistsRecomendation = new ArrayList<>();
        NewOnlinePlaylist = new ArrayList<>();

        //instantiate FirebaseAuth gia authentication
        //instantiate to instance tis FirebaseDatabase
        // pernei kai to reference stin database apo to opoio tha kanoume read kai write
        //meso tou instance pou dimiourgisame
        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //Elenxos an o user einai sindedemenos
        if (mAuth.getCurrentUser() == null){
            startActivity(new Intent(this,SignInActivity.class));
        }
        else {
            //an o user einai sindedemenos
            FirebaseUser user = mAuth.getCurrentUser();
            if(user != null){
                Username = user.getDisplayName();
                Email = user.getEmail();

                //elenxos an o user exei kanei set up to profile tou
                if (ShearedPreferencesHealper.loadUserProfile(this,Email) != null )
                    myProfile = ShearedPreferencesHealper.loadUserProfile(this,Email);
                else {
                    myProfile = new UserProfile();
                    myProfile.setUserEmail(Email);
                    Intent intent = new Intent(this,SetUpProfileActivity.class);
                    intent.putExtra(EXTRA_USER_PROFILE,myProfile.getProfileToString());
                    startActivity(intent);
                    finish();
                }
            }
        }

        //prosthetei to custom Toolbar sto activity
        CustomToolbar = findViewById(R.id.CustomToolbar);
        CustomToolbar.setTitle("");
        setSupportActionBar(CustomToolbar);

        //prosthetei to menu pou ftia3ame sto custom Activity bar mas ??? to kanei me alo tropo apo ton sinithismeno
        drawer = findViewById(R.id.drawer_layout);

        //auto einai gia na kanei to icon na peristrefete otan emfanizete o drawer ???
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,CustomToolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //arxilopoiei to NavigationView
        navigationView = findViewById(R.id.nav_view);

        //vazw ta stixia tou xristi sta textview tou
        View headerView = navigationView.getHeaderView(0);
        ((TextView)headerView.findViewById(R.id.NavHeaderUsername)).setText(Username);
        ((TextView)headerView.findViewById(R.id.NavHeaderEmail)).setText(Email);

        //ftiaxnw listener gia otan ginete select kapio item tou nav view
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()){
                    //navMenu settings
                    //navMenu logout
                    case R.id.navLogOut:
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                        finish();
                        break;
                    case R.id.navMusicPreferences:
                        intent = new Intent(getApplicationContext(),MusicPreferencesActivity.class);
                        intent.putExtra(EXTRA_USER_PROFILE,myProfile.getProfileToString());
                        intent.putExtra(EXTRA_PROFILE_UPDATE,true);
                        startActivityForResult(intent, GO_TO_MUSIC_PREFERENCES_REQUEST_CODE);
                        break;
                    case R.id.navMyMusic:
                        intent = new Intent(getApplicationContext(),AddSongsActivity.class);
                        intent.putExtra(EXTRA_USER_PROFILE,myProfile.getProfileToString());
                        intent.putExtra(EXTRA_PROFILE_UPDATE,true);
                        startActivityForResult(intent, GO_TO_ADD_SONG_REQUEST_CODE);
                        break;
                    case  R.id.navMyPOIs:
                        intent = new Intent(getApplicationContext(),POIsActivity.class);
                        intent.putExtra(EXTRA_USER_PROFILE,myProfile.getProfileToString());
                        intent.putExtra(EXTRA_PROFILE_UPDATE,true);
                        startActivityForResult(intent, GO_TO_POIS_REQUEST_CODE);
                        break;
                    case  R.id.navAboutUs:
                        ShowMessage("About Us", "SuperSound Dynamic Music Player Application Developers: \n" +
                                "Kottaridis Athanasios AM: P15059 \n" +
                                "Diatsigos Giannis AM: P15037 \n" +
                                "Tsilis Dimitrios AM: P11159");
                        break;
                    case R.id.navPrivacyPolicy:
                        ShowMessage("Privacy Policy","This application has been made for educational purposes and is the Assignment for the semester of the subject Modern Software Technology Issues for the academic year 2018-2019");

                }
                return true;
            }
        });

        //vriskw ta ipolipa views tou activiti
        //Dynamic Local playlist View
        LocalDynamicPlaylistLayout = findViewById(R.id.DynamicLocalPlaylist);

        //event sto onClick tou view
        LocalDynamicPlaylistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MusicPlayerActivity.class);
                intent.putExtra(EXTRA_USER_PROFILE,myProfile.getProfileToString());
                startActivity(intent);
            }
        });

        //Dynamic online playlist view
        OnlineDynamicPlaylistLayout = findViewById(R.id.DynamicOnLinePlaylist);

        //event sto onClick tou view
        OnlineDynamicPlaylistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MusicPlayerActivity.class);
                //tou stelnw to profile tou user gia na mporei na dei ta pois kai ta music preferences
                intent.putExtra(EXTRA_USER_PROFILE,myProfile.getProfileToString());

                //metatrepw tin recommended online playlist
                Gson gson = new Gson();
                String json = gson.toJson(OnLinePlaylistsRecomendation);
                //kai to vazw sto intent
                intent.putExtra(EXTRA_ONLINE_PLAYLIST,json);


                startActivity(intent);
            }
        });



        //kanei display to layout tou horizontal RecyclerView gia tis online playlists
        onlinePlaylisRecyclerView = findViewById(R.id.OnlinePlaylistRecyclerView);
        onlinePlaylistLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        onlinePlaylisRecyclerView.setLayoutManager(onlinePlaylistLayoutManager);
        onlinePlaylistRecyclerViewAdapter = new HorizontalRecyclerViewAdapter(this, OnLinePlaylists);
        onlinePlaylisRecyclerView.setAdapter(onlinePlaylistRecyclerViewAdapter);

        //kanei display to layout tou horizontal RecyclerView gia ta Recommendation
        //ton online playlist
        onlinePlaylisRecomendationRecyclerView = findViewById(R.id.OnlinePlaylistRecommendationRecyclerView);
        onlinePlaylistRecomendationLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        onlinePlaylisRecomendationRecyclerView.setLayoutManager(onlinePlaylistRecomendationLayoutManager);
        onlinePlaylistRecomendationRecyclerViewAdapter = new HorizontalRecyclerViewAdapter(this, OnLinePlaylistsRecomendation);
        onlinePlaylisRecyclerView.setAdapter(onlinePlaylistRecomendationRecyclerViewAdapter);

        //kanei display to tis horizontal RecyclerView me tis recently added playlist
        newOnlinePlaylisRecyclerView = findViewById(R.id.NewOnlinePlaylistRecyclerView);
        newOnlinePlaylistLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        newOnlinePlaylisRecyclerView.setLayoutManager(newOnlinePlaylistLayoutManager);
        newOnlinePlaylistRecyclerViewAdapter = new HorizontalRecyclerViewAdapter(this, NewOnlinePlaylist);
        newOnlinePlaylisRecyclerView.setAdapter(newOnlinePlaylistRecyclerViewAdapter);

        //pigenei sto child "OnlinePlaylist" tis firebase opou einai apothikeumenes oi playlists
        final DatabaseReference onlinePlaylistRef = databaseReference.child("OnlinePlaylist");

        //diavazei tis playlist pou einai apothikeumenes stin firebase
        //ftiaxnoume enan listener gia na diavazei tis playlist
        //kathe fora pou iparxi kapia alagi sta data (prosthiki/tropopoiisi)
        onlinePlaylistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //enimeronei tin OnLinePlaylists
                OnLinePlaylists = new ArrayList<>();

                //kathe fora pou alazei kati sta dedomena  travaei ena snapshot ton child OnlinePlailist
                //kai ma sto epistrefei opou kathe children autou tou snapshot einai mia playlist
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Playlist playlist = ds.getValue(Playlist.class);
                    OnLinePlaylists.add(playlist);

                    for (MusicPreference mp: myProfile.getMyMusicPreferences()){
                        if (playlist.getMusicKind().equals(mp.getMusicKind().toLowerCase()))
                            OnLinePlaylistsRecomendation.add(playlist);
                    }
                }

                //enimeronei tis online playlists ston adapter gia na ta kanei desplay sto Recycle view
                ((HorizontalRecyclerViewAdapter)onlinePlaylistRecyclerViewAdapter).setmPlaylists(OnLinePlaylists);
                onlinePlaylisRecyclerView.setAdapter(onlinePlaylistRecyclerViewAdapter);

                //enimeronei ta Recommendation ston adapter gia na kanei desplay sto recycle view
                ((HorizontalRecyclerViewAdapter)onlinePlaylistRecomendationRecyclerViewAdapter).setmPlaylists(OnLinePlaylistsRecomendation);
                onlinePlaylisRecomendationRecyclerView.setAdapter(onlinePlaylistRecomendationRecyclerViewAdapter);

                //ftiaxnei tin list me ta nea playlist
                try{
                    for (int i = OnLinePlaylists.size()-1; i > OnLinePlaylists.size()-6; i--)
                        NewOnlinePlaylist.add(OnLinePlaylists.get(i));
                }catch (Exception e){ }

                //enimeronei tis new playlist
                ((HorizontalRecyclerViewAdapter)newOnlinePlaylistRecyclerViewAdapter).setmPlaylists(NewOnlinePlaylist);
                newOnlinePlaylisRecyclerView.setAdapter(newOnlinePlaylistRecyclerViewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //event sto onclick tou kathe item tou Horizontal RecyclerView
        //me ton idio tropo pou vazoume event se koumpi
        //mono pou se auti ti periptosi xrisimopoiei ton listener pou ftia3ame
        // ston Adaptrer TOU RECYCLER view
        //*** to kanw me this gt ekana implement to interface sto activity
        //***grafw 3 fores to idio event

        //sto onClick item tis online playlist
        ((HorizontalRecyclerViewAdapter) onlinePlaylistRecyclerViewAdapter).setOnItemClickListener(this);
        //sto onClick item tis online playlist recommendation
        ((HorizontalRecyclerViewAdapter) onlinePlaylistRecomendationRecyclerViewAdapter).setOnItemClickListener(this);
        //sto onclick item tis recently added playlist
        ((HorizontalRecyclerViewAdapter) newOnlinePlaylistRecyclerViewAdapter).setOnItemClickListener(this);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_TO_MUSIC_PREFERENCES_REQUEST_CODE && resultCode ==RESULT_OK){
            myProfile = SetUpProfileActivity.getUserProfile(data);
            ShearedPreferencesHealper.updateUserProfile(getApplicationContext(),myProfile);
            Toast.makeText(getApplicationContext(),"updated",Toast.LENGTH_LONG).show();
        }
        else if(requestCode == GO_TO_ADD_SONG_REQUEST_CODE && resultCode == RESULT_OK){
            myProfile = SetUpProfileActivity.getUserProfile(data);
            ShearedPreferencesHealper.updateUserProfile(getApplicationContext(),myProfile);
            Toast.makeText(getApplicationContext(),"updated",Toast.LENGTH_LONG).show();
        }
        else if (requestCode == GO_TO_POIS_REQUEST_CODE && resultCode == RESULT_OK){
            myProfile = SetUpProfileActivity.getUserProfile(data);
            ShearedPreferencesHealper.updateUserProfile(getApplicationContext(),myProfile);
            Toast.makeText(getApplicationContext(),"updated",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        OnLinePlaylistsRecomendation = new ArrayList<>();

        for (Playlist playlist : OnLinePlaylists){
            for (MusicPreference mp: myProfile.getMyMusicPreferences()){
                if (playlist.getMusicKind().equals(mp.getMusicKind().toLowerCase()))
                    OnLinePlaylistsRecomendation.add(playlist);
            }
        }

        //enimeronei ta Recommendation ston adapter gia na kanei desplay sto recycle view
        ((HorizontalRecyclerViewAdapter)onlinePlaylistRecomendationRecyclerViewAdapter).setmPlaylists(OnLinePlaylistsRecomendation);
        onlinePlaylisRecomendationRecyclerView.setAdapter(onlinePlaylistRecomendationRecyclerViewAdapter);
    }

    //kanei implement to intreface pou ftia3ame sto HorizontalRecyclerViewAdapter
    @Override
    public void onItemClick(Playlist playlist) {
        //metatrepw tin playlist se jason gia na tin stilw sto intent
        Gson gson = new Gson();
        String json = gson.toJson(playlist);

        Intent intent = new Intent(getApplicationContext(),MusicPlayerActivity.class);
        intent.putExtra(EXTRA_ONLINE_PLAYLIST,json);
        startActivity(intent);
    }

    //method gia na kanei display ena message dialog
    public void ShowMessage(String title, String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.show();
    }
}
