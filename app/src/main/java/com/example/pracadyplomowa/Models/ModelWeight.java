package com.example.pracadyplomowa.Models;

public class ModelWeight {
    private String email, uid, weight, date;

    public ModelWeight(){
    }

    public ModelWeight(String email, String uid, String weight, String date) {
        this.email = email;
        this.uid = uid;
        this.weight = weight;
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
