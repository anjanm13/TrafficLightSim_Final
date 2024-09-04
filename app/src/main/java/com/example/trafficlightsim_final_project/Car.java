package com.example.trafficlightsim_final_project;

import android.util.Log;

public class Car extends Thread{

    public static final String TAG = Car.class.getSimpleName();

    public enum CarColor {
        RED("#FF0000"),
        GREEN("#00FF00"),
        BLUE("#0000FF"),
        YELLOW("#FFFF00");

        public final String colorCode;
        CarColor(String colorCode) {
            this.colorCode = colorCode;
        }
    }

    public final CarColor color;
    public final StartRoad startRoad;


    public Car(CarColor color, StartRoad startRoad) {
        this.color = color;
        this.startRoad = startRoad;
    }

    @Override
    public void run() {
        super.run();
        Log.d(TAG, "running");
        // has to wait at intersection if not at green

        Log.d(TAG, "done running");
    }
}
