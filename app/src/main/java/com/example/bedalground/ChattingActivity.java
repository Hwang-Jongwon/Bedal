package com.example.bedalground;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChattingActivity extends AppCompatActivity {

    private String chat_key, Uid, username;
    private EditText et_message;
    private RecyclerView rv_chatting;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    ArrayList<MessageItem> items = new ArrayList<>();
    MessageAdapter messageAdapter = null;

    private long now;
    private Date mDate;
    private SimpleDateFormat realTimeDateFormat, showTimeDateFormat;
    private String realTime, showTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        Intent intent = getIntent();
        chat_key = intent.getStringExtra("chat_key");
        et_message = findViewById(R.id.et_message);

        getCurrentUser();
        makeList();


    }

    private void makeList() {
        items.clear();
        rv_chatting = findViewById(R.id.rv_chatting);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        databaseReference.child("Posting").child(chat_key).child("chat").child("message_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    Log.e("##", ds.getKey());
                    String from = ds.child("from").getValue().toString();
                    String message = ds.child("message").getValue().toString();
                    String showTime = ds.child("showTime").getValue().toString();
                    items.add(new MessageItem(from, message, showTime));
                    rv_chatting.setLayoutManager(linearLayoutManager);
                    rv_chatting.setItemAnimator(new DefaultItemAnimator());
                    if(from.equals(username)){
                        Log.e("0", from+"/"+username);
                        messageAdapter = new MessageAdapter(items, 0);
                    }else{
                        Log.e("1", from+"/"+username);
                        messageAdapter = new MessageAdapter(items, 1);
                    }
                    rv_chatting.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getCurrentTime() {
        // 현재 시간 구하기
        now = System.currentTimeMillis();
        mDate = new Date(now);
        realTimeDateFormat = new SimpleDateFormat("yyyyMMddkkmmss");
        showTimeDateFormat = new SimpleDateFormat("a h:mm");

        realTime = realTimeDateFormat.format(mDate);
        showTime = showTimeDateFormat.format(mDate);
    }

    private void getCurrentUser() {
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
    }

    public void SendMessage(View view) {
        if(!et_message.getText().toString().equals("")){
            getCurrentTime();

            DatabaseReference ChatRef = databaseReference.child("Posting").child(chat_key).child("chat").child("message_list").push();
            ChatRef.child("from").setValue(username);
            ChatRef.child("message").setValue(et_message.getText().toString());
            ChatRef.child("realTime").setValue(realTime);
            ChatRef.child("showTime").setValue(showTime);

            messageAdapter.notifyDataSetChanged();

            et_message.setText("");
            makeList();
        }

    }
}