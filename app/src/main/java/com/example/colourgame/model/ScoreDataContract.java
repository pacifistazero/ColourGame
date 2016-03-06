package com.example.colourgame.model;

public class ScoreDataContract {

    private int id;
    private String name;
    private double score;

    // Empty constructor
    public ScoreDataContract(){

    }

    // constructor
    public ScoreDataContract(int id, String name, double score){
        this.setId(id);
        this.setName(name);
        this.setScore(score);
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
