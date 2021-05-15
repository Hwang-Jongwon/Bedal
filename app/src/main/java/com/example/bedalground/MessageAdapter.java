package com.example.bedalground;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {
    private ArrayList<MessageItem> mItems;
    private int code=1;
    Context mContext;
    public MessageAdapter(ArrayList itemList, int code){
        mItems = itemList;
        this.code = code;
    }
    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if(code == 0){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_msgbox, parent, false);
            Log.e("view", "0");
        }
        else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_msgbox, parent, false);
            Log.e("view", "1");

        }
        mContext = parent.getContext();
        MessageHolder holder = new MessageHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.tv_name.setText(mItems.get(position).name);
        holder.tv_time.setText(mItems.get(position).time);
        holder.tv_msg.setText(mItems.get(position).message);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


}
