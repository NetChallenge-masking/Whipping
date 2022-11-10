package kr.co.whipping.scan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import kr.co.whipping.databinding.ActivityScanBinding;
import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;
import kr.co.whipping.scan.camerascan.InnerCameraActivity;

public class ScanActivity extends AppCompatActivity {
    private ActivityScanBinding binding;
    TextToSpeech tts;
    int clickCnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.itemscanBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                talkBack("상품정보 확인",InnerCameraActivity.class);
            }
        });
        binding.barcordscanBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                talkBack("바코드로 상품담기",BarcodeScanActivity.class);

            }
        });
        binding.scanBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("뒤로가기");
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