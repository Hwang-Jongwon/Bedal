package com.example.bedalground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlusWriting extends Activity {
    private TextView tv_writer, tv_location;
    private EditText et_title, et_context;
    private ImageButton btn_cancel, btn_okay;
    private Spinner spn_category;
    private String Uid, Name, MyLocation, category;

    private double latitude, longitude;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private long now;
    private Date mDate;
    private SimpleDateFormat simpleDateFormat;
    private String currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_plus_writing);

        tv_writer = findViewById(R.id.tv_writer);
        tv_location = findViewById(R.id.tv_location);
        et_title = findViewById(R.id.et_title);
        et_context = findViewById(R.id.et_context);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_okay = findViewById(R.id.btn_okay);
        spn_category = findViewById(R.id.spn_category);

        // 현재 사용자 가져오는 코드
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Uid = user.getUid();
        databaseReference.child("users").child(Uid).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){

                }else {
                    Name = task.getResult().getValue().toString();
                    tv_writer.setText(Name);
                }
            }
        });

        // 내 위치 전달 받기
        Intent intent = getIntent();
        MyLocation = intent.getExtras().getString("MyLocation");
        latitude = intent.getExtras().getDouble("latitude");
        longitude = intent.getExtras().getDouble("longitude");
        tv_location.setText(MyLocation);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 카테고리
        spn_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_title.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                else if(et_context.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                else if(category.equals("")){
                    Toast.makeText(getApplicationContext(), "카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show();
                }
                else{
                    DatabaseReference PostId = databaseReference.child("Posting").push();
                    PostId.child("title").setValue(et_title.getText().toString());
                    PostId.child("writer").setValue(Name);
                    PostId.child("location").setValue(MyLocation);
                    PostId.child("latitude").setValue(latitude);
                    PostId.child("longitude").setValue(longitude);
                    PostId.child("category").setValue(category);
                    PostId.child("context").setValue(et_context.getText().toString());

                    // 현재 시간 구하기
                    now = System.currentTimeMillis();
                    mDate = new Date(now);
                    simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmm");
                    currentTime = simpleDateFormat.format(mDate);

                    PostId.child("time").setValue(currentTime);

                    Toast.makeText(getApplicationContext(), "글이 등록되었습니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}