package com.grievancesystem.speakout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.models.Inventory;
import com.grievancesystem.speakout.models.Notification;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.viewHolder> {
    List<Inventory> inventories;
    Context context;
    InventoryAdapter.InventoryListener listener;

    public InventoryAdapter(Context context,List<Inventory> inventories){
        this.context=context;
        this.inventories=inventories;
    }

    public void setUpOnInventoryListener(InventoryAdapter.InventoryListener mListener) {
        this.listener = mListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InventoryAdapter.viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_inventory_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Inventory inventory=inventories.get(position);
        holder.item.setText(inventory.getItemName());
        holder.quantity.setText(inventory.getQuantity());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.quantity.setVisibility(View.GONE);
                holder.edit.setVisibility(View.GONE);
                holder.save.setVisibility(View.VISIBLE);
                holder.quanEdit.setVisibility(View.VISIBLE);
                holder.quanEdit.setText(holder.quantity.getText());
                if(listener!=null){
                    listener.editClicked(inventory,position);
                }
            }
        });
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.quanEdit.setVisibility(View.GONE);
                holder.save.setVisibility(View.GONE);
                holder.edit.setVisibility(View.VISIBLE);
                holder.quantity.setVisibility(View.VISIBLE);
                holder.quantity.setText(holder.quanEdit.getText());
                if(listener!=null){
                    listener.saveClicked(inventory,position);
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.cardView.setVisibility(View.GONE);
                if(listener!=null){
                    listener.deleteClicked(inventory,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (inventories != null) ? inventories.size() : 0;
    }

    public interface InventoryListener {
        void editClicked(Inventory inventory,int position);
        void saveClicked(Inventory inventory,int position);
        void deleteClicked(Inventory inventory,int position);
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        TextView item,quantity;
        EditText quanEdit;
        ImageView delete,save,edit;
        CardView cardView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView.findViewById(R.id.itemName);
            quantity=itemView.findViewById(R.id.quantityTextView);
            quanEdit=itemView.findViewById(R.id.quantityEditText);
            delete=itemView.findViewById(R.id.delete);
            save=itemView.findViewById(R.id.save);
            edit=itemView.findViewById(R.id.edit);
            cardView=itemView.findViewById(R.id.cardView);
        }
    }
}
