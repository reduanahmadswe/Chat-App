package com.ahmadreduan.amitumi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ahmadreduan.amitumi.Models.Users;
import com.ahmadreduan.amitumi.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


       // setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });






        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your account");

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(),binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressDialog.dismiss();


                                if(task.isSuccessful()){
                                    Users users = new Users(binding.etUserName.getText().toString(), binding.etEmail.getText().toString(),
                                            binding.etPassword.getText().toString());

                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(users);



                                    Toast.makeText(SignUpActivity.this,"User Created Successfully", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(SignUpActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        });


        binding.tvAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent) ;
            }
        });





    }
}