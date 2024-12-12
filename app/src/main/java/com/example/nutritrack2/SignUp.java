package com.example.nutritrack2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.os.Message;

import com.example.nutritrack2.databinding.ActivitySignUpBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity {


    ActivitySignUpBinding signUpBinding;
    ExecutorService executorService;
    Handler handler;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler();
        try {
            userRepository = new UserRepository();
            Log.d("DEV", "UserRepository initialized successfully.");
        } catch (Exception e) {
            Log.e("DEV", "Failed to initialize UserRepository.", e);
        }
        Handler uiHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.obj instanceof User) {
                    User user = (User) msg.obj;
                    Log.d("DEV", "User object received: " + user);
                    Intent i = new Intent(SignUp.this, HomePage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("user", new Gson().toJson(user));
                    startActivity(i);
                } else {
                    Log.e("DEV", "Failed to receive User object");
                }
                return true;
            }
        });
        //SignUp Button
        signUpBinding.btnSignUpSubmit.setOnClickListener(v -> {
            String email = signUpBinding.etSignUpEmail.getText().toString().trim();
            String password = signUpBinding.etSignUpPassword.getText().toString().trim();
            String name = signUpBinding.etSignUpName.getText().toString().trim();
            String surname = signUpBinding.etSignUpSurname.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                User user = new User(email, password, name, surname);
                userRepository.registerUser2(Executors.newSingleThreadExecutor(), handler, user, success -> {
                    if (success) {
                        userRepository.checkUserExists(Executors.newSingleThreadExecutor(), uiHandler, email, password, new UserRepository.OnUserCheckListener() {
                            @Override
                            public void onUserCheck(boolean exists) {
                                if (exists) {
                                    Log.d("DEV", "User exists, fetching user data");
                                    userRepository.getUserByEmail(Executors.newSingleThreadExecutor(), uiHandler, email);
                                } else {
                                    Toast.makeText(SignUp.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SignUp.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(SignUp.this, "Please fill the blanks.", Toast.LENGTH_SHORT).show();
            }
        });

        signUpBinding.btnLogin.setOnClickListener(v -> {
            Intent i = new Intent(SignUp.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}