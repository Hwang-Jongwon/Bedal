package com.example.bedalground;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainFragment extends Fragment implements SensorEventListener {
    private GPSTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    SensorManager mSensorManager;
    Sensor mAccelerometer;

    private long mShakeTime;
    private static final int SHAKE_SKIP_TIME = 500;
    private static final float SHAKE_THRESHORD_GRAVITY= 3.0F;
    private int mShakeCount = 0;

    private View view;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String username, Uid;
    private TextView tv_mylocation;
    private FloatingActionButton btn_plus;
    private SwipeRefreshLayout refreshLayout;

    private RecyclerView re_post;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private HorizontalScrollView svCategory;

    private double latitude, longitude;

    private long now;
    private Date mDate;
    private SimpleDateFormat simpleDateFormat;
    private String currentTime;

    private int i;

    private LinearLayout[] llCategory;
    private ImageView img_hansik, img_zoongsik, img_ilsik, img_chicken, img_pizza, img_fastfood, img_yasik, img_boonsik, img_dosirak, img_coffee;
    private TextView tv_hansik, tv_zoongsik, tv_ilsik, tv_chicken, tv_pizza, tv_fastfood, tv_yasik, tv_boonsik, tv_dosirak, tv_coffee, tv_category;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        context = getActivity();
        view = inflater.inflate(R.layout.activity_main_fragment, container, false);

        init();

        //?????? ????????? ???????????? ??????
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Uid = user.getUid();
        databaseReference.child("users").child(Uid).child("name").get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){

            }else {
                username = task.getResult().getValue().toString();
                SavedSharedPreference.setUserName(context, username);
            }
        });

        //GPS
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }
        getMyLocation();

        makeList("");

        //?????????
        btn_plus.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PlusWriting.class);
            intent.putExtra("MyLocation", tv_mylocation.getText().toString());
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.anim_add_post_slide, R.anim.anim_none);
        });
        int YELLOW = ContextCompat.getColor(context, R.color.yellowAccent);
        int BLACK = ContextCompat.getColor(context, R.color.blackPrimary);
        llCategory[0].setOnClickListener(v -> {
            makeList("??????");
            tv_category.setText("#??????");
            img_hansik.setImageResource(R.drawable.hansik_y);
            img_zoongsik.setImageResource(R.drawable.zoongsik_b);
            img_ilsik.setImageResource(R.drawable.ilsik_b);
            img_chicken.setImageResource(R.drawable.chicken_b);
            img_pizza.setImageResource(R.drawable.pizza_b);
            img_fastfood.setImageResource(R.drawable.hamburger_b);
            img_yasik.setImageResource(R.drawable.yasik_b);
            img_boonsik.setImageResource(R.drawable.boonsik_b);
            img_dosirak.setImageResource(R.drawable.dosirak_b);
            img_coffee.setImageResource(R.drawable.coffee_b);
            tv_hansik.setTextColor(YELLOW);
            tv_zoongsik.setTextColor(BLACK);
            tv_ilsik.setTextColor(BLACK);
            tv_chicken.setTextColor(BLACK);
            tv_pizza.setTextColor(BLACK);
            tv_fastfood.setTextColor(BLACK);
            tv_yasik.setTextColor(BLACK);
            tv_boonsik.setTextColor(BLACK);
            tv_dosirak.setTextColor(BLACK);
            tv_coffee.setTextColor(BLACK);
        });
        llCategory[1].setOnClickListener(v -> {
            makeList("??????");
            tv_category.setText("#??????");
            img_hansik.setImageResource(R.drawable.hansik_b);
            img_zoongsik.setImageResource(R.drawable.zoongsik_y);
            img_ilsik.setImageResource(R.drawable.ilsik_b);
            img_chicken.setImageResource(R.drawable.chicken_b);
            img_pizza.setImageResource(R.drawable.pizza_b);
            img_fastfood.setImageResource(R.drawable.hamburger_b);
            img_yasik.setImageResource(R.drawable.yasik_b);
            img_boonsik.setImageResource(R.drawable.boonsik_b);
            img_dosirak.setImageResource(R.drawable.dosirak_b);
            img_coffee.setImageResource(R.drawable.coffee_b);
            tv_hansik.setTextColor(BLACK);
            tv_zoongsik.setTextColor(YELLOW);
            tv_ilsik.setTextColor(BLACK);
            tv_chicken.setTextColor(BLACK);
            tv_pizza.setTextColor(BLACK);
            tv_fastfood.setTextColor(BLACK);
            tv_yasik.setTextColor(BLACK);
            tv_boonsik.setTextColor(BLACK);
            tv_dosirak.setTextColor(BLACK);
            tv_coffee.setTextColor(BLACK);
        });
        llCategory[2].setOnClickListener(v -> {
            makeList("??????");
            tv_category.setText("#??????");
            img_hansik.setImageResource(R.drawable.hansik_b);
            img_zoongsik.setImageResource(R.drawable.zoongsik_b);
            img_ilsik.setImageResource(R.drawable.ilsik_y);
            img_chicken.setImageResource(R.drawable.chicken_b);
            img_pizza.setImageResource(R.drawable.pizza_b);
            img_fastfood.setImageResource(R.drawable.hamburger_b);
            img_yasik.setImageResource(R.drawable.yasik_b);
            img_boonsik.setImageResource(R.drawable.boonsik_b);
            img_dosirak.setImageResource(R.drawable.dosirak_b);
            img_coffee.setImageResource(R.drawable.coffee_b);
            tv_hansik.setTextColor(BLACK);
            tv_zoongsik.setTextColor(BLACK);
            tv_ilsik.setTextColor(YELLOW);
            tv_chicken.setTextColor(BLACK);
            tv_pizza.setTextColor(BLACK);
            tv_fastfood.setTextColor(BLACK);
            tv_yasik.setTextColor(BLACK);
            tv_boonsik.setTextColor(BLACK);
            tv_dosirak.setTextColor(BLACK);
            tv_coffee.setTextColor(BLACK);
        });
        llCategory[3].setOnClickListener(v -> {
            makeList("??????");
            tv_category.setText("#??????");
            img_hansik.setImageResource(R.drawable.hansik_b);
            img_zoongsik.setImageResource(R.drawable.zoongsik_b);
            img_ilsik.setImageResource(R.drawable.ilsik_b);
            img_chicken.setImageResource(R.drawable.chicken_y);
            img_pizza.setImageResource(R.drawable.pizza_b);
            img_fastfood.setImageResource(R.drawable.hamburger_b);
            img_yasik.setImageResource(R.drawable.yasik_b);
            img_boonsik.setImageResource(R.drawable.boonsik_b);
            img_dosirak.setImageResource(R.drawable.dosirak_b);
            img_coffee.setImageResource(R.drawable.coffee_b);
            tv_hansik.setTextColor(BLACK);
            tv_zoongsik.setTextColor(BLACK);
            tv_ilsik.setTextColor(BLACK);
            tv_chicken.setTextColor(YELLOW);
            tv_pizza.setTextColor(BLACK);
            tv_fastfood.setTextColor(BLACK);
            tv_yasik.setTextColor(BLACK);
            tv_boonsik.setTextColor(BLACK);
            tv_dosirak.setTextColor(BLACK);
            tv_coffee.setTextColor(BLACK);
        });
        llCategory[4].setOnClickListener(v -> {
            makeList("??????");
            tv_category.setText("#??????");
            img_hansik.setImageResource(R.drawable.hansik_b);
            img_zoongsik.setImageResource(R.drawable.zoongsik_b);
            img_ilsik.setImageResource(R.drawable.ilsik_b);
            img_chicken.setImageResource(R.drawable.chicken_b);
            img_pizza.setImageResource(R.drawable.pizza_y);
            img_fastfood.setImageResource(R.drawable.hamburger_b);
            img_yasik.setImageResource(R.drawable.yasik_b);
            img_boonsik.setImageResource(R.drawable.boonsik_b);
            img_dosirak.setImageResource(R.drawable.dosirak_b);
            img_coffee.setImageResource(R.drawable.coffee_b);
            tv_hansik.setTextColor(BLACK);
            tv_zoongsik.setTextColor(BLACK);
            tv_ilsik.setTextColor(BLACK);
            tv_chicken.setTextColor(BLACK);
            tv_pizza.setTextColor(YELLOW);
            tv_fastfood.setTextColor(BLACK);
            tv_yasik.setTextColor(BLACK);
            tv_boonsik.setTextColor(BLACK);
            tv_dosirak.setTextColor(BLACK);
            tv_coffee.setTextColor(BLACK);
        });
        llCategory[5].setOnClickListener(v -> {
            makeList("?????????");
            tv_category.setText("#?????????");
            img_hansik.setImageResource(R.drawable.hansik_b);
            img_zoongsik.setImageResource(R.drawable.zoongsik_b);
            img_ilsik.setImageResource(R.drawable.ilsik_b);
            img_chicken.setImageResource(R.drawable.chicken_b);
            img_pizza.setImageResource(R.drawable.pizza_b);
            img_fastfood.setImageResource(R.drawable.hamburger_y);
            img_yasik.setImageResource(R.drawable.yasik_b);
            img_boonsik.setImageResource(R.drawable.boonsik_b);
            img_dosirak.setImageResource(R.drawable.dosirak_b);
            img_coffee.setImageResource(R.drawable.coffee_b);
            tv_hansik.setTextColor(BLACK);
            tv_zoongsik.setTextColor(BLACK);
            tv_ilsik.setTextColor(BLACK);
            tv_chicken.setTextColor(BLACK);
            tv_pizza.setTextColor(BLACK);
            tv_fastfood.setTextColor(YELLOW);
            tv_yasik.setTextColor(BLACK);
            tv_boonsik.setTextColor(BLACK);
            tv_dosirak.setTextColor(BLACK);
            tv_coffee.setTextColor(BLACK);
        });
        llCategory[6].setOnClickListener(v -> {
            makeList("??????");
            tv_category.setText("#??????");
            img_hansik.setImageResource(R.drawable.hansik_b);
            img_zoongsik.setImageResource(R.drawable.zoongsik_b);
            img_ilsik.setImageResource(R.drawable.ilsik_b);
            img_chicken.setImageResource(R.drawable.chicken_b);
            img_pizza.setImageResource(R.drawable.pizza_b);
            img_fastfood.setImageResource(R.drawable.hamburger_b);
            img_yasik.setImageResource(R.drawable.yasik_y);
            img_boonsik.setImageResource(R.drawable.boonsik_b);
            img_dosirak.setImageResource(R.drawable.dosirak_b);
            img_coffee.setImageResource(R.drawable.coffee_b);
            tv_hansik.setTextColor(BLACK);
            tv_zoongsik.setTextColor(BLACK);
            tv_ilsik.setTextColor(BLACK);
            tv_chicken.setTextColor(BLACK);
            tv_pizza.setTextColor(BLACK);
            tv_fastfood.setTextColor(BLACK);
            tv_yasik.setTextColor(YELLOW);
            tv_boonsik.setTextColor(BLACK);
            tv_dosirak.setTextColor(BLACK);
            tv_coffee.setTextColor(BLACK);
        });
        llCategory[7].setOnClickListener(v -> {
            makeList("??????");
            tv_category.setText("#??????");
            img_hansik.setImageResource(R.drawable.hansik_b);
            img_zoongsik.setImageResource(R.drawable.zoongsik_b);
            img_ilsik.setImageResource(R.drawable.ilsik_b);
            img_chicken.setImageResource(R.drawable.chicken_b);
            img_pizza.setImageResource(R.drawable.pizza_b);
            img_fastfood.setImageResource(R.drawable.hamburger_b);
            img_yasik.setImageResource(R.drawable.yasik_b);
            img_boonsik.setImageResource(R.drawable.boonsik_y);
            img_dosirak.setImageResource(R.drawable.dosirak_b);
            img_coffee.setImageResource(R.drawable.coffee_b);
            tv_hansik.setTextColor(BLACK);
            tv_zoongsik.setTextColor(BLACK);
            tv_ilsik.setTextColor(BLACK);
            tv_chicken.setTextColor(BLACK);
            tv_pizza.setTextColor(BLACK);
            tv_fastfood.setTextColor(BLACK);
            tv_yasik.setTextColor(BLACK);
            tv_boonsik.setTextColor(YELLOW);
            tv_dosirak.setTextColor(BLACK);
            tv_coffee.setTextColor(BLACK);
        });
        llCategory[8].setOnClickListener(v -> {
            makeList("?????????");
            tv_category.setText("#?????????");
            img_hansik.setImageResource(R.drawable.hansik_b);
            img_zoongsik.setImageResource(R.drawable.zoongsik_b);
            img_ilsik.setImageResource(R.drawable.ilsik_b);
            img_chicken.setImageResource(R.drawable.chicken_b);
            img_pizza.setImageResource(R.drawable.pizza_b);
            img_fastfood.setImageResource(R.drawable.hamburger_b);
            img_yasik.setImageResource(R.drawable.yasik_b);
            img_boonsik.setImageResource(R.drawable.boonsik_b);
            img_dosirak.setImageResource(R.drawable.dosirak_y);
            img_coffee.setImageResource(R.drawable.coffee_b);
            tv_hansik.setTextColor(BLACK);
            tv_zoongsik.setTextColor(BLACK);
            tv_ilsik.setTextColor(BLACK);
            tv_chicken.setTextColor(BLACK);
            tv_pizza.setTextColor(BLACK);
            tv_fastfood.setTextColor(BLACK);
            tv_yasik.setTextColor(BLACK);
            tv_boonsik.setTextColor(BLACK);
            tv_dosirak.setTextColor(YELLOW);
            tv_coffee.setTextColor(BLACK);
        });
        llCategory[9].setOnClickListener(v -> {
            makeList("??????");
            tv_category.setText("#??????");
            img_hansik.setImageResource(R.drawable.hansik_b);
            img_zoongsik.setImageResource(R.drawable.zoongsik_b);
            img_ilsik.setImageResource(R.drawable.ilsik_b);
            img_chicken.setImageResource(R.drawable.chicken_b);
            img_pizza.setImageResource(R.drawable.pizza_b);
            img_fastfood.setImageResource(R.drawable.hamburger_b);
            img_yasik.setImageResource(R.drawable.yasik_b);
            img_boonsik.setImageResource(R.drawable.boonsik_b);
            img_dosirak.setImageResource(R.drawable.dosirak_b);
            img_coffee.setImageResource(R.drawable.coffee_y);
            tv_hansik.setTextColor(BLACK);
            tv_zoongsik.setTextColor(BLACK);
            tv_ilsik.setTextColor(BLACK);
            tv_chicken.setTextColor(BLACK);
            tv_pizza.setTextColor(BLACK);
            tv_fastfood.setTextColor(BLACK);
            tv_yasik.setTextColor(BLACK);
            tv_boonsik.setTextColor(BLACK);
            tv_dosirak.setTextColor(BLACK);
            tv_coffee.setTextColor(YELLOW);
        });
        return view;
    }
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    private void init() {
        tv_mylocation = view.findViewById(R.id.tv_mylocation);
        btn_plus = view.findViewById(R.id.btn_plus);

        llCategory = new LinearLayout[10];
        llCategory[0] = view.findViewById(R.id.hansik);
        llCategory[1] = view.findViewById(R.id.zoongsik);
        llCategory[2] = view.findViewById(R.id.ilsik);
        llCategory[3] = view.findViewById(R.id.chicken);
        llCategory[4] = view.findViewById(R.id.pizza);
        llCategory[5] = view.findViewById(R.id.fastfood);
        llCategory[6] = view.findViewById(R.id.yasik);
        llCategory[7] = view.findViewById(R.id.boonsik);
        llCategory[8] = view.findViewById(R.id.dosirak);
        llCategory[9] = view.findViewById(R.id.coffee);

        img_hansik = view.findViewById(R.id.img_hansik);
        img_zoongsik = view.findViewById(R.id.img_zoongsik);
        img_ilsik = view.findViewById(R.id.img_ilsik);
        img_chicken = view.findViewById(R.id.img_chicken);
        img_pizza = view.findViewById(R.id.img_pizza);
        img_fastfood = view.findViewById(R.id.img_fastfood);
        img_yasik = view.findViewById(R.id.img_yasik);
        img_boonsik = view.findViewById(R.id.img_boonsik);
        img_dosirak = view.findViewById(R.id.img_dosirak);
        img_coffee = view.findViewById(R.id.img_coffee);

        tv_hansik = view.findViewById(R.id.tv_hansik);
        tv_zoongsik = view.findViewById(R.id.tv_zoongsik);
        tv_ilsik = view.findViewById(R.id.tv_ilsik);
        tv_chicken = view.findViewById(R.id.tv_chicken);
        tv_pizza = view.findViewById(R.id.tv_pizza);
        tv_fastfood = view.findViewById(R.id.tv_fastfood);
        tv_yasik = view.findViewById(R.id.tv_yasik);
        tv_boonsik = view.findViewById(R.id.tv_boonsik);
        tv_dosirak = view.findViewById(R.id.tv_dosirak);
        tv_coffee = view.findViewById(R.id.tv_coffee);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        tv_category = view.findViewById(R.id.tv_category);
        svCategory = view.findViewById(R.id.svCategory);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeList("");
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void makeList(String str_category) {
        i=0;

        re_post = view.findViewById(R.id.re_post);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        ArrayList<PostItem> items = new ArrayList<>();

        gettime();

        Location locationA = new Location("point A");
        locationA.setLatitude(latitude);
        locationA.setLongitude(longitude);
        Location locationB = new Location("point B");
        Log.e("###", Long.valueOf(currentTime)-10000+"..");

        databaseReference.child("Posting").orderByChild("time").startAt(String.valueOf(Long.valueOf(currentTime)-10000)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String title, sub, category, writer;

                    title = ds.child("title").getValue().toString();
                    sub = ds.child("context").getValue().toString();
                    category = ds.child("category").getValue().toString();
                    writer = ds.child("writer").getValue().toString();

                    long time;
                    time = Long.parseLong(ds.child("time").getValue().toString());

                    double lati, longi;

                    lati=Double.parseDouble(ds.child("latitude").getValue().toString());
                    longi=Double.parseDouble(ds.child("longitude").getValue().toString());
                    locationB.setLatitude(lati);
                    locationB.setLongitude(longi);
                    double distance = locationA.distanceTo(locationB);
                    Log.e("distance", distance+"m");
                    if(distance<=10000.0){
                        if(str_category.equals("")||str_category.equals(category))
                            items.add(new PostItem(title, sub, Long.valueOf(currentTime)-time, distance, writer, ds.getKey()));
                    }
                    re_post.setLayoutManager(linearLayoutManager);
                    re_post.setItemAnimator(new DefaultItemAnimator());

                    recyclerViewAdapter = new RecyclerViewAdapter(items);
                    re_post.setAdapter(recyclerViewAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gettime() {
        // ?????? ?????? ?????????
        now = System.currentTimeMillis();
        mDate = new Date(now);
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        currentTime = simpleDateFormat.format(mDate);
    }

    public void getMyLocation(){
        gpsTracker = new GPSTracker(getActivity());
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        tv_mylocation.setText(address);
    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ???????????????.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //?????? ?????? ????????? ??? ??????
                ;
            }
            else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(context, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    getActivity().finish();


                }else {

                    Toast.makeText(context, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            // 3.  ?????? ?????? ????????? ??? ??????



        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Toast.makeText(context, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(context, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(context, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(context, "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS ????????? ?????????");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            float axisX= event.values[0];
            float axisY = event.values[1];
            float axisZ=event.values[2];

            float gravityX = axisX / SensorManager.GRAVITY_EARTH;
            float gravityY = axisY / SensorManager.GRAVITY_EARTH;
            float gravityZ = axisZ / SensorManager.GRAVITY_EARTH;

            float f = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ;
            double squaredD= Math.sqrt(f);
            float gForce = (float) squaredD;
            if(gForce> SHAKE_THRESHORD_GRAVITY){
                long currentTime = System.currentTimeMillis();
                if(mShakeTime+SHAKE_SKIP_TIME>currentTime)
                    return;
                mShakeTime=currentTime;
                mShakeCount++;
                Log.d("##","onSensorChange : Shake ??????" + mShakeCount);
                executeWhenShakeOccur();
            }
        }
    }

    private void executeWhenShakeOccur() {
        Random random = new Random();
        int randomNum = random.nextInt(10);
        llCategory[randomNum].callOnClick();
        svCategory.scrollTo(140*randomNum, 0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
