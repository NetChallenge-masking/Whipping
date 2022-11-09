package kr.co.whipping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;

import java.util.Locale;

import kr.co.whipping.locationinfo.BeaconMainActivity;
import kr.co.whipping.scan.ScanActivity;
import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;
import kr.co.whipping.search.SearchActivity;


public class HomeActivity extends AppCompatActivity {
    private ImageView searchItemBtn;
    private ImageView checkItemBtn;
    private ImageView basketItemBtn;
    private ImageView locationBtn;
    private ImageView finishHomeBtn;

    TextToSpeech tts;
    int clickCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //레이아웃과 버튼 연결
        searchItemBtn=findViewById(R.id.searchItemBtn);
        checkItemBtn=findViewById(R.id.checkItemBtn);
        basketItemBtn=findViewById(R.id.basketItemBtn);
        locationBtn=findViewById(R.id.locationBtn);
        finishHomeBtn=findViewById(R.id.finishHomeBtn);


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
            @Override
            public void onInit(int i) {

                if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                    tts.setLanguage(Locale.KOREA);     //한국어로 설정
                    tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                    tts.speak("쇼핑을 시작합니다.", TextToSpeech.QUEUE_ADD, null);
                }
            }
        });


        //상품검색
        searchItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DBHelper dbHelper = new DBHelper(HomeActivity.this);
                dbHelper.addItem("4902430232159", "헤드&숄더 두피 토탈 솔루션 가려운 두피케어 샴푸", "지하 1층 샴푸 매대");
                dbHelper.addItem("4902430232160", "케라시스 샴푸", "지하 1층 샴푸 매대");
                dbHelper.addItem("4902430232161", "샴푸", "지하 1층 샴푸 매대");
                talkBack("상품검색",SearchActivity.class);
            }
        });

        //상품확인
        checkItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("상품확인",ScanActivity.class);
            }
        });

        //장바구니
        basketItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("장바구니",CartActivity.class);
            }
        });
        //위치안내
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("위치안내",BeaconMainActivity.class);
            }
        });

        finishHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("쇼핑종료");
            }
        });

    }
    //인텐트 넘겨주는 버튼을 위한 접근성 음성안내 함수
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
    //인텐트 넘겨주는 않는 뒤로가기 또는 종료 버튼을 위한 접근성 음성안내 함수
    public void talkBack(String text){
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
                                finish();
                            }
                            clickCnt = 0; //클릭횟수 0으로 초기화
                        }

                    }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우

                }
            }
        });
    }

}





