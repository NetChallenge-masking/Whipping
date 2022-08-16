package kr.co.whipping.locationinfo;


import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.MinewBeacon;

import java.util.Comparator;

public class UserRssi implements Comparator<MinewBeacon> {
    //가까운 비콘이 무엇인지 파악하느 코드
    @Override
    public int compare(MinewBeacon minewBeacon, MinewBeacon t1) {
        float floatValue1 = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();
        float floatValue2 = t1.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();
        if(floatValue1 < floatValue2){
            return 1;
        }else if(floatValue1 == floatValue2){
            return 0;
        }else {
            return -1;
        }
    }
}
