package com.example.trafficlightsim_final_project;

import android.os.Handler;

import java.util.*;


public class TrafficSimModel {

    private static final String TAG = TrafficSimModel.class.getSimpleName();
    private Map<StartRoad, Queue<Car>> carQueues;
    private Map<StartRoad, TrafficLight> lights;
    private Pair<StartRoad, StartRoad> currCrossing;
    private ModelListener listener;

    public TrafficSimModel(ModelListener listener) {
        carQueues = new HashMap<>();
        this.lights = new HashMap<>();
        this.listener = listener;

        for (StartRoad road : StartRoad.values()) {
            carQueues.put(road, new LinkedList<>());
            lights.put(road, new TrafficLight(3000));
        }

        // start with north to south road being green
        currCrossing = new Pair<>(StartRoad.North, StartRoad.South);
        Objects.requireNonNull(lights.get(StartRoad.North)).currColor = TrafficLight.Color.GREEN;
        Objects.requireNonNull(lights.get(StartRoad.South)).currColor = TrafficLight.Color.GREEN;
    }

    public Queue<Car> getCarQueue(StartRoad road) {
        return carQueues.get(road);
    }

    public void addCar(Car car) {
        // add limit maybe 3 cars per road
        Objects.requireNonNull(carQueues.get(car.startRoad)).add(car);
        listener.updateCar(car.startRoad);
    }

    public void carLeaves(StartRoad road) throws InterruptedException {

        if (!carQueues.get(road).isEmpty()) {
            Car car = carQueues.get(road).remove();
            car.start();
            car.join();
        }
    }

    public synchronized void changeLightsGreen(StartRoad road) throws InterruptedException {
        // since it's two roads at a time, select the two roads opposite of each other to cross\\

        if (road != currCrossing.first && road != currCrossing.second) {
            Pair<StartRoad, StartRoad> roadsCrossing = new Pair<>(road, getOppositeRoad(road));

            TrafficLight first = lights.get(roadsCrossing.first);
            TrafficLight second = lights.get(roadsCrossing.second);

            TrafficLight currFirst = lights.get(currCrossing.first);
            TrafficLight currSecond = lights.get(currCrossing.second);

            currFirst.changeColor();
            currSecond.changeColor();

            listener.updateLight(currCrossing.first, TrafficLight.Color.YELLOW);

            // CHANGE THREADS TO HANDLERS
            new Handler().postDelayed(() -> {
                listener.updateLight(currCrossing.first, TrafficLight.Color.RED);

                new Handler().postDelayed(() -> {
                    first.changeColor();
                    second.changeColor();

                    currCrossing.first = roadsCrossing.first;
                    currCrossing.second = roadsCrossing.second;
                    listener.updateLight(road, TrafficLight.Color.GREEN);
                }, 500);
            }, currFirst.yellowTimeMillis);
        }
    }

    public static StartRoad getOppositeRoad(StartRoad road) {
        switch (road) {
            case North:
                return StartRoad.South;
            case South:
                return StartRoad.North;
            case East:
                return StartRoad.West;
            case West:
                return StartRoad.East;
            default:
                // never will happen
                return null;
        }
    }

    public Pair<StartRoad, StartRoad> getCurrCrossing() {
        return currCrossing;
    }

    public boolean isRoadGreen(StartRoad road) {
        return lights.get(road).currColor == TrafficLight.Color.GREEN;
    }

}
