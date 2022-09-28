package kr.co.whipping;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
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

public class CartActivity extends AppCompatActivity {

    DBHelper dbHelper;
    RecyclerView recyclerView;
    BasketAdapter adapter;

    // tts
    TextToSpeech tts;

    // tts 객체 정리
    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ArrayList<Basket> basketList = new ArrayList<>();

        recyclerView = findViewById(R.id.RecyclerView);
        adapter = new BasketAdapter(basketList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));

        dbHelper = new DBHelper(CartActivity.this);

        Cursor cursor = dbHelper.readAllBasket();
        while(cursor.moveToNext()) {
            Basket basket = new Basket(
                    cursor.getInt(0),  //basket_id
                    cursor.getString(2),  //barcode_id
                    cursor.getString(3),  //barcode_type
                    cursor.getString(4),
                    cursor.getInt(5)  //amount
            );
            basketList.add(basket);

            adapter.notifyDataSetChanged();
        }

        // tts 객체 초기화
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH); // English 로 음성변환 언어 지정
                }
            }
        });

        //tts
        adapter.setItemClickListener(new BasketAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(int position) {
                String text = "앙팡 아기치즈";

                tts.setPitch(1.0f); // 음성 높낮이
                tts.setSpeechRate(1.0f); // 음성 빠르기
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        //상품 삭제 버튼
        Button basket_delete_btn = findViewById(R.id.basket_delete_btn);
        basket_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Basket recyclerItem = adapter.getSelected();
                if (recyclerItem == null) {
                    Toast.makeText(CartActivity.this, "선택된 아이템이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

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
        });

        //상품 수량 추가 버튼
        Button amount_add_btn = findViewById(R.id.amount_add_btn);
        amount_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Basket recyclerItem = adapter.getSelected();
                if (recyclerItem == null) {
                    Toast.makeText(CartActivity.this, "선택된 아이템이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                };

                Basket basket = new Basket(
                        recyclerItem.getBasketId(),  //basket_id
                        recyclerItem.getBarcodeId(),  //barcode_id
                        recyclerItem.getBarcdoeType(),  //barcode_type
                        recyclerItem.getItemName(),
                        recyclerItem.getAmount() + 1  //amount
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
        });

        //상품 수량 감소 버튼
        Button amount_minus_btn = findViewById(R.id.amount_minus_btn);
        amount_minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Basket recyclerItem = adapter.getSelected();
                if (recyclerItem == null) {
                    Toast.makeText(CartActivity.this, "선택된 아이템이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                };

                Basket basket = new Basket(
                        recyclerItem.getBasketId(),  //basket_id
                        recyclerItem.getBarcodeId(),  //barcode_id
                        recyclerItem.getBarcdoeType(),  //barcode_type
                        recyclerItem.getItemName(),
                        recyclerItem.getAmount() - 1  //amount
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
        });
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

    };
}