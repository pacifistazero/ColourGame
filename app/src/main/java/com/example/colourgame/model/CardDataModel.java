package com.example.colourgame.model;

import android.widget.Button;

public class CardDataModel {

    public int x;
    public int y;
    public Button button;

    public CardDataModel(Button button, int x, int y) {
        this.x = x;
        this.y = y;
        this.button=button;
    }
}