package com.example.trafficlightsim_final_project;

import android.view.View;

public interface ModelListener {
     // should add update car and update light or some shit
     // should add some sort of way for the model to tell when car should moves in line
     // model should tell when to move through intersection

     void updateLight(StartRoad road, TrafficLight.Color color);
     void updateCar(StartRoad road);

}
