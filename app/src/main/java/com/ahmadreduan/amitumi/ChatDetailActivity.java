package com.ahmadreduan.amitumi;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmadreduan.amitumi.Adapters.ChatAdapter;
import com.ahmadreduan.amitumi.Models.MessageModel;
import com.ahmadreduan.amitumi.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final String senderID = auth.getUid();
        String recieveID = getIntent().getStringExtra("userID");
        String userName = getIntent().getStringExtra("userName");
        String progilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(progilePic).placeholder(R.drawable.user).into(binding.profileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this);
        binding.chatRecyclarView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclarView.setLayoutManager(layoutManager);



        final String senderRoom = senderID + recieveID;
        final String reciverRoom = recieveID+senderID;


//        database.getReference().child("chats")
//                        .child(senderRoom)
//                                .addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
//                                            MessageModel model = snapshot1.getValue(MessageModel.class);
//                                          // model.setMessageId(snapshot1.getKey());
//                                            messageModels.add(model);
//                                          // chatAdapter.notifyDataSetChanged();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });

        /////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
       // upore je comment kora ache ai khane amra bug dekte paici jkn porobortite kaj korbo
        //thakon oi bug fixt korbo 4:04:00
        ////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////



        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String message =  binding.etMessage.getText().toString();
               final MessageModel model = new MessageModel(senderID , message);
               model.setTimestamp(new Date().getTime());

               binding.etMessage.setText("");

               database.getReference().child("chats")
                       .child(senderRoom)
                       .push()
                       .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               database.getReference().child("chats")
                                       .child(reciverRoom)
                                       .push()
                                       .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void unused) {

                                           }
                                       });
                           }
                       });



            }
        });



    }
}