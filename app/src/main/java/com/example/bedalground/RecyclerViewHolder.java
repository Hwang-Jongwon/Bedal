package com.example.bedalground;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView re_title, re_sub, re_time, re_meter;
    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        re_title = (TextView)itemView.findViewById(R.id.re_title);
        re_sub = (TextView)itemView.findViewById(R.id.re_sub);
        re_time = (TextView)itemView.findViewById(R.id.re_time);
        re_meter = (TextView)itemView.findViewById(R.id.re_meter);
    }
}
