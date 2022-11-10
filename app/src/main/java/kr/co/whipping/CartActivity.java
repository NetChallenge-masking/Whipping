package kr.co.whipping;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import kr.co.whipping.search.SearchActivity;

public class CartActivity extends AppCompatActivity {

    DBHelper dbHelper;
    RecyclerView recyclerView;
    BasketAdapter adapter;

    // tts
    TextToSpeech tts;
    int clickCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //tts구현
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                    tts.setLanguage(Locale.KOREA);     //한국어로 설정
                    tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                }
            }
        });

        ArrayList<Basket> basketList = new ArrayList<>();

        recyclerView = findViewById(R.id.RecyclerView);
        adapter = new BasketAdapter(basketList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));

        dbHelper = new DBHelper(CartActivity.this);

        Cursor cursor = dbHelper.readAllBasket();
        while (cursor.moveToNext()) {
            Basket basket = new Basket(
                    cursor.getInt(0),  //basket_id
                    cursor.getString(2),  //barcode_id
                    cursor.getString(3),  //barcode_type
                    cursor.getString(4),  //item_name
                    cursor.getInt(5),  //amount
                    cursor.getInt(6),  //price
                    cursor.getBlob(7)
            );
            basketList.add(basket);

            adapter.notifyDataSetChanged();
        }

        //tts
        adapter.setItemClickListener(new BasketAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(int position) {

                adapter.checkedPosition = position;
                System.out.println("선택 값: " + adapter.checkedPosition);

                String text = basketList.get(position).getItemName() + " " + basketList.get(position).getAmount() + "개";
                tts.setPitch(1.0f); // 음성 높낮이
                tts.setSpeechRate(0.8f); // 음성 빠르기
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                adapter.notifyDataSetChanged();
            }
        });

        //상품 삭제 버튼
        Button basket_delete_btn = findViewById(R.id.basket_delete_btn);
        basket_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Basket recyclerItem = adapter.getSelected();
                if (recyclerItem == null) {
                    tts.speak("상품 삭제 버튼입니다. 장바구니 상품 항목을 먼저 선택해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                    return;
                }

                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
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
                                        tts.speak("상품 삭제 버튼입니다. 활성화하려면 두번 탭하세요.", TextToSpeech.QUEUE_ADD, null);
                                    } else if (clickCnt == 2) {
                                        tts.speak("상품이 장바구니에서 삭제되었습니다.", TextToSpeech.QUEUE_ADD, null);

                                        //선택한 item 삭제
                                        basketList.remove(recyclerItem);

                                        //List 반영
                                        final int checkedPosition = adapter.getCheckedPosition();
                                        adapter.notifyItemRemoved(checkedPosition);

                                        //선택 항목 초기화
                                        adapter.clearSelected();

                                        //db에서 상품 제거
                                        dbHelper.deleteBasket(recyclerItem.getBasketId());
                                    }
                                    clickCnt = 0; //클릭횟수 0으로 초기화
                                }
                            }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우
                        }
                    }
                });
            }
        });

        //상품 수량 추가 버튼
        Button amount_add_btn = findViewById(R.id.amount_add_btn);
        amount_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Basket recyclerItem = adapter.getSelected();
                if (recyclerItem == null) {
                    tts.speak("상품 수량 추가 버튼입니다. 장바구니 상품 항목을 먼저 선택해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                    return;
                }

                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
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
                                        tts.speak("상품 수량 추가 버튼입니다. 활성화하려면 두번 탭하세요.", TextToSpeech.QUEUE_ADD, null);
                                    } else if (clickCnt == 2) {
                                        tts.speak("상품 수량이 추가되었습니다.", TextToSpeech.QUEUE_ADD, null);

                                        Basket basket = new Basket(
                                                recyclerItem.getBasketId(),  //basket_id
                                                recyclerItem.getBarcodeId(),  //barcode_id
                                                recyclerItem.getBarcdoeType(),  //barcode_type
                                                recyclerItem.getItemName(),
                                                recyclerItem.getAmount() + 1,  //amount
                                                recyclerItem.getPrice(),
                                                recyclerItem.getImg()
                                        );

                                        // 선택한 item 수량 추가
                                        final int checkedPosition = adapter.getCheckedPosition();
                                        basketList.set(checkedPosition, basket);

                                        //List 반영
                                        adapter.notifyItemRemoved(checkedPosition);

                                        //선택 항목 초기화
                                        adapter.clearSelected();

                                        //db에서 상품 수량 추가
                                        dbHelper.addAmount(recyclerItem.getBasketId());
                                    }
                                    clickCnt = 0; //클릭횟수 0으로 초기화
                                }
                            }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우
                        }
                    }
                });
            }
        });

        //상품 수량 감소 버튼
        Button amount_minus_btn = findViewById(R.id.amount_minus_btn);
        amount_minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Basket recyclerItem = adapter.getSelected();
                if (recyclerItem == null) {
                    tts.speak("상품 수량 감소 버튼입니다. 장바구니 상품 항목을 먼저 선택해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                    return;
                }

                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
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
                                        tts.speak("상품 수량 감소 버튼입니다. 활성화하려면 두번 탭하세요.", TextToSpeech.QUEUE_ADD, null);
                                    }
                                    else if (clickCnt == 2) {
                                        tts.speak("상품 수량이 감소되었습니다.", TextToSpeech.QUEUE_ADD, null);

                                        Basket basket = new Basket(
                                                recyclerItem.getBasketId(),  //basket_id
                                                recyclerItem.getBarcodeId(),  //barcode_id
                                                recyclerItem.getBarcdoeType(),  //barcode_type
                                                recyclerItem.getItemName(),  //item_name
                                                recyclerItem.getAmount() - 1,  //amount
                                                recyclerItem.getPrice(),
                                                recyclerItem.getImg()
                                        );

                                        // 선택한 item 수량 감소
                                        final int checkedPosition = adapter.getCheckedPosition();
                                        basketList.set(checkedPosition, basket);

                                        //List 반영
                                        adapter.notifyItemRemoved(checkedPosition);

                                        //선택 항목 초기화
                                        adapter.clearSelected();

                                        //db에서 상품 수량 삭제
                                        dbHelper.minusAmount(recyclerItem.getBasketId());
                                    }
                                    clickCnt = 0; //클릭횟수 0으로 초기화
                                }
                            }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우
                        }
                    }
                });
            }
        });

        //장바구니 상품 총 가격
        Button total_price = findViewById(R.id.total_price);
        total_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = dbHelper.readAllBasket();
                int price = 0;
                while (cursor.moveToNext()) {
                    price += cursor.getInt(6) * cursor.getInt(5);
                }

                String text = "총액은 " + price + "원 입니다.";
                tts.setPitch(1.0f); // 음성 높낮이
                tts.setSpeechRate(1.0f); // 음성 빠르기
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        //바코드 이미지 화면으로 넘어가기
        Button barcode_zip_btn = findViewById(R.id.barcode_zip_btn);
        barcode_zip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                talkBack("바코드 이미지 화면으로 넘어가는", CartBarcodeActivity.class);

            }
        });
    }

    /*tts*/
    public void talkBack(String text,Class intentClassName){
        //음성안내 구현
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
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
                                tts.speak(text +" 버튼입니다. 활성화하려면 두번 탭하세요.", TextToSpeech.QUEUE_ADD, null);
                            } else if (clickCnt == 2) { //두번 클릭했을 경우 다음 화면으로 intent

                                Intent intent = new Intent(getApplicationContext(), intentClassName);
                                startActivity(intent);

                            }
                            clickCnt = 0; //클릭횟수 0으로 초기화
                        }

                    }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우

                }
            }
        });
    }
}