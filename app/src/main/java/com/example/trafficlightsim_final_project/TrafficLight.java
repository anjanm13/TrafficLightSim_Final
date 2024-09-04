package com.example.trafficlightsim_final_project;

public class TrafficLight extends Thread{

    public enum Color {
        RED,
        YELLOW,
        GREEN
    }

    Color currColor;
    long yellowTimeMillis;

    public TrafficLight(long yellowTimeMillis) {
        currColor = Color.RED;
        this.yellowTimeMillis = yellowTimeMillis;
    }

    public void changeColor() {
        if (currColor == Color.RED) {
            currColor = Color.GREEN;
        } else if (currColor == Color.GREEN) {
            currColor = Color.RED;
        }
    }

}
