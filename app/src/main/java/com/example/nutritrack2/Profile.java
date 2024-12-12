package com.example.nutritrack2;

import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import android.content.SharedPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.nutritrack2.databinding.ActivityProfileBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends AppCompatActivity {

    ExecutorService executorService;

    UserRepository userRepository;


    ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        executorService = Executors.newSingleThreadExecutor();
        userRepository = new UserRepository();
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                JSONObject userAttJson = (JSONObject) msg.obj;

                Sex genderUser;
                try {
                    String gender = userAttJson.getString("gender");
                    if (gender.equals("MALE")){
                        genderUser = Sex.MALE;
                    }
                    else{
                        genderUser = Sex.FEMALE;
                    }
                    double height = userAttJson.getDouble("height");
                    System.err.println("User height: " + height);
                    double weight = userAttJson.getDouble("weight");
                    String dateOfBirth = userAttJson.getString("dateOfBirth");
                    UserAttributes userAtt = new UserAttributes(genderUser, height, weight, dateOfBirth);
                    String userJson = getIntent().getStringExtra("user");
                    User user = User.fromJson(userJson);

                    user.setUserAttribute(userAtt);
                    System.err.println("User height2: " + user.getUserAttribute().getHeight());
                    //Handle the JSON response and update the UI
                    Intent intent = new Intent(Profile.this, HomePage.class);
                    intent.putExtra("user_profile", new Gson().toJson(user));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }

                //Toast.makeText(Profile.this, "Response: " + userAtt.toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });



        String userJson = getIntent().getStringExtra("user");
        User user = User.fromJson(userJson);


        // Set the user details
        binding.tvUsername.setText(user.getNameAndLastName());
        binding.tvEmail.setText(user.getEmail());
        binding.etHeight.setHint(String.valueOf(user.getUserAttribute().getHeight()));
        binding.etWeight.setHint(String.valueOf(user.getUserAttribute().getWeight()));
        binding.etGoalWeight.setHint(String.valueOf(user.getGoalWeight()));
        binding.dob.setHint(user.getUserAttribute().getDateOfBirth());

        if(user.getUserAttribute().getGender() == Sex.MALE){
            binding.male.setChecked(true);
        }
        else {
            binding.female.setChecked(true);
        }





        // Set the date
        binding.saveButton.setOnClickListener(v -> {
            String height = binding.etHeight.getText().toString().trim();
            String weight = binding.etWeight.getText().toString().trim();
            String date_of_birth = binding.dob.getText().toString().trim();
            String genderForJson;
            if (binding.male.isChecked()){
                genderForJson = "MALE";
            }else{
                genderForJson = "FEMALE";
            }


            if (!height.isEmpty() && !weight.isEmpty() && !date_of_birth.isEmpty()) {
                try {

                    UserAttributes myAtt = new UserAttributes(Sex.MALE, 2.3, 3.4, date_of_birth);


                    //UserAttributes userAtt = new UserAttributes(userForGender.getGender(), heightValue, weightValue, date_of_birth);
                    if(userRepository != null && executorService != null){
                        userRepository.updateUser(executorService, handler, myAtt.getAge(), genderForJson, height, weight, date_of_birth, user.getId());

                    } else {
                        // Handle the null case
                        Toast.makeText(this, "Initialization error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } catch (DateTimeParseException e) {
                    // Handle the error gracefully, e.g., show an error message to the user
                    e.printStackTrace();
                    // Show a Toast or AlertDialog to inform the user
                    Toast.makeText(this, "Please enter valid numbers for height and weight and a valid date of birth.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(Profile.this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
            }

        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnLogout.setOnClickListener(v -> {
            Intent i3 = new Intent(Profile.this, MainActivity.class);
            i3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i3);
            finish();
        });


    }


}