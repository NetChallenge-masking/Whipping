package kr.co.whipping;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BarcodeImgAdapter extends RecyclerView.Adapter<BarcodeImgAdapter.ViewHolder>{
    private List<Bitmap> img;

    public BarcodeImgAdapter(List<Bitmap> img) {
        this.img = img;
    }

    @NonNull
    @Override
    public BarcodeImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.barcodeimg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeImgAdapter.ViewHolder holder, int position) {

        holder.img.setImageBitmap(img.get(position));
    }

    @Override
    public int getItemCount() {
        return img.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_barcode_background);
        }
    }
}

