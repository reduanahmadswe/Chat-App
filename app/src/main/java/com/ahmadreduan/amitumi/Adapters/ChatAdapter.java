package com.ahmadreduan.amitumi.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadreduan.amitumi.Models.MessageModel;
import com.ahmadreduan.amitumi.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciver, parent, false);
            return new ReciverViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }

    }

//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//        MessageModel messageModel = messageModels.get(position);
//
//        if(holder.getClass()== SenderViewHolder.class){
//            ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());
//
//        }else {
//            ((ReciverViewHolder)holder).reciverMsg.setText(messageModel.getMessage());
//            //((ReciverViewHolder)holder).recivertime.setText(messageModel.getTimestamp());
//        }
//
//    }

    //    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        MessageModel messageModel = messageModels.get(position);
//
//        if (messageModel == null) {
//            return;  // Prevent NullPointerException
//        }
//
//        Log.d("ChatAdapter", "Binding message: " + messageModel.getMessage());
//
//        if (holder instanceof SenderViewHolder) {
//            SenderViewHolder senderHolder = (SenderViewHolder) holder;
//
//            if (senderHolder.senderMsg == null) {
//                Log.e("ChatAdapter", "senderMsg is NULL");
//            } else {
//                senderHolder.senderMsg.setText(messageModel.getMessage() != null ? messageModel.getMessage() : "");
//            }
//
//            if (senderHolder.sendertime == null) {
//                Log.e("ChatAdapter", "sendertime is NULL");
//            } else {
//                senderHolder.sendertime.setText(messageModel.getTimestamp() != null ? messageModel.getTimestamp().toString() : "");
//            }
//
//        } else if (holder instanceof ReciverViewHolder) {
//            ReciverViewHolder receiverHolder = (ReciverViewHolder) holder;
//
//            if (receiverHolder.reciverMsg == null) {
//                Log.e("ChatAdapter", "reciverMsg is NULL");
//            } else {
//                receiverHolder.reciverMsg.setText(messageModel.getMessage() != null ? messageModel.getMessage() : "");
//            }
//
//            if (receiverHolder.recivertime == null) {
//                Log.e("ChatAdapter", "recivertime is NULL");
//            } else {
//                receiverHolder.recivertime.setText(messageModel.getTimestamp() != null ? messageModel.getTimestamp().toString() : "");
//            }
//        }
//    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                String senderRoom = FirebaseAuth.getInstance().getUid()+recId;


                                database.getReference().child("chats").child(senderRoom)
                                        .child(messageModel.getMessageId())
                                        .setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

                return false;
            }
        });






        if (messageModel == null) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(new Date(messageModel.getTimestamp()));

        if (holder instanceof SenderViewHolder) {
            SenderViewHolder senderHolder = (SenderViewHolder) holder;

            if (senderHolder.senderMsg != null) {
                senderHolder.senderMsg.setText(messageModel.getMessage() != null ? messageModel.getMessage() : "");
            }

            if (senderHolder.sendertime != null) {
                senderHolder.sendertime.setText(formattedTime);
            }

        } else if (holder instanceof ReciverViewHolder) {
            ReciverViewHolder receiverHolder = (ReciverViewHolder) holder;

            if (receiverHolder.reciverMsg != null) {
                receiverHolder.reciverMsg.setText(messageModel.getMessage() != null ? messageModel.getMessage() : "");
            }

            if (receiverHolder.recivertime != null) {
                receiverHolder.recivertime.setText(formattedTime);
            }
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


    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMsg, sendertime;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            sendertime = itemView.findViewById(R.id.senderTime);
        }
    }
}
