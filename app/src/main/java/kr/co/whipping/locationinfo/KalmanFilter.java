package kr.co.whipping.locationinfo;

public class KalmanFilter {
    boolean initialized =false;
    double processNoise=0.005;
    int measurementNoise=20;
    double predictedRSSI=0;
    int errorCovariance=0;
    double errorCovarianceRSSI;

//    KalmanFilter(double processNoise , int mesurementNoise ){
//        this.processNoise=processNoise;
//        this.measurementNoise=mesurementNoise;
//    }

    public double filtering(double rssi){
        double priorRSSI;
        double priorErrorCovariance;
        if (!initialized){
            initialized =true;
            priorRSSI=rssi;
            priorErrorCovariance =1;
        }else{
            priorRSSI = predictedRSSI;
            priorErrorCovariance = errorCovariance + processNoise;
        }
        double kalmanGain = priorErrorCovariance / (priorErrorCovariance + measurementNoise);
        predictedRSSI = priorRSSI + (kalmanGain * (rssi - priorRSSI));
        errorCovarianceRSSI = (1 - kalmanGain) * priorErrorCovariance;

        return predictedRSSI;
    }


}