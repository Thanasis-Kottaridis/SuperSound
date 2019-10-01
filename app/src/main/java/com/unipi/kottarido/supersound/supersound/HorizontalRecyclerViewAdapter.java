package com.unipi.kottarido.supersound.supersound;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.HorizontalRecyclerViewHolder> {

    public static final String TAG = "HorizontalRecyclerView";

    private List<Playlist> mPlaylists;
    private OnItemClickListener listener;
    private Context context;

    //o constructor tou adapter
    public HorizontalRecyclerViewAdapter(Context context, List<Playlist> mPlaylists) {
        this.context = context;
        this.mPlaylists = mPlaylists;
    }

    //ftiaxnei ena view pou kanei inflate to layout pou eutia3a gia to kathe item
    @NonNull
    @Override
    public HorizontalRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG,"onCreateViewHolder: called");

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cover_item,viewGroup,false);
        return new HorizontalRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRecyclerViewHolder holder, final int i) {
        Log.d(TAG,"onBindViewHolder: called");
        //metatrepei to url apo string se eikona meso tou glide
        Glide.with(context)
                .asBitmap()
                .load(mPlaylists.get(i).getCoverURL())
                .into(holder.image);
        //vazei to title Text to onoma tis playlist
        holder.title.setText(mPlaylists.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }


    public class HorizontalRecyclerViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title;

        public HorizontalRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null && pos != RecyclerView.NO_POSITION){
                        listener.onItemClick(mPlaylists.get(pos));
                    }
                }
            });

        }
    }

    //gia na ftia3oume event sto onClick tou kathe item tou recycle view
    //prepei na ftia3oume enan listener(interface)

    public interface OnItemClickListener{
        void onItemClick(Playlist playlist);
    }

    // episis dimiourgoume kai tin methodo setOnItemClickListener
    // i opoia dexete ena instance pou kanei implement to onItemClickListener
    // gia na ipoxreosoume opoion to kalei na kanei implement to interface mas

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    //setter tou mPlaylists
    public void setmPlaylists(List<Playlist> mPlaylists) {
        this.mPlaylists = mPlaylists;
    }
}
