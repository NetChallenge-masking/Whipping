package kr.co.whipping.locationinfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import kr.co.whipping.R;
import kr.co.whipping.locationinfo.demofragment.Fragment1;
import kr.co.whipping.locationinfo.demofragment.Fragment2;
import kr.co.whipping.locationinfo.demofragment.Fragment3;
import kr.co.whipping.locationinfo.demofragment.Fragment4;

public class BeaconDemoActivity extends FragmentActivity {

    private static final int NUM_PAGES=4;
    private ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_demo);

        viewPager2=findViewById(R.id.beaconViewPager);
        pagerAdapter=new ScreenSlidePageAdapter(this);
        viewPager2.setAdapter(pagerAdapter);


    }
    private class ScreenSlidePageAdapter extends FragmentStateAdapter{

        public ScreenSlidePageAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new Fragment1();
                case 1:
                    return new Fragment2();
                case 2:
                    return new Fragment3();

                case 3:
                    return new Fragment4();
                default:
                    return null;
            }


        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
