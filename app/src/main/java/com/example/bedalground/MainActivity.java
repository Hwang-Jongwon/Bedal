package com.example.bedalground;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private  MainFragment mainFragment;
    private MapFragment mapFragment;
    private MyPageFragment myPageFragment;

    ImageView img_map, img_main, img_mp;
    TextView tv_map, tv_main, tv_mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        mainFragment = new MainFragment();
        mapFragment = new MapFragment();
        myPageFragment = new MyPageFragment();

        img_map = findViewById(R.id.img_map);
        img_main = findViewById(R.id.img_main);
        img_mp = findViewById(R.id.img_mypage);

        tv_map = findViewById(R.id.tv_map);
        tv_main = findViewById(R.id.tv_main);
        tv_mp = findViewById(R.id.tv_mypage);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, mainFragment).commitAllowingStateLoss();
    }

    public void clickHandler(View view){
        int YELLOW = ContextCompat.getColor(getApplicationContext(), R.color.yellowAccent);
        int BLACK = ContextCompat.getColor(getApplicationContext(), R.color.blackPrimary);
        transaction = fragmentManager.beginTransaction();
        switch (view.getId()){
            case R.id.btn_map_fragment:
                transaction.replace(R.id.frameLayout, mapFragment).commitAllowingStateLoss();
                img_map.setImageResource(R.drawable.map_y);
                img_main.setImageResource(R.drawable.main_b);
                img_mp.setImageResource(R.drawable.mp_b);
                tv_map.setTextColor(YELLOW);
                tv_main.setTextColor(BLACK);
                tv_mp.setTextColor(BLACK);
                break;
            case R.id.btn_main_fragment:
                transaction.replace(R.id.frameLayout, mainFragment).commitAllowingStateLoss();
                img_map.setImageResource(R.drawable.map_b);
                img_main.setImageResource(R.drawable.main_y);
                img_mp.setImageResource(R.drawable.mp_b);
                tv_map.setTextColor(BLACK);
                tv_main.setTextColor(YELLOW);
                tv_mp.setTextColor(BLACK);
                break;
            case R.id.btn_mp_fragment:
                transaction.replace(R.id.frameLayout, myPageFragment).commitAllowingStateLoss();
                img_map.setImageResource(R.drawable.map_b);
                img_main.setImageResource(R.drawable.main_b);
                img_mp.setImageResource(R.drawable.mp_y);
                tv_map.setTextColor(BLACK);
                tv_main.setTextColor(BLACK);
                tv_mp.setTextColor(YELLOW);
                break;
        }
    }
}