package com.example.bedalground;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageHolder extends RecyclerView.ViewHolder{
    public TextView tv_name, tv_time, tv_msg;
    public MessageHolder(@NonNull View itemView) {
        super(itemView);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_time = itemView.findViewById(R.id.tv_time);
        tv_msg = itemView.findViewById(R.id.tv_msg);
    }
}
