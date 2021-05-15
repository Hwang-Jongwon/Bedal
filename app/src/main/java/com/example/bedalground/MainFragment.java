package com.example.bedalground;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MainFragment extends Fragment{
    private GPSTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private View view;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String username, Uid;
    private TextView tv_mylocation;
    private ImageView btn_plus;

    private RecyclerView re_post;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private double latitude, longitude;

    private long now;
    private Date mDate;
    private SimpleDateFormat simpleDateFormat;
    private String currentTime;

    private int i;

    private LinearLayout hansik, zoongsik, ilsik, chicken, pizza, fastfood, yasik, boonsik, dosirak, coffee;
    private ImageView img_hansik, img_zoongsik, img_ilsik, img_chicken, img_pizza, img_fastfood, img_yasik, img_boonsik, img_dosirak, img_coffee;
    private TextView tv_hansik, tv_zoongsik, tv_ilsik, tv_chicken, tv_pizza, tv_fastfood, tv_yasik, tv_boonsik, tv_dosirak, tv_coffee, tv_category;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        context = getActivity();
        view = inflater.inflate(R.layout.activity_main_fragment, container, false);

        init();

        //현재 사용자 가져오는 코드
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Uid = user.getUid();
        databaseReference.child("users").child(Uid).child("name").get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){

            }else {
                username = task.getResult().getValue().toString();
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

        //글쓰기
        btn_plus.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PlusWriting.class);
            intent.putExtra("MyLocation", tv_mylocation.getText().toString());
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        });
        int YELLOW = ContextCompat.getColor(context, R.color.yellowAccent);
        int BLACK = ContextCompat.getColor(context, R.color.blackPrimary);
        hansik.setOnClickListener(v -> {
            makeList("한식");
            tv_category.setText("#한식");
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
        zoongsik.setOnClickListener(v -> {
            makeList("중식");
            tv_category.setText("#중식");
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
        ilsik.setOnClickListener(v -> {
            makeList("일식");
            tv_category.setText("#일식");
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
        chicken.setOnClickListener(v -> {
            makeList("치킨");
            tv_category.setText("#치킨");
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
        pizza.setOnClickListener(v -> {
            makeList("피자");
            tv_category.setText("#피자");
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
        fastfood.setOnClickListener(v -> {
            makeList("햄버거");
            tv_category.setText("#햄버거");
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
        yasik.setOnClickListener(v -> {
            makeList("야식");
            tv_category.setText("#야식");
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
        boonsik.setOnClickListener(v -> {
            makeList("분식");
            tv_category.setText("#분식");
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
        dosirak.setOnClickListener(v -> {
            makeList("도시락");
            tv_category.setText("#도시락");
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
        coffee.setOnClickListener(v -> {
            makeList("카페");
            tv_category.setText("#카페");
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

    private void init() {
        tv_mylocation = view.findViewById(R.id.tv_mylocation);
        btn_plus = view.findViewById(R.id.btn_plus);

        hansik = view.findViewById(R.id.hansik);
        zoongsik = view.findViewById(R.id.zoongsik);
        ilsik = view.findViewById(R.id.ilsik);
        chicken = view.findViewById(R.id.chicken);
        pizza = view.findViewById(R.id.pizza);
        fastfood = view.findViewById(R.id.fastfood);
        yasik = view.findViewById(R.id.yasik);
        boonsik = view.findViewById(R.id.boonsik);
        dosirak = view.findViewById(R.id.dosirak);
        coffee = view.findViewById(R.id.coffee);

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

        tv_category = view.findViewById(R.id.tv_category);
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

        databaseReference.child("Posting").orderByChild("time").startAt(String.valueOf(Long.valueOf(currentTime)-100)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    if(distance<=100.0){
                        if(str_category.equals("")||str_category.equals(category))
                            items.add(new PostItem(title, sub, Long.valueOf(currentTime)-time, distance, writer));
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
        // 현재 시간 구하기
        now = System.currentTimeMillis();
        mDate = new Date(now);
        simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmm");
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

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(context, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    getActivity().finish();


                }else {

                    Toast.makeText(context, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(context, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
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
}
