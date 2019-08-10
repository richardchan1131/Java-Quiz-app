package com.rishav.quizearn;

public class Users {

    private String name,email,pass,code;
    private String profile="https://firebasestorage.googleapis.com/v0/b/quiz-earn-b5b83.appspot.com/o/profilepic.png?alt=media&token=99194940-0f72-446f-a593-059b01a6b255";
    private long coins =25 ;


    public Users(){

    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

}
