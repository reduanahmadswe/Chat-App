package com.ahmadreduan.amitumi.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadreduan.amitumi.Models.MessageModel;
import com.ahmadreduan.amitumi.R;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Context;

import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> messageModels;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent,false);
            return  new ReciverViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciver, parent,false);
            return  new SenderViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {

        if(messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else {
            return RECEIVER_VIEW_TYPE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = messageModels.get(position);

        if(holder.getClass()== SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());

        }else {
            ((ReciverViewHolder)holder).reciverMsg.setText(messageModel.getMessage());
            //((ReciverViewHolder)holder).recivertime.setText(messageModel.getTimestamp());
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReciverViewHolder extends RecyclerView.ViewHolder {

        TextView reciverMsg, recivertime;

        public ReciverViewHolder(@NonNull View itemView) {


            super(itemView);

            reciverMsg = itemView.findViewById(R.id.receiverText);
            recivertime = itemView.findViewById(R.id.receiverTime);

        }
    }


    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMsg, sendertime;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            sendertime = itemView.findViewById(R.id.senderTime);
        }
    }
}
