package com.ahmadreduan.amitumi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ahmadreduan.amitumi.databinding.ActivitySettingsBinding;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private static final int IMAGE_REQUEST_CODE = 33;
    private Uri selectedImageUri;
    private final String cloudName = "di21cbkyf";
    private final String uploadPreset = "chat_app_reduan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Cloudinary
        initCloudinary();

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        binding.backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
        });


        binding.plus.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        });

        binding.btnSave.setOnClickListener(v -> {
            String userName = binding.etUserName.getText().toString();
            String userStatus = binding.etStatus.getText().toString();

            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userStatus)) {
                Log.e("Firebase", "User name or status is empty.");
                return;
            }



            if (selectedImageUri != null) {
                uploadImageToCloudinary(userName, userStatus);
            } else {
                saveProfileDataToDatabase(null, userName, userStatus);
            }
        });


    }



    /**
     * Initialize Cloudinary SDK
     */
    private void initCloudinary() {
        try {

            MediaManager.get();
            Log.d("Cloudinary", "Cloudinary is already initialized.");
        } catch (IllegalStateException e) {

            Map config = new HashMap();
            config.put("cloud_name", "di21cbkyf");
            MediaManager.init(this, config);
            Log.d("Cloudinary", "Cloudinary initialized successfully.");
        } catch (Exception e) {
            Log.e("Cloudinary", "Error initializing Cloudinary", e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.profileImage.setImageURI(selectedImageUri);

        }
    }

    /**
     * Upload selected image to Cloudinary
     */
    private void uploadImageToCloudinary(String userName, String userStatus) {
        if (selectedImageUri == null) {
            Log.e("Cloudinary", "No image selected!");
            return;
        }

        try {
            MediaManager.get();
        } catch (IllegalStateException e) {
            Log.e("Cloudinary", "Cloudinary is not initialized!");
            return;
        }

        Log.d("Cloudinary", "Uploading image: " + selectedImageUri.toString());

        MediaManager.get().upload(selectedImageUri)
                .unsigned(uploadPreset)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Upload started...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d("Cloudinary", "Uploading... " + bytes + "/" + totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String uploadedImageUrl = (String) resultData.get("secure_url");
                        Log.d("Cloudinary", "Upload successful! URL: " + uploadedImageUrl);

                        // After the image upload is successful, save the profile data
                        saveProfileDataToDatabase(uploadedImageUrl, userName, userStatus);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary", "Upload failed: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.w("Cloudinary", "Upload rescheduled.");
                    }
                }).dispatch();
    }


    /**
     * Save uploaded profile image URL to Firebase Database
     */
    private void saveProfileDataToDatabase(String imageUrl, String userName, String userStatus) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String userId = auth.getCurrentUser().getUid();

        // Save the profile data
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("profileImage", imageUrl);
        userProfile.put("userName", userName);
        userProfile.put("status", userStatus);

        DatabaseReference userRef = database.getReference("Users").child(userId);

        userRef.setValue(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Profile data saved successfully!");
                    loadProfileImage(); // Optionally load the profile image to verify
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to save profile data", e);
                });
    }


    /**
     * Load profile image from Firebase and display it
     */
    private void loadProfileImage() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("Users").child(userId).child("profileImage");

        userRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String imageUrl = dataSnapshot.getValue(String.class);
                Log.d("Firebase", "Profile image loaded: " + imageUrl);
                Glide.with(this).load(imageUrl).into(binding.profileImage);
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Failed to load profile image", e);
        });
    }
}









//package com.ahmadreduan.amitumi;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.ahmadreduan.amitumi.databinding.ActivitySettingsBinding;
//import com.ahmadreduan.amitumi.databinding.ActivitySignInBinding;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//public class SettingsActivity extends AppCompatActivity {
//
//    ActivitySettingsBinding binding;
//    FirebaseStorage storage;
//    FirebaseAuth auth;
//    FirebaseDatabase database;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        EdgeToEdge.enable(this);
//
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//
//        storage = FirebaseStorage.getInstance();
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//
//
//
//        binding.backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
//                startActivity(intent);
//
//            }
//        });
//
//
//        binding.plus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, 33);
//
//
//            }
//        });
//
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data.getData() != null) {
//
//            Uri sFile = data.getData();
//            binding.profileImage.setImageURI(sFile);
//
//            final StorageReference reference =
//        }
//
//    }
//}