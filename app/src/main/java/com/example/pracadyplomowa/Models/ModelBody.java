package com.example.pracadyplomowa.Models;

public class ModelBody {


    private String date;
    private String email;
    private String id;
    private String key;
    private String value;
    private String uid;

    public ModelBody(){
    }

    public ModelBody(String date, String email, String id, String key, String uid, String value) {
        this.date = date;
        this.email = email;
        this.id = id;
        this.key = key;
        this.uid = uid;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
