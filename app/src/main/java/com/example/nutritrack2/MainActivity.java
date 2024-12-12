package com.example.nutritrack2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nutritrack2.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    UserRepository userRepository;
    Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository();
        uiHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.obj instanceof User) {
                    User user = (User) msg.obj;
                    Log.d("DEV", "User object received: " + user);
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    intent.putExtra("user", new Gson().toJson(user));
                    startActivity(intent);
                } else {
                    Log.e("DEV", "Failed to receive User object");
                }
                return true;
            }
        });

        binding.btnLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etLoginEmail.getText().toString().trim();
                String password = binding.etLoginPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    userRepository.checkUserExists(Executors.newSingleThreadExecutor(), uiHandler, email, password, new UserRepository.OnUserCheckListener() {
                        @Override
                        public void onUserCheck(boolean exists) {
                            if (exists) {
                                Log.d("DEV", "User exists, fetching user data");
                                userRepository.getUserByEmail(Executors.newSingleThreadExecutor(), uiHandler, email);
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Please fill the blanks.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignUp.class);
                startActivity(i);
            }
        });
    }
}
