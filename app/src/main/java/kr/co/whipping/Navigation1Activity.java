package kr.co.whipping;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Navigation1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation1);


        ImageView wanttogo = findViewById(R.id.wanttogo);
        Animation anim = AnimationUtils.loadAnimation
                (getApplicationContext(), R.anim.updown);
        wanttogo.startAnimation(anim);
    }
}