package com.example.bedalground;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MessageItem> mItems;
    Context mContext;
    public MessageAdapter(ArrayList<MessageItem> itemList){
        mItems = itemList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        mContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (viewType == MessageCode.ViewType.MY_MESSAGE){
            view = inflater.inflate(R.layout.my_msgbox, parent, false);
            return new MyMessageHolder(view);
        }
        else{
            view = inflater.inflate(R.layout.other_msgbox, parent, false);
            return new OtherMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyMessageHolder){
            ((MyMessageHolder) holder).my_name.setText(mItems.get(position).getName());
            ((MyMessageHolder) holder).my_time.setText(mItems.get(position).getTime());
            ((MyMessageHolder) holder).my_msg.setText(mItems.get(position).getMessage());
        }
        else if(holder instanceof OtherMessageHolder) {
            ((OtherMessageHolder)holder).other_name.setText(mItems.get(position).getName());
            ((OtherMessageHolder)holder).other_time.setText(mItems.get(position).getTime());
            ((OtherMessageHolder)holder).other_msg.setText(mItems.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getViewType();
    }

    public class MyMessageHolder extends RecyclerView.ViewHolder{
        public TextView my_name, my_time, my_msg;
        public MyMessageHolder(@NonNull View itemView) {
            super(itemView);
            my_name = itemView.findViewById(R.id.my_name);
            my_time = itemView.findViewById(R.id.my_time);
            my_msg = itemView.findViewById(R.id.my_msg);
        }
    }
    public class OtherMessageHolder extends RecyclerView.ViewHolder{
        public TextView other_name, other_time, other_msg;
        public OtherMessageHolder(@NonNull View itemView) {
            super(itemView);
            other_name = itemView.findViewById(R.id.other_name);
            other_time = itemView.findViewById(R.id.other_time);
            other_msg = itemView.findViewById(R.id.other_msg);
        }
    }
}
