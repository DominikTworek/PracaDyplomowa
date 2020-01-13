package com.example.pracadyplomowa.Models;

public class ModelWorkout {
    private String exerciseId, weight, repeats, series, uid, email, date;

    public ModelWorkout(){
    }

    public ModelWorkout(String exerciseId, String weight, String repeats, String series, String uid, String email, String date) {
        this.exerciseId = exerciseId;
        this.weight = weight;
        this.repeats = repeats;
        this.series = series;
        this.uid = uid;
        this.email = email;
        this.date = date;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRepeats() {
        return repeats;
    }

    public void setRepeats(String repeats) {
        this.repeats = repeats;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
