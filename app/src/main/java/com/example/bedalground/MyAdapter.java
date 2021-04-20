package com.example.bedalground;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private String[] title, context, ctgr_list, time, meter;
    private double[] x, y;
    private String UID, currentTime, ctgr;
    private double lati, longi;

    public MyAdapter(String[] title_list, String[] context_list, String[] ctgr_list, String[] time_list, double[] x_list, double[] y_list, String Uid, double lati, double longi, String currentTime, String[] meter_list, String ctgr){
        this.title = title_list;
        this.context = context_list;
        this.ctgr_list = ctgr_list;
        this.time = time_list;
        this.x = x_list;
        this.y = y_list;
        this.UID = Uid;
        this.lati = lati;
        this.longi = longi;
        this.currentTime = currentTime;
        this.meter = meter_list;
        this.ctgr = ctgr;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(holderView);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        double meter = Double.parseDouble(this.meter[position]);
        Log.e("meter", meter+"m");
        if(meter<=1000.0){
            holder.re_title.setText(this.title[position]);
            holder.re_context.setText(this.context[position]);
            holder.re_time.setText(Long.parseLong(this.currentTime)-Long.parseLong(this.time[position])+"분 전");
            holder.re_meter.setText((int)meter+"m");
        }
    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView re_title, re_context, re_time, re_meter;
        public LinearLayout ll_post;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.re_title = itemView.findViewById(R.id.re_title);
            this.re_context = itemView.findViewById(R.id.re_context);
            this.re_time = itemView.findViewById(R.id.re_time);
            this.re_meter = itemView.findViewById(R.id.re_meter);
            this.ll_post = itemView.findViewById(R.id.ll_post);
        }
    }
}
