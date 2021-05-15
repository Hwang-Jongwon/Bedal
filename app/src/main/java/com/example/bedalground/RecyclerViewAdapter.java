package com.example.bedalground;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private ArrayList<PostItem> mItems;
    Context mContext;
    public RecyclerViewAdapter(ArrayList itemList){
        mItems = itemList;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post, parent, false);
        mContext = parent.getContext();
        RecyclerViewHolder holder = new RecyclerViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.re_title.setText(mItems.get(position).title);
        holder.re_sub.setText(mItems.get(position).sub);
        String minute = String.valueOf(mItems.get(position).time);
        holder.re_time.setText(minute+"분 전");
        int int_meter = (int) mItems.get(position).meter;
        holder.re_meter.setText(int_meter+"m");
        String writer = mItems.get(position).writer;
        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(writer+"님의 채팅방에 입장하시겠습니까?")
                    .setPositiveButton("GO",
                            (dialog, which) -> {
                                //채팅창으로 이동
                            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}