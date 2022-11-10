package kr.co.whipping.search;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.whipping.BarcodeImgAdapter;
import kr.co.whipping.Navigation1Activity;
import kr.co.whipping.R;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MyViewHolder> {
    private Context context;
    private List<Item> itemList;

    TextToSpeech tts;
    int clickCnt;

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
        String curName = holder.tvItemName.getText().toString();
        String curLocation = holder.tvItemLocation.getText().toString();

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = view.getContext();
                //음성안내 구현
                tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() { //tts구현
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                            tts.setLanguage(Locale.KOREA);     //한국어로 설정
                            tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                            clickCnt++; //클릭 횟수
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (clickCnt == 1) { //한번 클릭했을 경우 버튼내용 음성안내
                                        tts.speak(curName + "의 위치는 " + curLocation + "입니다. 지도 활성화는 두번 탭하세요", TextToSpeech.QUEUE_ADD, null);
                                    } else if (clickCnt == 2) { //두번 클릭했을 경우 다음 화면으로 intent
                                        Intent intent = new Intent(context, Navigation1Activity.class);
                                        context.startActivity(intent);
                                    }
                                    clickCnt = 0; //클릭횟수 0으로 초기화
                                }

                            }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우

                        }
                    }


                });
            }

        });
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
