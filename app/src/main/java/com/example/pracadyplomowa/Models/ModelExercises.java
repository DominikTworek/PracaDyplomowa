package com.example.pracadyplomowa.Models;

import android.content.Intent;

public class ModelExercises {
    String id;
    String name;
    String type;
    String search;
    String image;

    public ModelExercises(){
    }

    public ModelExercises(String id, String name, String type, String search, String image) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.search = search;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
