package com.pushparaj.googlemaps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class REcyclerAdapter extends RecyclerView.Adapter<REcyclerAdapter.RecycleHolders> {
    latlngrows[] ins;

    public REcyclerAdapter(latlngrows[] ins) {
        this.ins = ins;
    }

    @Override
    public int getItemCount() {
        return ins.length;
    }

    @Override
    public RecycleHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        return new RecycleHolders(view);
    }

    @Override
    public void onBindViewHolder(RecycleHolders holder, int position) {
        holder.name.setText(ins[position].getName());
        if(!ins[position].getOffer().equals(""))
        holder.chat.setText(ins[position].getOffer());

    }

    public static  class RecycleHolders extends RecyclerView.ViewHolder{
        TextView name,chat;
        RelativeLayout re;

        public RecycleHolders(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.txtnames);
            chat = (TextView)itemView.findViewById(R.id.txtchats);
            re = (RelativeLayout) itemView.findViewById(R.id.recycle);
        }
    }

}
