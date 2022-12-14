package com.example.bedalground;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
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


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentActivity mContext;

    private static final String TAG = MapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private MapView mapView = null;
    private Marker currentMarker = null;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated??? FusedLocationApi??? ??????
    private LocationRequest locationRequest;
    private Location mCurrentLocatiion;
    static Location location;
    private FirebaseAuth mAuth;

    private long now;
    private Date mDate;
    private SimpleDateFormat simpleDateFormat;
    private String currentTime;

    private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000 * 60 * 1;  // 1??? ?????? ?????? ??????
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 30; // 30??? ????????? ?????? ??????

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private DatabaseReference databaseReference;

    public MapFragment() {
    }

    @Override
    public void onAttach(Activity activity) { // Fragment ??? Activity??? attach ??? ??? ????????????.
        mContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ????????? ?????? ?????? ??????????????? ????????? ????????? ?????????.
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Layout ??? inflate ?????? ?????????.
        if (savedInstanceState != null) {
            mCurrentLocatiion = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        View layout = inflater.inflate(R.layout.activity_map_fragment, container, false);
        mapView = (MapView) layout.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        mapView.getMapAsync(this);

        getPostList();
        return layout;
    }

    private void getPostList() {
        gettime();

        ArrayList<MapItem> arrayList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Posting").orderByChild("time").startAt(String.valueOf(Long.valueOf(currentTime)-10000)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Location locationA = new Location("point A");
                locationA.setLatitude(location.getLatitude());
                locationA.setLongitude(location.getLongitude());
                Location locationB = new Location("point B");

                for(DataSnapshot ds: snapshot.getChildren()){
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
                        arrayList.add(new MapItem(ds.child("title").getValue().toString(), ds.child("context").getValue().toString(), ds.child("category").getValue().toString(), String.valueOf(Long.valueOf(currentTime)-time), String.valueOf(distance), ds.child("writer").getValue().toString(), ds.getKey(), lati, longi));
                    }
                }

                setCurrentLocation(arrayList);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Fragement????????? OnCreateView??? ?????????, Activity?????? onCreate()??? ???????????? ?????? ???????????? ???????????????.
        // Activity??? Fragment??? ?????? ?????? ????????? ?????????, View??? ???????????? ????????? ????????? ?????????.
        super.onActivityCreated(savedInstanceState);

        //??????????????? ?????? ????????? ??? ???????????? ??????
        MapsInitializer.initialize(mContext);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // ???????????? ?????????????????? ??????
                .setInterval(UPDATE_INTERVAL_MS) // ????????? Update ?????? ??????
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS); // ?????? ????????? ?????????????????? ??????

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        // FusedLocationProviderClient ?????? ??????
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setDefaultLocation(); // GPS??? ?????? ????????? ????????? ?????? ?????? ????????? ?????? ????????? ?????????.

        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocatiion = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setDefaultLocation() {
        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mDefaultLocation);
        markerOptions.title("???????????? ????????? ??? ??????");
        markerOptions.snippet("?????? ???????????? GPS ?????? ?????? ???????????????");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15);
        mMap.moveCamera(cameraUpdate);
    }

    String getCurrentAddress(LatLng latlng) {
        // ?????? ????????? ?????????????????? ?????? ???????????? ?????????.
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        // ??????????????? ???????????? ?????? ???????????? ?????????.
        try {
            addressList = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        } catch (IOException e) {
            Toast.makeText(mContext, "??????????????? ????????? ????????? ??? ????????????. ??????????????? ???????????? ????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "?????? ?????? ??????";
        }

        if (addressList.size() < 1) { // ?????? ???????????? ??????????????? ?????? ?????????
            return "?????? ????????? ?????? ??????";
        }

        // ????????? ?????? ???????????? ???????????? ??????
        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }

        return addressStringBuilder.toString();
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                LatLng currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "??????:" + String.valueOf(location.getLatitude())
                        + " ??????:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "Time :" + CurrentTime() + " onLocationResult : " + markerSnippet);

                //?????? ????????? ?????? ???????????? ??????
                mCurrentLocatiion = location;
            }
        }

    };

    private String CurrentTime() {
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        return time.format(today);
    }

    public void setCurrentLocation(ArrayList<MapItem> arrayList) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        for(int i = 0; i < arrayList.size(); i++){
            MapItem mapItem = arrayList.get(i);
            LatLng latLng = new LatLng(mapItem.getLatitude(), mapItem.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(mapItem.getTitle());
            markerOptions.snippet(mapItem.getContent());
            markerOptions.draggable(true);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(mapItem.getWriter()+"?????? ???????????? ?????????????????????????")
                            .setPositiveButton("GO",
                                    (dialog, which) -> {
                                        addToMyChatList(mapItem.getChatKey());
                                        Intent intent = new Intent(mContext, ChattingActivity.class);
                                        intent.putExtra("chat_key", mapItem.getChatKey());
                                        mContext.startActivity(intent);
                                    });
                    builder.show();
                }
            });

            currentMarker = mMap.addMarker(markerOptions);
        }


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);

        mMap.moveCamera(cameraUpdate);
    }

    private void addToMyChatList(String chat_key) {
        mAuth = FirebaseAuth.getInstance();
        String Uid = mAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getTime();
        databaseReference.child("users").child(Uid).child("chatList").orderByKey().equalTo(chat_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    databaseReference.child("users").child(Uid).child("chatList").child(chat_key).setValue(currentTime);
                    DatabaseReference chatRef = databaseReference.child("Posting").child(chat_key).child("chat").child("message_list").push();
                    chatRef.child("from").setValue("manager");
                    chatRef.child("message").setValue(SavedSharedPreference.getUserName(mContext)+" ?????? ?????????????????????.");
                    chatRef.child("realTime").setValue("0");
                    chatRef.child("showTime").setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTime() {
        // ?????? ?????? ?????????
        now = System.currentTimeMillis();
        mDate = new Date(now);
        simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmm");
        currentTime = simpleDateFormat.format(mDate);
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(mContext,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onStart() { // ???????????? Fragment??? ???????????? ?????????.
        super.onStart();
        mapView.onStart();
        Log.d(TAG, "onStart ");
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onStop : removeLocationUpdates");
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }


    @Override
    public void onResume() { // ???????????? Fragment??? ????????????, ????????? ??????????????? ???????????? ?????? ??????
        super.onResume();
        mapView.onResume();
        if (mLocationPermissionGranted) {
            Log.d(TAG, "onResume : requestLocationUpdates");
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                if (mMap!=null)
                    mMap.setMyLocationEnabled(true);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            mapView.onPause();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();
        }

        @Override
        public void onDestroyView() { // ?????????????????? ????????? View ??? ???????????? ??????
            super.onDestroyView();
            if (mFusedLocationProviderClient != null) {
                Log.d(TAG, "onDestroyView : removeLocationUpdates");
                mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
            }
        }

        @Override
        public void onDestroy() {
            // Destroy ??? ??????, ????????? OnDestroyView?????? View??? ????????????, OnDestroy()??? ????????????.
            super.onDestroy();
            mapView.onDestroy();
        }

    }



