package kr.co.whipping.scan.camerascan;

import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class WhippingTTS {
    String text;
    int textLength;
    TextToSpeech tts;
    WhippingTTS(String text, TextToSpeech tts){
        this.text=text.toString();
        this.textLength=this.text.length();
        this.tts=tts;
        tts.setSpeechRate(1.0f);
    }

    public void ttsResult(){
        int idx=0;
        String tempText="";

        boolean flag =isAlpha(text.charAt(0))?true:false; //flag가 false 이면 한글 true 이면 영어
        while(idx<textLength){
            char c= text.charAt(idx);
            if(flag==true && isAlpha(c)==true) { //이전 문자가 영어이고 현재 문자가 알파벳 인 경우
                tempText = tempText + c;
                flag = true;
            }
            else if(flag==true && isAlpha(c)==false) {  //이전 문자가 영어이고 현재 문자가 한글 인 경우
                tts.setLanguage(Locale.US);
                tts.speak(tempText, TextToSpeech.QUEUE_ADD, null); //텍스트 결과 출력
                tempText = ""; //빈문자로 초기화
                tempText = tempText + c;
                flag = false;

            }
            else if(flag==false && isAlpha(c)==true){ //이전 문자가 한글이고 현재 문자가 영어인 경우
                tts.setLanguage(Locale.KOREA);
                tts.speak(tempText, TextToSpeech.QUEUE_ADD, null); //텍스트 결과 출력
                tempText=""; //빈문자로 초기화
                tempText = tempText + c;
                flag = true;
            }else{ //이전 문자가 한글이고 현재 문자가 한글인 경우
                tempText = tempText+c;
                flag =false;
            }
            idx++;
        }
        //마지막 문자열 처리
        if(isAlpha(tempText)){
            tts.setLanguage(Locale.US);
            tts.speak(tempText, TextToSpeech.QUEUE_ADD, null); //tts결과 출력
        }
        else{
            tts.setLanguage(Locale.KOREA);
            tts.speak(tempText, TextToSpeech.QUEUE_ADD, null); //tts결과출력
        }

    }

    public static boolean isAlpha(char c){
        //문자가 알파벳인 경우
        if(!((c>='A') && c<='Z') && !(c>='a' && c<='z')){
            return false;
        }
        return true;
    }

    public static boolean isAlpha(String s) {
        return s != null && s.matches("^[a-zA-Z]*$");
    }


}
