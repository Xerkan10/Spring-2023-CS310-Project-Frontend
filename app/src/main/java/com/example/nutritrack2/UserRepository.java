package com.example.nutritrack2;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

import java.util.List;
import com.google.gson.reflect.TypeToken;


public class UserRepository {



    public void getUserByEmail(ExecutorService srv, Handler uiHandler, String email) {
        srv.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/user/getUserByEmail/" + URLEncoder.encode(email, "UTF-8"));
                System.err.println("URL created: " + url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(false); // Changed to false since it's a GET request
                System.err.println("Connection opened");

                int responseCode = conn.getResponseCode();
                System.err.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Convert the response to a JSON object
                    String jsonResponse = response.toString();
                    System.err.println("Response: " + jsonResponse);
                    Gson gson = new Gson();
                    User user = gson.fromJson(jsonResponse, User.class);

                    // Pass the user object to the UI thread
                    Message msg = Message.obtain();
                    msg.obj = user;
                    uiHandler.sendMessage(msg);
                } else {
                    System.err.println("GET request failed with response code: " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception occurred during GET request: " + e.getMessage());
            }
        });
    }
    public void checkUserExists(ExecutorService srv, Handler uiHandler, String email, String password, OnUserCheckListener listener) {
        srv.execute(() -> {
            try {
                System.err.println("Starting checkUserExists");

                URL url = new URL("http://10.0.2.2:8080/user/login");
                System.err.println("URL created: " + url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                System.err.println("Connection opened");

                JSONObject userJson = new JSONObject();
                userJson.put("email", email);
                userJson.put("password", password);
                System.err.println("JSON created: " + userJson.toString());

                BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(userJson.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
                System.err.println("JSON sent");

                int responseCode = conn.getResponseCode();
                System.err.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    reader.close();

                    System.err.println("Response: " + buffer.toString());

                    JSONObject responseJson = new JSONObject(buffer.toString());
                    String status = responseJson.optString("status");
                    System.err.println("Status: " + status);

                    if ("OK".equals(status)) {
                        uiHandler.post(() -> listener.onUserCheck(true));
                    } else {
                        uiHandler.post(() -> listener.onUserCheck(false));
                    }
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    reader.close();

                    System.err.println("Error Response: " + buffer.toString());

                    uiHandler.post(() -> listener.onUserCheck(false));
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception: " + e.getMessage());
                uiHandler.post(() -> listener.onUserCheck(false));
            }
        });
    }
    // Listener interface for user check result
    public interface OnUserCheckListener {
        void onUserCheck(boolean exists);
    }
    public void registerUser(ExecutorService srv, Handler uiHandler, User user) {
        srv.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://10.0.2.2:8080/user/register");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject userJson = new JSONObject();
                userJson.put("email", user.getEmail());
                userJson.put("password", user.getPassword());
                userJson.put("name", user.getName());
                userJson.put("lastName", user.getLastName());

                OutputStream os = conn.getOutputStream();
                os.write(userJson.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                System.err.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Handle success
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    System.out.println("Response: " + response.toString());
                } else {
                    // Handle error
                    InputStream es = conn.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(es));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    System.err.println("Error response: " + response.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    public void updateUser(ExecutorService srv, Handler uiHandler, int age, String gender, String height, String weight, String birthDate, String id){
        srv.execute(() -> {
            try {

                URL url = new URL("http://10.0.2.2:8080/user/updateattributes/" + URLEncoder.encode(id, "UTF-8"));
                System.err.println("URL created: " + url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject userJson = new JSONObject();
                userJson.put("age", age);
                userJson.put("gender", gender);
                userJson.put("height", height);
                userJson.put("weight", weight);
                userJson.put("dateOfBirth", birthDate);

                OutputStream os = conn.getOutputStream();
                os.write(userJson.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                System.err.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Handle success
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    System.out.println("Response: " + response.toString());


                    Message msg = Message.obtain();
                    msg.obj = userJson;

                    uiHandler.sendMessage(msg);

                } else {
                    // Handle error
                    InputStream es = conn.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(es));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    System.err.println("Error response: " + response.toString());

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        });
    }

    public void getAllFood(ExecutorService srv, Handler uiHandler) {
        srv.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/nutritrack/foods");
                System.err.println("URL created: " + url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(false); // Since it's a GET request
                System.err.println("Connection opened");

                int responseCode = conn.getResponseCode();
                System.err.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Convert the response to a JSON object
                    String jsonResponse = response.toString();
                    System.err.println("Response: " + jsonResponse);
                    Gson gson = new Gson();
                    List<Food> foodList = gson.fromJson(jsonResponse, new TypeToken<List<Food>>(){}.getType());

                    // Pass the food list to the UI thread
                    Message msg = Message.obtain();
                    msg.obj = foodList;
                    uiHandler.sendMessage(msg);
                } else {
                    System.err.println("GET request failed with response code: " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception occurred during GET request: " + e.getMessage());
            }
        });
    }

    public void updatePersonalizedList(ExecutorService srv, Handler uiHandler, List<Food> personalizedList, String id){
        srv.execute(() -> {
            try {

                URL url = new URL("http://10.0.2.2:8080/user/updatePersonalizedList/" + URLEncoder.encode(id, "UTF-8"));
                System.err.println("URL created: " + url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject userJson = new JSONObject();
                JSONArray foodArray = new JSONArray();

                for (Food food : personalizedList) {
                    JSONObject foodJson = new JSONObject();
                    foodJson.put("id", food.getId());
                    foodJson.put("name", food.getName());
                    foodJson.put("type", food.getType());
                    foodJson.put("foodWeight", food.getFoodWeight());

                    // Create the nested foodServings object
                    JSONObject foodServingsJson = new JSONObject();
                    foodServingsJson.put("calories", food.getFoodServings().getCalories());
                    foodServingsJson.put("carbohydrate", food.getFoodServings().getCarbohydrate());
                    foodServingsJson.put("protein", food.getFoodServings().getProtein());
                    foodServingsJson.put("fat", food.getFoodServings().getFat());
                    foodServingsJson.put("sugar", food.getFoodServings().getSugar());
                    foodServingsJson.put("trans_fat", food.getFoodServings().getTrans_fat());
                    foodServingsJson.put("saturated_fat", food.getFoodServings().getSaturated_fat());                    // Add other food properties as needed

                    foodJson.put("foodServings", foodServingsJson);
                    foodArray.put(foodJson);
                }

                //userJson.put("personalizedList", foodArray);

                System.err.println(foodArray.toString());

                OutputStream os = conn.getOutputStream();
                os.write(foodArray.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                System.err.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Handle success
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    System.out.println("Response: " + response.toString());

                    // Parse JSON response
                    JSONArray foodArray2 = new JSONArray(response.toString());
                    List<Food> foodList = new ArrayList<>();
                    for (int i = 0; i < foodArray.length(); i++) {
                        JSONObject foodJson = foodArray2.getJSONObject(i);
                        Food food = new Food(
                                foodJson.getString("name"), "asd", 12);
                        foodList.add(food);
                    }

                    // Send food list to the UI thread
                    Message msg = Message.obtain();
                    msg.obj = foodList;
                    uiHandler.sendMessage(msg);


                } else {
                    // Handle error
                    InputStream es = conn.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(es));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    System.err.println("Error response: " + response.toString());

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        });
    }

    public void registerUser2(ExecutorService srv, Handler uiHandler, User user, OnUserRegisteredListener listener) {
        srv.execute(() -> {
            HttpURLConnection conn = null;
            boolean registrationSuccess = false;
            try {
                URL url = new URL("http://10.0.2.2:8080/user/register");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject userJson = new JSONObject();
                userJson.put("email", user.getEmail());
                userJson.put("password", user.getPassword());
                userJson.put("name", user.getName());
                userJson.put("lastName", user.getLastName());

                OutputStream os = conn.getOutputStream();
                os.write(userJson.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                System.err.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Handle success
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    System.out.println("Response: " + response.toString());
                    registrationSuccess = true;
                } else {
                    // Handle error
                    InputStream es = conn.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(es));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    System.err.println("Error response: " + response.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                boolean finalRegistrationSuccess = registrationSuccess;
                uiHandler.post(() -> listener.onUserRegistered(finalRegistrationSuccess));
            }
        });
    }

    public interface OnUserRegisteredListener {
        void onUserRegistered(boolean success);
    }

}
