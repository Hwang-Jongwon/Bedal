package com.example.bedalground;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListHolder> {
    private ArrayList<ChatListItem> mItems;
    Context mContext;
    public ChatListAdapter(ArrayList itemList){
        mItems = itemList;
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
        holder.re_chattitle.setText(mItems.get(position).title);
        holder.re_lastchat.setText(mItems.get(position).last_message);
        holder.re_lasttime.setText(mItems.get(position).last_time);

        holder.itemView.setOnClickListener(v -> {
            String chat_key = mItems.get(position).chat_key;
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
