package com.pushparaj.googlemaps;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class chatAdapter extends RecyclerView.Adapter<chatAdapter.RecycleHolders> {
    chat_row[] ins;

    public chatAdapter(chat_row[] ins) {
        this.ins = ins;
    }

    @Override
    public int getItemCount() {
        return ins.length;
    }

    @Override
    public RecycleHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row_layout,parent,false);
        return new RecycleHolders(view);
    }

    @Override
    public void onBindViewHolder(RecycleHolders holder, int position) {
            if(ins[position].getLor() == 0){
                holder.nameleft.setText(ins[position].getName());
                holder.messleft.setText(ins[position].getMess());
            }else if(ins[position].getLor() == 1){
                holder.nameright.setText(ins[position].getName());
                holder.messright.setText(ins[position].getMess());
            }

    }

    public static  class RecycleHolders extends RecyclerView.ViewHolder{

        TextView nameright,messright,nameleft,messleft;

        public RecycleHolders(View itemView) {
            super(itemView);
            nameright = (TextView)itemView.findViewById(R.id.nameright);
            messright = (TextView)itemView.findViewById(R.id.messright);
            nameleft = (TextView)itemView.findViewById(R.id.nameleft);
            messleft = (TextView)itemView.findViewById(R.id.messleft);
        }
    }

}
