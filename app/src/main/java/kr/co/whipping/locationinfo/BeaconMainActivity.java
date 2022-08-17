package kr.co.whipping.locationinfo;

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

                if (!minewBeacons.isEmpty()) { //근처에 비콘이 없을 경우가 아니라면
                    String beaconName = minewBeacons.get(0).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    if (beaconName.equals("beacon1")
                            || (beaconName.equals("beacon2"))) {
                        Log.e("beacon1,2인식" ,"화면 문구 : 트리트먼트, 의약제품 , 음성안내문구 : 왼쪽에 트리트먼트 제품이 있습니다, 오른쪽에 의약제품 및 가그린이 있습니다.");
                        //텍스트 안내
                        setBeaconItemInfo("트리트먼트","의약제품 및 가그린");
                    }
                    else if((beaconName.equals("beacon3"))
                    || (beaconName.equals("beacon4"))){
                        Log.e("beacon3,4인식" ,"화면 문구 : 샴푸, 면도기 , 음성안내문구 : 왼쪽에 삼푸 제품이 있습니다, 오른쪽에 면도기제품이 있습니다.");

                        setBeaconItemInfo("샴푸","면도기");
                    }
                    else if(beaconName.equals("beacon5")){
                        Log.e("beacon5인식" ,"화면 문구 : 샴푸, 면도기 , 음성안내문구 : 왼쪽에 헤어 용품이 있습니다, 오른쪽에 구강 용품이 있습니다.");
                        //텍스트 안내
                        setBeaconItemInfo("헤어용품","구강용품");

                    }
                    else if(beaconName.equals("beacon6")){
                        Log.e("beacon6인식" ,"화면 문구 : 지하1층 , 음성안내문구 : 지하1층 입니다.");
                        setBeaconFacilitiesInfo("지하 1층");

                    }
                    else if(beaconName.equals("beacon7")){
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
        beaconInfo1TextView.setText(saleItem2);
        //음성안내
        beaconInfo1TextView.setContentDescription("추천 상품인"+ saleItem1+" 있습니다.");
        beaconInfo1TextView.setContentDescription("1+1 행사 중인"+ saleItem2+" 있습니다.");
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
