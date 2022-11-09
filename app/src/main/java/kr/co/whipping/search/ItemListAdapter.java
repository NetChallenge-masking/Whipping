package kr.co.whipping.search;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.whipping.R;
import kr.co.whipping.search.db.Item;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MyViewHolder> {
    private Context context;
    private List<Item> itemList;

    public ItemListAdapter(Context context){this.context=context;}
    public void setItemList(List<Item> itemList){
        this.itemList=itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_row,parent,false);

        return new MyViewHolder(view);
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
