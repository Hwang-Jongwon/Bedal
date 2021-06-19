package com.example.bedalground;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    private TextView tv_title;
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

    }
    @Override
    protected void onResume(){
        super.onResume();
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
        rv_chatting.setLayoutManager(linearLayoutManager);

        databaseReference.child("Posting").child(chat_key).child("chat").child("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("title", snapshot+".");
                tv_title = findViewById(R.id.tv_title);
                tv_title.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("Posting").child(chat_key).child("chat").child("message_list").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String from = "", message = "", showTime = "";
                from = snapshot.child("from").getValue().toString();
                message = snapshot.child("message").getValue().toString();
                showTime = snapshot.child("showTime").getValue().toString();

                if (from.equals(username)) {
                    items.add(new MessageItem(from, message, showTime, MessageCode.ViewType.MY_MESSAGE));
                }
                else if(from.equals("manager")){
                    items.add(new MessageItem(from,message,showTime, MessageCode.ViewType.PUBLIC_MESSAGE));
                }
                else {
                    items.add(new MessageItem(from, message, showTime, MessageCode.ViewType.OTHER_MESSAGE));
                }
                messageAdapter = new MessageAdapter(items);
                rv_chatting.setAdapter(messageAdapter);

                rv_chatting.scrollToPosition(items.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        username = SavedSharedPreference.getUserName(this);
    }

    public void SendMessage(View view) {
        if(!et_message.getText().toString().equals("")){
            getCurrentTime();

            ChatDTO chatDTO = new ChatDTO(username, et_message.getText().toString(), realTime, showTime);
            databaseReference.child("Posting").child(chat_key).child("chat").child("message_list").push().setValue(chatDTO);

            messageAdapter = new MessageAdapter(items);
            rv_chatting.setAdapter(messageAdapter);

            et_message.setText("");

            rv_chatting.scrollToPosition(items.size()-1);
        }

    }

    public void goToBack(View view) {
        finish();
    }

}