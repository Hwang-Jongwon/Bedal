package com.example.bedalground;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListHolder> {
    private ArrayList<ChatListItem> mItems;
    private FragmentManager fragmentManager;
    private Fragment fragment;

    Context mContext;
    public ChatListAdapter(ArrayList itemList, FragmentManager fragmentManager, Fragment fragment){
        mItems = itemList;
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
    }
    @NonNull
    @Override
    public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat, parent, false);
        mContext = parent.getContext();
        ChatListHolder holder = new ChatListHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListHolder holder, int position) {
        holder.re_chattitle.setText(mItems.get(position).getTitle());
        holder.re_lastchat.setText(mItems.get(position).getLast_message());
        holder.re_lasttime.setText(mItems.get(position).getLast_time());


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String Uid = user.getUid();

        holder.itemView.setOnClickListener(v -> {
            String chat_key = mItems.get(position).getChat_key();
            Intent intent = new Intent(mContext, ChattingActivity.class);
            intent.putExtra("chat_key", chat_key);
            mContext.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setMessage("채팅방을 나가시겠습니까?")
                    .setCancelable(true)
                    .setPositiveButton("네", ((dialog, which) -> {
                        databaseReference.child("users").child(Uid).child("chatList").child(mItems.get(position).getChat_key()).removeValue();
                        DatabaseReference chatRef = databaseReference.child("Posting").child(mItems.get(position).getChat_key()).child("chat").child("message_list").push();
                        chatRef.child("from").setValue("manager");
                        chatRef.child("message").setValue(SavedSharedPreference.getUserName(mContext)+" 님이 떠났습니다.");
                        chatRef.child("realTime").setValue("0");
                        chatRef.child("showTime").setValue("0");
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.detach(fragment).attach(fragment).commit();
                    }));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
