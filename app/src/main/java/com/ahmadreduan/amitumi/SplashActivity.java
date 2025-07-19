package com.ahmadreduan.amitumi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.ahmadreduan.amitumi.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (getIntent().getExtras() != null) {
            String userId = getIntent().getExtras().getString("userId");

            if (userId != null) {
                FirebaseUtil.allUserCollectionReference().document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Log.d("FirebaseData", "User data: " + documentSnapshot.getData());
                            } else {
                                Log.d("FirebaseData", "No such document!");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirebaseData", "Error fetching document", e);
                        });
            }
        }


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {

            binding.loginBtn.setOnClickListener(v -> {
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                System.out.println("login click");
                startActivity(intent);
            });

            binding.signupBtn.setOnClickListener(v -> {
                startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
            });
        }

        //DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        // Update token in database in background thread
                        new Thread(() -> FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(userId)
                                .child("fcmToken")
                                .setValue(token)).start();
                        Log.d("FCM Token", token);
                    });

        }
    }
}