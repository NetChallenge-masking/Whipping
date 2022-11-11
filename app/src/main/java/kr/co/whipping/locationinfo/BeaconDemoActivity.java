package kr.co.whipping.locationinfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import kr.co.whipping.R;
import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;
import kr.co.whipping.scan.camerascan.InnerCameraActivity;

public class BeaconDemoActivity extends AppCompatActivity {
    TextToSpeech tts;
    int clickCnt=0;
    private TextView beaconInfo1TextView;
    private TextView beaconInfo2TextView;
    private Button itemInfoBtn;
    private Button barcodeInfoBtn;
    private ImageView backBtn;
    private ImageView leftBtn;
    private ImageView rightBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_demoactivity);
        initView();

    }

    private void setTextItem(int clickCnt){
            switch (clickCnt){
                case 0:
                    beaconInfo1TextView.setText("2+1 행사상품 리엔 물들임 새치커버 샴푸450ml(자연갈색)");
                    beaconInfo2TextView.setText("2+1 행사상품 리엔 물들임 새치커버 샴푸450ml(자연갈색)");
                    break;
                case 1:
                    beaconInfo1TextView.setText("2층 매장 입구");
                    beaconInfo2TextView.setText("2층 매장 입구");
                    break;
                case 2:
                    beaconInfo1TextView.setText("잡화");
                    beaconInfo2TextView.setText("계산대");
                    break;
                case 3:
                case 8:
                    beaconInfo1TextView.setText("헤어용품 및 구강용품");
                    beaconInfo2TextView.setText("헤어용품 및 구강용품");
                    break;
                case 4:
                    beaconInfo1TextView.setText("샴푸");
                    beaconInfo2TextView.setText("면도기");
                    break;
                case 5:
                    beaconInfo1TextView.setText("트리트먼트");
                    beaconInfo2TextView.setText("면도기");
                    break;
                case 6:
                    beaconInfo1TextView.setText("트리트먼트");
                    beaconInfo2TextView.setText("의약제품 및 가그린");
                    break;
                case 7:
                    beaconInfo1TextView.setText("2+1 행사상품 리엔 물들임 새치커버 샴푸450ml(자연갈색)");
                    beaconInfo2TextView.setText("2+1 행사상품 리엔 물들임 새치커버 샴푸450ml(자연갈색)");
                    break;
                case 9:
                    beaconInfo1TextView.setText("의약제품 및 가그린");
                    beaconInfo2TextView.setText("트리트먼트");
                    break;
                case 10:
                    beaconInfo1TextView.setText("면도기");
                    beaconInfo2TextView.setText("트리트먼트");
                    break;
                case 11:
                    beaconInfo1TextView.setText("면도기");
                    beaconInfo2TextView.setText("샴푸");
                    break;
                case 12:
                    beaconInfo1TextView.setText("헤어용품 및 구강용품");
                    beaconInfo2TextView.setText("헤어용품 및 구강용품");
                    clickCnt=0;
                    break;
                default:
                    clickCnt=0;
                    beaconInfo1TextView.setText("매대이름1");
                    beaconInfo2TextView.setText("매대이름2");
            }

    }


    private void initView() {
        clickCnt=0;
        backBtn=(ImageView)findViewById(R.id.backBtn);
        beaconInfo1TextView = (TextView) findViewById(R.id.beaconInfo1TextView);
        beaconInfo2TextView = (TextView) findViewById(R.id.beaconInfo2TextView);
        itemInfoBtn = (Button) findViewById(R.id.itemInfoTextView);
        barcodeInfoBtn = (Button) findViewById(R.id.barcodeInfoBtn);
        leftBtn=(ImageView) findViewById(R.id.leftItemBtn);
        rightBtn=(ImageView) findViewById(R.id.rightItemBtn);






        itemInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("상품정보 확인하기", InnerCameraActivity.class);
            }
        });
        barcodeInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("바코드로 상품담기", BarcodeScanActivity.class);
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCnt--;
                if(clickCnt<0 ||clickCnt>12) clickCnt=0;
                setTextItem(clickCnt);
                Log.d("클릭수", String.valueOf(clickCnt));
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCnt++;
                if(clickCnt<0 ||clickCnt>12) clickCnt=0;
                setTextItem(clickCnt);
                Log.d("클릭수", String.valueOf(clickCnt));
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           talkBack("뒤로가기");

                                       }
                                   }

        );


        beaconInfo1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                            tts.setLanguage(Locale.KOREA);     //한국어로 설정
                            tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                            tts.speak(beaconInfo1TextView.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });

            }
        });
        beaconInfo2TextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
                    @Override
                    public void onInit(int i) {

                        if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                            tts.setLanguage(Locale.KOREA);     //한국어로 설정
                            tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                            tts.speak(beaconInfo2TextView.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });

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
    //매대 안내를 위한 비콘 함수
    private void setBeaconItemInfo(String leftItem1,String rightItem2){
        //텍스트 안내
        beaconInfo1TextView.setText(leftItem1);
        beaconInfo2TextView.setText(rightItem2);
        //음성안내
        beaconInfo1TextView.setContentDescription("왼쪽에 "+leftItem1+"이 있습니다.");
        beaconInfo2TextView.setContentDescription("오른쪽에"+rightItem2+"이 있습니다.");
    }
    //편의시설 안내를 위한 비콘 함수
    private void setBeaconFacilitiesInfo(String facilities){
        //텍스트 안내
        beaconInfo1TextView.setText(facilities);
        beaconInfo2TextView.setText(facilities);
        //음성안내
        beaconInfo1TextView.setContentDescription("현재 위치는 " + facilities + "입니다.");
        beaconInfo1TextView.setContentDescription("현재 위치는 " + facilities + "입니다.");
    }
    //행사상품 안내를 위한 비콘 함수
    private void setBeaconSaleInfo(String saleItem1,String saleItem2){
        //텍스트 안내
        beaconInfo1TextView.setText(saleItem1);
        beaconInfo2TextView.setText(saleItem2);
        //음성안내
        beaconInfo1TextView.setContentDescription(saleItem1+" 있습니다.");
        beaconInfo2TextView.setContentDescription( saleItem2+" 있습니다.");
    }


}
