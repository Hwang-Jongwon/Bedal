package com.example.bedalground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
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
    private TextView btn_okay;
    private EditText et_title, et_context;
    private Spinner spn_category;
    private String Uid, Name, MyLocation, category;

    private double latitude, longitude;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private long now;
    private Date mDate;
    private SimpleDateFormat simpleDateFormat, realTimeDateFormat, showTimeDateFormat;
    private String currentTime, realTime, showTime;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_none, R.anim.anim_add_post_finish);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_plus_writing);

        et_title = findViewById(R.id.et_title);
        et_context = findViewById(R.id.et_context);
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
                }
            }
        });

        // 내 위치 전달 받기
        Intent intent = getIntent();
        MyLocation = intent.getExtras().getString("MyLocation");
        latitude = intent.getExtras().getDouble("latitude");
        longitude = intent.getExtras().getDouble("longitude");

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

        btn_okay.setOnClickListener(v -> {
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
                realTimeDateFormat = new SimpleDateFormat("yyyyMMddkkmmss");
                showTimeDateFormat = new SimpleDateFormat("a h:mm");

                currentTime = simpleDateFormat.format(mDate);
                realTime = realTimeDateFormat.format(mDate);
                showTime = showTimeDateFormat.format(mDate);

                PostId.child("time").setValue(currentTime);

                PostId.child("chat").child("title").setValue(et_title.getText().toString());
                DatabaseReference ChatId = PostId.child("chat").child("message_list").push();
                ChatId.child("from").setValue(Name);
                ChatId.child("realTime").setValue(realTime);
                ChatId.child("showTime").setValue(showTime);
                ChatId.child("message").setValue(et_context.getText().toString());

                databaseReference.child("users").child(Uid).child("chatList").child(PostId.getKey()).setValue(currentTime);

                Toast.makeText(getApplicationContext(), "글이 등록되었습니다!", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
                finish();
                overridePendingTransition(R.anim.anim_none, R.anim.anim_add_post_finish);
            }
        });
    }

}