package com.kelin.translucentbart;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class FragmentActivity extends AppCompatActivity {

    private static final int[] color = {android.R.color.holo_orange_dark, android.R.color.holo_blue_light,
            android.R.color.holo_purple,
            android.R.color.holo_green_dark};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_common_bar);
        initBottomNavigationView();
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content, ImageOverBarFragment.newInstance());
            transaction.commit();
        }
    }

    private void initBottomNavigationView() {
        BottomNavigationBar mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.phx_guest_main_tab_1_unchecked, "One").setActiveColorResource(color[0]))
                .addItem(new BottomNavigationItem(R.mipmap.phx_guest_main_tab_2_unchecked, "Two").setActiveColorResource(color[1]))
                .addItem(new BottomNavigationItem(R.mipmap.phx_guest_main_tab_3_unchecked, "Three").setActiveColorResource(color[2]))
                .addItem(new BottomNavigationItem(R.mipmap.phx_guest_main_tab_4_unchecked, "Four").setActiveColorResource(color[3]))
                .setFirstSelectedPosition(0)
                .initialise();

        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);

        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 0) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.content, ImageOverBarFragment.newInstance());
                    transaction.commit();
                } else {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.content, CommonBarFragment.newInstance(position));
                    transaction.commit();
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });


    }


}
