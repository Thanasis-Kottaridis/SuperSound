package com.unipi.kottarido.supersound.supersound;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class POIsAdapter extends RecyclerView.Adapter<POIsAdapter.POIsHolder> {

    //LISTA ME TA POIS
    private List<POI> myPOIs;

    private OnItemClickListener listener;

    @NonNull
    @Override
    public POIsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //ftiaxnoume ena view to opoio tha kanei inflate to item pou ftia3ame
        //to opoio to pernaei san orisma ston holder

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.poi_item, viewGroup,false);
        return new  POIsAdapter.POIsHolder(view);
    }

    //dinei timi sta instances tou Holder gia kathe item tis listas
    @Override
    public void onBindViewHolder(@NonNull POIsHolder holder, int i) {
        POI poi = myPOIs.get(i);
        holder.poiName.setText(poi.getName());
        holder.poiCategory.setText("Category: "+poi.getCategory());
        holder.poiPreferences.setText("POI Preferences: "+ poi.getPoiPreferencesToString());
    }

    @Override
    public int getItemCount() {
        return myPOIs.size();
    }

    //constructor
    public POIsAdapter (List<POI> myPOIs){
        this.myPOIs = myPOIs;
    }

    //myPOIs Setter
    public void setMyPOIs(List<POI> myPOIs) {
        this.myPOIs = myPOIs;
    }

    public class POIsHolder extends RecyclerView.ViewHolder{

        private TextView poiName;
        private TextView poiCategory;
        private TextView poiPreferences;

        public POIsHolder(@NonNull View view) {
            super(view);

            poiName= view.findViewById(R.id.POI_Name);
            poiCategory = view.findViewById(R.id.POI_Category);
            poiPreferences = view.findViewById(R.id.POI_Preferences);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null && pos != RecyclerView.NO_POSITION){
                        listener.onItemClick(myPOIs.get(pos));
                    }
                }
            });
        }
    }

    //ga na kanoume to update sto kathe poi xriazomaste event sto onclick tou Recycle view item
    // giafto ftiaxnoume ena functional interface onItemClickListener

    public interface OnItemClickListener{
        void onItemClick(POI poi);
    }

    // episis dimiourgoume kai tin methodo setOnItemClickListener
    // i opoia dexete ena instance pou kanei implement to onItemClickListener
    // gia na ipoxreosoume opoion to kalei na kanei implement to interface mas

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener =listener;
    }

}
