package com.example.nutritrack2;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;



public class User {


    private String id;
    private Sex gender;
    private String email;
    private String password;
    private Token token;
    private String name;
    private String lastName;

    private List<Food> personalized_List;
    private UserAttributes userAttribute;
    private double goalWeight;


    public User() {
        userAttribute = new UserAttributes();
        personalized_List = new ArrayList<Food>();
    }


    public User(String email, String password, String name, String lastName) {
        super();
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
    }


    public User(String email, String password, Token token, String name, String lastName) {
        super();
        this.email = email;
        this.password = password;
        this.token = token;
        this.name = name;
        this.lastName = lastName;
    }



    public User(List<Food> personalized_List, UserAttributes userAttribute, double goalWeight) {
        super();
        this.personalized_List = personalized_List;
        this.userAttribute = userAttribute;
        this.goalWeight = goalWeight;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public Sex getGender(){
        return gender;
    }

    public void setGender(Sex gender){
        this.gender = gender;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public Token getToken() {
        return token;
    }


    public void setToken(Token token) {
        this.token = token;
    }


    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public List<Food> getPersonalized_List() {
        return personalized_List;
    }



    public void setPersonalized_List(List<Food> personalized_List) {
        this.personalized_List = personalized_List;
    }



    public UserAttributes getUserAttribute() {
        return userAttribute;
    }



    public void setUserAttribute(UserAttributes userAttribute) {
        this.userAttribute = userAttribute;
    }



    public double getGoalWeight() {
        return goalWeight;
    }



    public void setGoalWeight(double goalWeight) {
        this.goalWeight = goalWeight;
    }

    public static User fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    public String getNameAndLastName() {
        return name + " " + lastName;
    }

    public void addFoodToPersonalizedList(Food food) {
        if (food != null) {
            personalized_List.add(food);
        }
    }


}





