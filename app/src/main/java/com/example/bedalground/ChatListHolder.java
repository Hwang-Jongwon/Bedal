package com.example.bedalground;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListHolder extends RecyclerView.ViewHolder{
    public TextView re_chattitle, re_lastchat, re_lasttime;
    public ChatListHolder(@NonNull View itemView) {
        super(itemView);
        re_chattitle = (TextView)itemView.findViewById(R.id.re_chattitle);
        re_lastchat = (TextView)itemView.findViewById(R.id.re_lastchat);
        re_lasttime = (TextView)itemView.findViewById(R.id.re_lasttime);
    }
}
