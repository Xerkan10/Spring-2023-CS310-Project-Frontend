package com.example.nutritrack2;

public class Token {

    private String token;
    private String timeOut;


    public Token() {
        // TODO Auto-generated constructor stub
    }


    public Token(String token, String timeOut) {
        super();
        this.token = token;
        this.timeOut = timeOut;
    }


    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public String getTimeOut() {
        return timeOut;
    }


    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }




}
