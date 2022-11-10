package kr.co.whipping.scan.camerascan;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Objects;

import kr.co.whipping.MainActivity;
import kr.co.whipping.R;
import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;

public class CameraScanActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDaNsgeV__6hcIrCllAdU1XAgj4OV29id4";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private Button camerabackBtn;
    private Button barcordGoBtn;
    private Button readTextBtn;
    private ImageView backBtn;
    TextToSpeech tts;
    int clickCnt;
    WhippingTTS whippingTTS;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerascan);
        readTextBtn=findViewById(R.id.btn_read_text);
        backBtn = findViewById(R.id.camescan_detail_back_btn);
        mImageDetails = findViewById(R.id.image_details);
        camerabackBtn = findViewById(R.id.cameraBackBtn);
        barcordGoBtn = findViewById(R.id.barcordScanGoBtn);

        Intent intent = getIntent(); //텍스트 인식 데이터 수신
        String textDetectionResult = intent.getExtras().getString("textDetectionResult");
        //바코드 스캔으로 바로가기 버튼
        mImageDetails.setText(textDetectionResult);

        tts= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    whippingTTS=new WhippingTTS(textDetectionResult,tts);
                    whippingTTS.ttsResult();
                }
            }
        });

        readTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                        tts.speak("다시듣기 버튼입니다. 활성화하려면 두번 탭하세요.", TextToSpeech.QUEUE_ADD, null);
                                    } else if (clickCnt == 2) { //두번 클릭했을 경우 다음 화면으로 intent
                                        whippingTTS=new WhippingTTS(textDetectionResult,tts);
                                        whippingTTS.ttsResult();
                                    }
                                    clickCnt = 0; //클릭횟수 0으로 초기화
                                }

                            }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우

                        }
                    }
                });
            }
        });
        barcordGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("상품담기",BarcodeScanActivity.class);
            }
        });
        //카메라 스캔으로 바로가기 버튼
        camerabackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("카메라 스캔",InnerCameraActivity.class);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("뒤로가기");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
        tts.shutdown();
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


