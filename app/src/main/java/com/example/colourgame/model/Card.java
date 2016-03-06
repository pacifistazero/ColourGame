package com.example.colourgame.model;

import android.widget.Button;

/**
 * Created by Ilham on 3/6/2016.
 */
public class Card{

    public int x;
    public int y;
    public Button button;

    public Card(Button button, int x,int y) {
        this.x = x;
        this.y = y;
        this.button=button;
    }
}