package com.ahmadreduan.amitumi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        View mainLayout = findViewById(R.id.main);


        mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.purple));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final String senderID = auth.getUid();
        String recieveID = getIntent().getStringExtra("userID");
        String userName = getIntent().getStringExtra("userName");


        binding.userName.setText(userName);


//        FirebaseDatabase.getInstance().getReference("Users")
//                .child(recieveID)
//                .child("profileImage")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            String imageUrl = snapshot.getValue(String.class);
//                            Picasso.get().load(imageUrl).placeholder(R.drawable.user).into(binding.profileImage);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {}
//                });

        FirebaseDatabase.getInstance().getReference("Users")
                .child(recieveID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            String cloudinaryImageUrl = snapshot.child("profileImage").getValue(String.class);
                            String googleImageUrl = snapshot.child("profilepic").getValue(String.class);


                            if (cloudinaryImageUrl != null && !cloudinaryImageUrl.isEmpty()) {

                                Picasso.get().load(cloudinaryImageUrl)
                                        .placeholder(R.drawable.user)
                                        .into(binding.profileImage);
                            } else if (googleImageUrl != null && !googleImageUrl.isEmpty()) {

                                Picasso.get().load(googleImageUrl)
                                        .placeholder(R.drawable.user)
                                        .into(binding.profileImage);
                            } else {

                                binding.profileImage.setImageResource(R.drawable.user);
                            }
                        } else {

                            binding.profileImage.setImageResource(R.drawable.user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        Log.e("Firebase", "Error loading profile image", error.toException());
                    }
                });




        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this,recieveID);
        binding.chatRecyclarView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclarView.setLayoutManager(layoutManager);


        final String senderRoom = senderID + recieveID;
        final String reciverRoom = recieveID + senderID;



        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessageModel model = snapshot1.getValue(MessageModel.class);


                            model.setMessageId(snapshot1.getKey());

                            messageModels.add(model);

                        }

                        chatAdapter.notifyDataSetChanged();

                        binding.chatRecyclarView.scrollToPosition(messageModels.size() - 1);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = binding.etMessage.getText().toString();

                if (message.isEmpty()) {
                    Toast.makeText(ChatDetailActivity.this, "Write a message before sending!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final MessageModel model = new MessageModel(senderID, message);
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