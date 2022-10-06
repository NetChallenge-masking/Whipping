package kr.co.whipping.locationinfo;

import static java.lang.Math.pow;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import java.util.Collections;
import java.util.List;

import kr.co.whipping.R;
import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;
import kr.co.whipping.scan.camerascan.CameraScanActivity;

public class BeaconMainActivity extends AppCompatActivity {
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1000;
    private MinewBeaconManager mMinewBeaconManager;
    private TextView beaconInfo1TextView;
    private TextView beaconInfo2TextView;
    private Button itemInfoBtn;
    private Button barcodeInfoBtn;

    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isScanning;


    UserRssi comp = new UserRssi();
    private TextView mStart_scan;
    private boolean mIsRefreshing;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_mainactivity);

        initView();
        initManager();
        checkBluetooth();
        checkLocationPermition();
        initListener();

    }

    private void checkLocationPermition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {

                // 권한 없음
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_FINE_LOCATION);


            } else {

                // ACCESS_FINE_LOCATION 에 대한 권한이 이미 있음.

            }


        }

// OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
        else {

        }
    }

    /**
     * check Bluetooth state
     */
    private void checkBluetooth() {

        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        Log.d("블루투스권한체크",bluetoothState.toString());
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                break;
        }
    }


    private void initView() {

        mStart_scan = (TextView) findViewById(R.id.start_scan);
        beaconInfo1TextView =(TextView) findViewById(R.id.beaconInfo1TextView);
        beaconInfo2TextView =(TextView) findViewById(R.id.beaconInfo2TextView);
        itemInfoBtn = (Button) findViewById(R.id.itemInfoTextView);
        barcodeInfoBtn = (Button) findViewById(R.id.barcodeInfoBtn);

        itemInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), CameraScanActivity.class);
                startActivity(intent);
            }
        });
        barcodeInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), BarcodeScanActivity.class);
                startActivity(intent);
            }
        });

    }
    //비콘 매니저 실행
    private void initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
    }

    //비콘 탐색 시작 버튼
    private void initListener() {
        mStart_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMinewBeaconManager != null) {
                    BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
                    switch (bluetoothState) { //블루투스 권한
                        case BluetoothStateNotSupported:
                            Toast.makeText(BeaconMainActivity.this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case BluetoothStatePowerOff:
                            showBLEDialog();
                            return;
                        case BluetoothStatePowerOn:
                            break;
                    }
                }
                if (isScanning) {
                    isScanning = false;
                    mStart_scan.setText("Start");
                    if (mMinewBeaconManager != null) {
                        mMinewBeaconManager.stopScan();
                    }
                } else {
                    isScanning = true;
                    mStart_scan.setText("Stop");
                    try {
                        mMinewBeaconManager.startScan(); //스캔시작
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {

            /**
             *   if the manager find some new beacon, it will call back this method.
             *
             *  @param minewBeacons  new beacons the manager scanned
             */
            @Override
            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {
                Log.e("onAppearBeacons()","실행");
            }

            /**
             *  if a beacon didn't update data in 10 seconds, we think this beacon is out of rang, the manager will call back this method.
             *
             *  @param minewBeacons beacons out of range
             */
            @Override
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                /*for (MinewBeacon minewBeacon : minewBeacons) {
                    String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    Toast.makeText(getApplicationContext(), deviceName + "  out range", Toast.LENGTH_SHORT).show();
                }*/
            }

            /**
             *  the manager calls back this method every 1 seconds, you can get all scanned beacons.
             *
             *  @param minewBeacons all scanned beacons
             */
            @Override
            public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
                Log.e("onRangeBeacons()","실행");
                if (!minewBeacons.isEmpty()) { //근처에 비콘이 없을 경우가 아니라면
                    Log.e("onRangeBeacons2()","실행");
                    KalmanFilter kalmanFilter= new KalmanFilter();
                    //RSSI값으로 부터 거리를 구하기 위해서 제일 가까운 3개의 비콘의 RSSI값 받아오기
                    double beaconRssi0 = minewBeacons.get(0).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();
                    double beaconRssi1 = minewBeacons.get(1).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();
                    double beaconRssi2 = minewBeacons.get(2).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();

                    //제일 가까운 3개의 RSSI값 받아서 필터링 함수 적용
                    beaconRssi0= kalmanFilter.filtering(beaconRssi0);
                    beaconRssi1 = kalmanFilter.filtering(beaconRssi1);
                    beaconRssi2=kalmanFilter.filtering(beaconRssi2);

                    minewBeacons.get(0).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_InRage);

                    //제일 가까운 비콘 3개의 이름 값 받기
                    String beaconName0 = minewBeacons.get(0).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    String beaconName1 = minewBeacons.get(1).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    String beaconName2 = minewBeacons.get(2).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();

                    //RSSI로 비콘으로 부터 거리계산하기
                    double beaconDistance0 = calculateAccuracy(beaconRssi0);
                    double beaconDistance1 = calculateAccuracy(beaconRssi1);
                    double beaconDistance2 = calculateAccuracy(beaconRssi2);

                    Log.d("비콘정보0: ",beaconName0+", 비콘0의 RSSI"+beaconRssi0+", 비콘0으로 부터 거리"+beaconDistance0);
                    Log.d("비콘정보1: ",beaconName1+", 비콘1의 RSSI"+beaconRssi1+", 비콘1으로 부터 거리"+beaconDistance1);
                    Log.d("비콘정보2: ",beaconName2+", 비콘2의 RSSI"+beaconRssi2+", 비콘2으로 부터 거리"+beaconDistance2);


                    if (beaconName0.equals("beacon1")
                            || (beaconName0.equals("beacon2"))) {
                        Log.e("beacon1,2인식" ,"화면 문구 : 트리트먼트, 의약제품 , 음성안내문구 : 왼쪽에 트리트먼트 제품이 있습니다, 오른쪽에 의약제품 및 가그린이 있습니다.");
                        //텍스트 안내
                        setBeaconItemInfo("트리트먼트","의약제품 및 가그린");
                    }
                    else if(beaconName0.equals("beacon3")){
                        Log.e("beacon3,4인식" ,"화면 문구 : 샴푸, 면도기 , 음성안내문구 : 왼쪽에 삼푸 제품이 있습니다, 오른쪽에 면도기제품이 있습니다.");
                        setBeaconItemInfo("샴푸","면도기");
                    }
                    else if(beaconName0.equals("beacon4")){
                        Log.e("beacon4인식" ,"화면 문구 : 리엔 물들임 트린트먼트150ml(흑갈색),헤드앤숄더 샴푸850ml , 음성안내문구 : 추천,세일 상품안내");
                        setBeaconSaleInfo("추천 상품\n 리엔 물들임 트린트먼트150ml(흑갈색)","1+1 행사상품\n 헤드앤숄더 샴푸850ml");
                    }
                    else if(beaconName0.equals("beacon5")){
                        Log.e("beacon5인식" ,"화면 문구 : 샴푸, 면도기 , 음성안내문구 : 왼쪽에 헤어 용품이 있습니다, 오른쪽에 구강 용품이 있습니다.");
                        //텍스트 안내
                        setBeaconItemInfo("헤어용품","구강용품");

                    }
                    else if(beaconName0.equals("beacon6")){
                        Log.e("beacon6인식" ,"화면 문구 : 지하1층 , 음성안내문구 : 지하1층 입니다.");
                        setBeaconFacilitiesInfo("지하 1층");

                    }
                    else if(beaconName0.equals("beacon7")){
                        Log.e("beacon7인식" ,"화면 문구 : 리엔 물들임 트린트먼트150ml(흑갈색),헤드앤숄더 샴푸850ml , 음성안내문구 : 추천,세일 상품안내");
                        setBeaconSaleInfo("리엔 물들임 트린트먼트150ml(흑갈색)","헤드앤숄더 샴푸850ml");

                    }
                }
            }


            /**
             *  the manager calls back this method when BluetoothStateChanged.
             *
             *  @param state BluetoothState
             */
            //블루투스 켜졌을 경우, 꺼졌을 경우
            @Override
            public void onUpdateState(BluetoothState state) {
                switch (state) {
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    //비콘 RSSI를 이용한 거리값 계산 함수
    //n은 경로 손실 지수로 주변 환경(벽 또는 장애물의 여부 등)에 따라 2~4의 값을 가집니다. 주변에 아무런 장애물도 없다면 경로 손실 지수는 2이며,
    // 장애물의 여부와 개수에 따라 3 또는 4가 될 수 있습니다. d는 거리이고, α는 TX power로 특정 거리에서 수신기로부터 측정된 기본 RSSI 값입니다.
    private static double calculateAccuracy(double rssi){
        if(rssi==1){return -1;}//정확도 알수 없음

        int n=2; //constant N
        int alpha = -63; //rssi at 1m

        return pow(10.0,((alpha-rssi)/(10*n)));
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
        beaconInfo1TextView.setContentDescription("추천 상품인"+ saleItem1+" 있습니다.");
        beaconInfo2TextView.setContentDescription("1+1 행사 중인"+ saleItem2+" 있습니다.");
    }



    //액티비티 종료되면 비콘스캔 종료
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop scan
        if (isScanning) {
            mMinewBeaconManager.stopScan();
        }
    }
    //사용자에게 블루투스 권한받지 않았을 경우
    private void showBLEDialog() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }
    //블루투스 권한을 받지 않았으면 처리하는 코드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                break;
        }
    }
}
