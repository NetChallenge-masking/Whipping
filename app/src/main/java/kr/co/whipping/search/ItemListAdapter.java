package kr.co.whipping.search;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.whipping.BarcodeImgAdapter;
import kr.co.whipping.R;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MyViewHolder> {
    private Context context;
    private List<Item> itemList;

    public ItemListAdapter(ArrayList<Item> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row,parent,false);
        return new ItemListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvItemName.setText(this.itemList.get(position).item_name);
        holder.tvItemLocation.setText(this.itemList.get(position).item_location);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvItemName;
        TextView tvItemLocation;

        public MyViewHolder(View view){
            super(view);
            tvItemName=view.findViewById(R.id.tvItemName);
            tvItemLocation=view.findViewById(R.id.tvItemLocation);
        }
    }
}
