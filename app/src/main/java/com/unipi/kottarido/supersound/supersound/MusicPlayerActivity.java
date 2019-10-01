package com.unipi.kottarido.supersound.supersound;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, LocationListener {

    //REQUEST CODES
    public static final int PERMISSION_REQUEST_CODE = 1;

    private Intent intent;

    private UserProfile myProfile;
    private List<Song> mySongs;
    private List<POI> myPOIs;

    //Dynamic Local Playlist
    private List<Song> dynamicLocalPlaylist;

    //Dynamic Onlin Playlist
    private List<Playlist> myOnlinePlaylists;
    private List<Playlist> DynamicOnlinePlaylist;
    private int playlistPos;

    //Location Manager
    private LocationManager locationManager;

    //Layout Views
    private ImageView albumCoverImage;
    private TextView songTitle;
    private TextView songArtist;
    private ImageView playPauseButton;
    private ImageView playNext;
    private ImageView playPrevious;
    private SeekBar seekBar;
    private TextView timePlayed;
    private TextView timeRemaining;

    //MediaPlayer
    private MediaPlayer mediaPlayer;
    private int totalTime;

    //flags gia dynamic Playlist
    private boolean playOnline;
    private boolean playOnlineDynamicPlaylist;
    private int songPos;
    private boolean nearPoi;
    private String nearPoiName;
    private boolean newPlaylist;

    //Default Distance Range
    private int distanceRange = 100;

    //seekBar Thread
    private Thread thread;

    //gia Play Online Playlist
    private Playlist onlinePlaylist;

    //LOCATION LISTENER METHODS!!!

    @Override
    public void onLocationChanged(Location location) {

        //Sto alagma topothesias
        //elenxos an einai konta se kapoio POI
        for (POI poi : myPOIs) {
            Location loc = new Location("destination");
            loc.setLatitude(poi.getLatitude());
            loc.setLongitude(poi.getLongitude());

            if (location.distanceTo(loc) < distanceRange && !poi.isNear()) {
                //simenei oti eisai konta se kapoio poi!
                //ftiaxne tin dynamicPlaylist me ta preferences autou tou poi
                poi.setNear(true);
                nearPoiName = poi.getName();
                //emfanizei se poio poi imaste konta
                Toast.makeText(getApplicationContext(),"You are near to the POI: "+nearPoiName,Toast.LENGTH_LONG).show();

                //ti tha kanei an paizei online Dynamic playlist
                if (playOnline) {
                    DynamicOnlinePlaylist = new ArrayList<>();
                    for (MusicPreference mp : poi.getPoiPreferences()) {
                        for (Playlist playlist : myOnlinePlaylists) {
                            if (playlist.getMusicKind().equals(mp.getMusicKind().toLowerCase())) {
                                DynamicOnlinePlaylist.add(playlist);
                            }
                        }
                        playlistPos = 0;
                        songPos = 0;
                        if (mediaPlayer.isPlaying()) {
                            newPlaylist = true;
                        }
                        else {
                            playOnlineSong();
                            Glide.with(this)
                                    .asBitmap()
                                    .load(DynamicOnlinePlaylist.get(playlistPos).getCoverURL())
                                    .into(albumCoverImage);
                        }
                    }
                    break;
                }
                else {
                    //ti tha kanei an paizei local dynamic playlist
                    dynamicLocalPlaylist = new ArrayList<>();

                    for (MusicPreference mp : poi.getPoiPreferences()) {
                        for (Song song : mySongs) {
                            if (mp.getMusicKind().equals(song.getMusicKind().getMusicKind()))
                                dynamicLocalPlaylist.add(song);
                        }
                    }
                    //anakateuei tin lista
                    Collections.shuffle(dynamicLocalPlaylist);
                    songPos = 0;
                    if (mediaPlayer.isPlaying())
                        newPlaylist = true;
                    else
                        playLocalSong();
                    break;
                }
            }
            //an stamatisei na einai konta sto poi tote kanei to is.Near false
            else if (location.distanceTo(loc) > distanceRange && poi.isNear()) {
                poi.setNear(false);

                if (playOnline){
                    DynamicOnlinePlaylist = myOnlinePlaylists;
                    newPlaylist = true;
                    playlistPos = 0;
                    songPos = 0;
                }
                else
                    dynamicLocalPlaylist = mySongs;

            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        intent = getIntent();

        //location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //find views
        albumCoverImage = findViewById(R.id.SongAlbumCoverImage_MusicPlayer);
        songTitle = findViewById(R.id.SongTitleText_MusicPlayer);
        songArtist = findViewById(R.id.SongArtistText_MusicPlayer);
        playPauseButton = findViewById(R.id.PlayPauseSongButton_MusicPlayer);
        playNext = findViewById(R.id.PlayNext_MusicPlayer);
        playPrevious = findViewById(R.id.PlayPrevious_MusicPlayer);
        seekBar = findViewById(R.id.seekBar);
        timePlayed = findViewById(R.id.TimePlayedText_MusicPlayer);
        timeRemaining = findViewById(R.id.TimeRemainingText_MusicPlayer);

        //kanei set up ton MediaPlayer
        mediaPlayer = new MediaPlayer();
        //dilonei to idos pou tha anaparagei o MediaPlayer
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //orizoume tous listener pou exoun oristei sto telos tis class
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        //elenxos an tha pai3i online playlist i apo local storage
        if (intent.hasExtra(MainActivity.EXTRA_ONLINE_PLAYLIST)) {

            //an prokite gia dynamic anaparagogi playlist
            if (intent.hasExtra(MainActivity.EXTRA_USER_PROFILE)) {
                //ti tha kanei an paizei dynamic online playlist
                playOnline = true;
                playOnlineDynamicPlaylist = true;
                playlistPos = 0;
                songPos = 0;
                myProfile = SetUpProfileActivity.getUserProfile(intent);
                myPOIs = myProfile.getMyPois();
                newPlaylist = false;


                //dexete tin listame tis playlist se json apo to intent
                //kai tin metatrepei se playlists pali
                Gson gson = new Gson();
                String json = intent.getStringExtra(MainActivity.EXTRA_ONLINE_PLAYLIST);
                Type type = new TypeToken<List<Playlist>>() {}.getType();

                //pernw tin online playlist
                myOnlinePlaylists = gson.fromJson(json, type);
                DynamicOnlinePlaylist = myOnlinePlaylists;

                //arxikopoiei to e3ofilo tis playlist
                Glide.with(this)
                        .asBitmap()
                        .load(DynamicOnlinePlaylist.get(playlistPos).getCoverURL())
                        .into(albumCoverImage);

                //elenxos gia location prmission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    EnableGPS();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }


            } else {
                //ti tha kanei otan paizei online playlist
                playOnline = true;
                playOnlineDynamicPlaylist = false;
                songPos = 0;
                //dexete tin playlist se json apo to intent
                //kai tin metatrepei se playlist pali
                Gson gson = new Gson();
                String json = intent.getStringExtra(MainActivity.EXTRA_ONLINE_PLAYLIST);
                Type type = new TypeToken<Playlist>() {
                }.getType();

                //pernw tin online playlist
                onlinePlaylist = gson.fromJson(json, type);

                //vazei to cover tis plailist sto image view
                Glide.with(this)
                        .asBitmap()
                        .load(onlinePlaylist.getCoverURL())
                        .into(albumCoverImage);

                //kalei tin methodo gia na pai3ei online song
                playOnlineSong();
            }
        } else {
            playOnline = false;
            playOnlineDynamicPlaylist = false;
            songPos = 0;
            myProfile = SetUpProfileActivity.getUserProfile(intent);
            mySongs = myProfile.getMySong();
            dynamicLocalPlaylist = mySongs;
            myPOIs = myProfile.getMyPois();
            newPlaylist = false;

            //elenxos gia location prmission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                EnableGPS();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    //an den paizei tragoudi
                    mediaPlayer.start();
                    playPauseButton.setImageResource(R.drawable.ic_pause_circle);
                } else {
                    mediaPlayer.pause();
                    playPauseButton.setImageResource(R.drawable.ic_play_circle);
                }
            }
        });

        //sto onClick tou playNext button
        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextMethod();
            }
        });

        //sto onClick tou PlayPrev button
        playPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousMethod();
            }
        });

        //Seek Bar
        //rithmizoume tin seek bar na proxoraei mazi me to tragoudi
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
            }
        });

        //Thread (Update seek bar and time Labels)

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                    }
                }
            }
        });

        thread.start();


    }

    //ftiaxnoume ton handler pou elenxei to thread
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            //update seek bar
            seekBar.setProgress(currentPosition);

            //update labels
            String playedTime = createTimeLabel(currentPosition);
            timePlayed.setText(playedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
            timeRemaining.setText("- " + remainingTime);
        }
    };

    //methodos pou ftiaxnei ti timi gia ta time labels
    // diladi ftiaxnei to format ton timon pou tha emfanizonte sta labels
    private String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    public void playLocalSong() {
        //kanw reset ton player
        mediaPlayer.reset();

        //vres to tragoudi apo tin list
        Song playSong = dynamicLocalPlaylist.get(songPos);
        //vriskei to uri tou tragoudiou apo to storage
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, playSong.getId());

        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        //vazei ton titlo tou song sto SongTitle text view
        songTitle.setText(playSong.getTitle());
        songArtist.setText(playSong.getArtist());

        //kanei prepare to media player
        mediaPlayer.prepareAsync();
    }

    //methodos gia anaparagogi tragoudion meso
    //online url
    private void playOnlineSong() {
        //kanw reset ton player
        mediaPlayer.reset();
        //vriskw to song pou tha pai3ei
        OnlineSong song;
        //an einai na pai3ei online dynamic playlist
        if (playOnlineDynamicPlaylist) {
            Playlist playlist = DynamicOnlinePlaylist.get(playlistPos);
            song = playlist.getOnlineSongList().get(songPos);
        }
        //an einai na pai3ei apla mia online playlist
        else {
            song = onlinePlaylist.getOnlineSongList().get(songPos);
        }
        //vriskw to url tou tragoudiou pou prokite na pai3i
        String songUrl = song.getUrl();

        try {
            mediaPlayer.setDataSource(songUrl);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());


        //kanei prepare to media player
        mediaPlayer.prepareAsync();

    }

    private void EnableGPS() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {

        }
    }

    private void DisableGps() {
        try {
            locationManager.removeUpdates(this);
            locationManager = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //on Request Permission Result
    //event pou ektelite otan o xristis dosei apantisei gia ta permission
    //pou tou zitithikan apo tin euarmogi

    //TO String[] permission einai ena array pou periexei ola ta permission pou exoume zitisei(sti periptosi mas 1)
    // to int[] grantResults periexei tis apantisis tou xristi sxetika me tin adia ton permition pou zitisame
    // kai einai panta oses kai ta permissions pou zitisame

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EnableGPS();
            } else {
                Toast.makeText(this, getString(R.string.noAccessFineLocationPermission), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    //MediaPlayer methods
    //ti tha ginete molis telionei ena tragoudi
    @Override
    public void onCompletion(MediaPlayer mp) {

        //an paizei online songs
        playNextMethod();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback otan exei fortothei to trafoudi
        mediaPlayer.seekTo(0);
        //diavazei ton xrono diarkias tou tragoudiou
        totalTime = mediaPlayer.getDuration();
        //vazei ton xrono tou tragoudiou sto seekbar
        seekBar.setMax(totalTime);

        mediaPlayer.start();
        playPauseButton.setImageResource(R.drawable.ic_pause_circle);
    }

    //ti tha ginete sto patima to next button
    private void playNextMethod() {
        //an paizei online playlis
        if (playOnline) {
            //an paizi dinamika online playlist
            if (playOnlineDynamicPlaylist) {
                if (newPlaylist) {
                    newPlaylist = false;
                    //enimerosi tou playlist cover
                    Glide.with(this)
                            .asBitmap()
                            .load(DynamicOnlinePlaylist.get(playlistPos).getCoverURL())
                            .into(albumCoverImage);
                }
                else {
                    songPos++;
                    if (songPos > DynamicOnlinePlaylist.get(playlistPos).getOnlineSongList().size() - 1) {
                        songPos = 0;
                        playlistPos++;
                        if (playlistPos > DynamicOnlinePlaylist.size() - 1)
                            playlistPos = 0;

                        //enimerosi tou playlist cover tou epomenou playlist
                        Glide.with(this)
                                .asBitmap()
                                .load(DynamicOnlinePlaylist.get(playlistPos).getCoverURL())
                                .into(albumCoverImage);
                    }
                }

            }
            //paizei apla mia online playlist
            else {
                songPos++;
                if (songPos > onlinePlaylist.getOnlineSongList().size() - 1) songPos = 0;
            }
            playOnlineSong();
        }
        //an paizei local playlist
        else {
            if(newPlaylist){
                songPos = 0;
                newPlaylist = false;
            }
            else {
                songPos++;
                //ama to pos einai sto telefteo tragoudi tis lists mas 3anapaei apo to 0
                if ((songPos > dynamicLocalPlaylist.size() - 1))
                    songPos = 0;

            }
            playLocalSong();
        }
    }

    //ti tha ginete sto patima tou previous button
    private void playPreviousMethod() {
        //an paizei online playlist
        if (playOnline) {
            //an paizi dinamika online playlist
            if (playOnlineDynamicPlaylist) {

                if (newPlaylist) {
                    newPlaylist = false;
                    Glide.with(this)
                            .asBitmap()
                            .load(DynamicOnlinePlaylist.get(playlistPos).getCoverURL())
                            .into(albumCoverImage);
                }
                else {
                    songPos--;
                    if (songPos < 0) {
                        playlistPos--;
                        songPos = 0;
                        if ((playlistPos < 0)){
                            playlistPos = DynamicOnlinePlaylist.size() - 1;
                        }

                        Glide.with(this)
                                .asBitmap()
                                .load(DynamicOnlinePlaylist.get(playlistPos).getCoverURL())
                                .into(albumCoverImage);
                    }
                }

            }
            else {
                songPos--;
                if (songPos < 0)
                    songPos = onlinePlaylist.getOnlineSongList().size() - 1;
            }
            playOnlineSong();
        }
        //an paizei local song
        else {

            if (newPlaylist) {
                songPos = 0;
                newPlaylist = false;
            }
            else {
                songPos--;
                //ama imaste sto proto tagoudi tis playlist mas paei sto telefteo
                if (songPos < 0)
                    songPos = dynamicLocalPlaylist.size() - 1;
                else if (newPlaylist) {
                    songPos = 0;
                    newPlaylist = false;
                }
            }
            playLocalSong();
        }
    }

    //methodos gia to back press tou kinitou!!!!
    @Override
    public void onBackPressed() {

        //an paizei online playlist apenergopoiei to gps
        if (!playOnline || playOnlineDynamicPlaylist) {
            //apenegopoiei to gps
            DisableGps();
        }
        mediaPlayer.stop();
        mediaPlayer = null;


        super.onBackPressed();
        finish();
    }
}
