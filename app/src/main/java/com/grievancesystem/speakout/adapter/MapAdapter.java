package com.grievancesystem.speakout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.models.MapPlaces;

import java.util.List;
import java.util.Map;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.viewHolder> {
    List<MapPlaces> list;
    Context context;
    MapListener mapListener;

    public MapAdapter(Context context,List<MapPlaces> list){
        this.context=context;
        this.list=list;
    }

    public void setOnPlaceClickListener(MapAdapter.MapListener listener){
        this.mapListener=listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MapAdapter.viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_campus_places, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MapPlaces mapPlaces=list.get(position);
        holder.name.setText(mapPlaces.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mapListener!=null){
                    mapListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public interface MapListener{
        void onClick(int position);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CardView cardView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.text);
            cardView=itemView.findViewById(R.id.cardMap);
        }
    }
}
