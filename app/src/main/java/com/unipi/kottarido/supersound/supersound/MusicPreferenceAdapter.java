package com.unipi.kottarido.supersound.supersound;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MusicPreferenceAdapter extends RecyclerView.Adapter<MusicPreferenceAdapter.MusicPreferenceHolder> {

    private List<MusicPreference> myMusicPreferences;

    @NonNull
    @Override
    public MusicPreferenceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //ftiaxnoume ena view to opoio tha kanei inflate to item pou ftia3ame
        //to opoio to pernaei san orisma ston music preference holder

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.music_preference_item, viewGroup,false);
        return new MusicPreferenceHolder(itemView);
    }

    //dinei timi sta instances tou MusicPreferenceHolder gia kathe item tis listas myMusicPreferences
    @Override
    public void onBindViewHolder(@NonNull MusicPreferenceHolder holder, int i) {
        MusicPreference preference = myMusicPreferences.get(i);
        holder.MusicPreferenceName.setText(preference.getMusicKind());
        holder.TotalSongs.setText("Total Songs: "+String.valueOf(preference.getTotalSongs()));
    }

    @Override
    public int getItemCount() {
        return myMusicPreferences.size();
    }

    //constructor gia na pernaw ston adapter tin preference list
    public MusicPreferenceAdapter(List<MusicPreference> myMusicPreferences){
        this.myMusicPreferences = myMusicPreferences;
    }

    public void setMyMusicPreferences(List<MusicPreference> myMusicPreferences) {
        this.myMusicPreferences = myMusicPreferences;
    }

    class MusicPreferenceHolder extends RecyclerView.ViewHolder{

        private TextView MusicPreferenceName;
        private TextView TotalSongs;


        public MusicPreferenceHolder(@NonNull View view) {
            super(view);
            MusicPreferenceName = view.findViewById(R.id.SongTitle);
            TotalSongs = view.findViewById(R.id.SongArtist);
        }
    }

}
