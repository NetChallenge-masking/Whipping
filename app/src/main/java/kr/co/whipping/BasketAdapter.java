package kr.co.whipping;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class BasketAdapter extends RecyclerView.Adapter<BasketHolder> {

    public int checkedPosition = -1;
    private List<Basket> basketList;


    public interface OnItemClickEventListener {
        void onItemClick(int position);
    }

    private OnItemClickEventListener itemClickListener = null;

//    private OnItemClickEventListener itemClickListener = new OnItemClickEventListener() {
//        @Override
//        public void onItemClick(View view, int position) {
//            notifyItemChanged(checkedPosition, null);
//            checkedPosition = position;
//            notifyItemChanged(position, null);
//        }
//    };


    public void setItemClickListener(OnItemClickEventListener listener) {
        itemClickListener = listener;
    }

    //생성자에서 데이터 리스트 객체를 전달받음.
    BasketAdapter(List<Basket> a_list) {
        basketList = a_list;
    }

    //아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public BasketHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.listitem_cart, parent, false) ;
        BasketHolder vh = new BasketHolder(view, itemClickListener) ;

        return vh ;
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(BasketHolder holder, int position) {
        final Basket item = basketList.get(position);

        final int color;
        if (holder.getAdapterPosition() == checkedPosition) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.green1);
        } else {
            color = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent);

        }
        holder.itemView.setBackgroundColor(color);

        holder.basket_id_tv.setText(String.valueOf(item.getBasketId()));
        holder.barcode_id_tv.setText(String.valueOf(item.getBarcodeId()));
        holder.barcode_type_tv.setText(String.valueOf(item.getBarcdoeType()));
        holder.item_name_tv.setText(String.valueOf(item.getItemName()));
        holder.amount_tv.setText(String.valueOf(item.getAmount()));

    }

    public Basket getSelected() {
        if (checkedPosition > -1) {
            return basketList.get(checkedPosition);
        }
        return null;
    }

    //선택한 아이템 위치
    public int getCheckedPosition() {
        return checkedPosition;
    }

    //선택 초기화
    public void clearSelected() {
        checkedPosition = -1;
    }

    //데이터 개수 리턴
    @Override
    public int getItemCount() {
        return basketList.size() ;
    }

    //데이터 추가
    public void addItem(Basket item) {
        basketList.add(item);
    }
}

class BasketHolder extends RecyclerView.ViewHolder {

    TextView basket_id_tv, barcode_id_tv, barcode_type_tv, item_name_tv, amount_tv;

    public BasketHolder(View view, final BasketAdapter.OnItemClickEventListener itemClickListener) {
        super(view);

        basket_id_tv = itemView.findViewById(R.id.basket_id_tv);
        barcode_id_tv = itemView.findViewById(R.id.barcode_id_tv);
        barcode_type_tv = itemView.findViewById(R.id.barcode_type_tv);
        item_name_tv = itemView.findViewById(R.id.item_name_tv);
        amount_tv = itemView.findViewById(R.id.amount_tv);

        // Click event
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {
                final int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
//                    itemClickListener.onItemClick(a_view, position);
                    if(itemClickListener != null)
                        itemClickListener.onItemClick(position);
                }
            }
        });

    }
}
