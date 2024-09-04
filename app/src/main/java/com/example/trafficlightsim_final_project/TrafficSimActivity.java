package com.example.trafficlightsim_final_project;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class TrafficSimActivity extends AppCompatActivity implements ModelListener {
    public static final String TAG = TrafficSimActivity.class.getSimpleName();
    private final TrafficSimModel model = new TrafficSimModel(this);
    private Map<StartRoad, List<View>> carViewQueue = new HashMap<>();

    private ConstraintLayout roadHorizontal, roadVertical;
    private final Random random = new Random();
    private Handler autoCarHandler, autoLightHandler;
    Runnable autoCarsRunnable, autoLightsRunnable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traffic_intersection);

        roadHorizontal = findViewById(R.id.road_horizontal);
        roadVertical = findViewById(R.id.road_vertical);
        roadVertical.setElevation(1);

        for (StartRoad road : StartRoad.values()) {
            carViewQueue.put(road, new ArrayList<>());
        }

        View topRoadAdder = findViewById(R.id.top_road_car_adder);
        topRoadAdder.setOnTouchListener(touchListener);
        topRoadAdder.setOnClickListener(v -> model.addCar(new Car(Car.CarColor.RED, StartRoad.North)));

        View bottomRoadAdder = findViewById(R.id.bottom_road_car_adder);
        bottomRoadAdder.setOnTouchListener(touchListener);
        bottomRoadAdder.setOnClickListener(v -> model.addCar(new Car(Car.CarColor.RED, StartRoad.South)));

        View rightRoadAdder = findViewById(R.id.right_road_car_adder);
        rightRoadAdder.setOnTouchListener(touchListener);
        rightRoadAdder.setOnClickListener(v -> model.addCar(new Car(Car.CarColor.RED, StartRoad.East)));

        View leftRoadAdder = findViewById(R.id.left_road_car_adder);
        leftRoadAdder.setOnTouchListener(touchListener);
        leftRoadAdder.setOnClickListener(v -> model.addCar(new Car(Car.CarColor.RED, StartRoad.West)));


        RelativeLayout northLight = getTrafficLight(StartRoad.North);
        northLight.setOnClickListener(v -> {
            PopupMenu menu = getPopupMenu(v);
            menu.show();
            menu.setOnMenuItemClickListener(menuItem -> {
                try {
                    return menuOnClick(menuItem, StartRoad.North);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        RelativeLayout southLight = getTrafficLight(StartRoad.South);
        southLight.setOnClickListener(v -> {
            PopupMenu menu = getPopupMenu(v);
            menu.show();
            menu.setOnMenuItemClickListener(menuItem -> {
                try {
                    return menuOnClick(menuItem, StartRoad.South);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        RelativeLayout eastLight = getTrafficLight(StartRoad.East);
        eastLight.setOnClickListener(v -> {
            PopupMenu menu = getPopupMenu(v);
            menu.show();
            menu.setOnMenuItemClickListener(menuItem -> {
                try {
                    return menuOnClick(menuItem, StartRoad.East);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        RelativeLayout westLight = getTrafficLight(StartRoad.West);
        westLight.setOnClickListener(v -> {
            PopupMenu menu = getPopupMenu(v);
            menu.show();
            menu.setOnMenuItemClickListener(menuItem -> {
                try {
                    return menuOnClick(menuItem, StartRoad.West);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });


        autoLightHandler = new Handler();
        autoLightsRunnable = () -> {
            // randomly choose lights to switch
            try {
                switch (random.nextInt(4)) {
                    case 1:
                        model.changeLightsGreen(StartRoad.South);
                        break;
                    case 2:
                        model.changeLightsGreen(StartRoad.East);
                        break;
                    case 3:
                        model.changeLightsGreen(StartRoad.West);
                        break;
                    default:
                        model.changeLightsGreen(StartRoad.North);
                        break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            autoLightHandler.postDelayed(autoLightsRunnable, (long)((Math.random() * (7000 - 2000)) + 2000));
        };


        View autoLights = findViewById(R.id.auto_lights);
        View autoCars = findViewById(R.id.auto_cars);


        autoLights.setOnTouchListener(touchListener);
        autoLights.setOnClickListener(v -> {
            runOnUiThread(() -> {
                northLight.setClickable(false);
                southLight.setClickable(false);
                eastLight.setClickable(false);
                westLight.setClickable(false);
                topRoadAdder.setVisibility(View.VISIBLE);
                bottomRoadAdder.setVisibility(View.VISIBLE);
                rightRoadAdder.setVisibility(View.VISIBLE);
                leftRoadAdder.setVisibility(View.VISIBLE);
                ((CardView) autoCars).setCardBackgroundColor(getResources().getColor(R.color.asphalt_grey));
                ((CardView) autoLights).setCardBackgroundColor(getResources().getColor(R.color.button_green));
            });

            autoCarHandler.removeCallbacks(autoCarsRunnable);
            autoLightHandler.removeCallbacks(autoLightsRunnable);
            autoLightHandler.post(autoLightsRunnable);
        });


        autoCarHandler = new Handler();
        autoCarsRunnable = () -> {
            // randomly choose direction cars spawn from
            switch (random.nextInt(4)) {
                case 1:
                    model.addCar(new Car(Car.CarColor.RED, StartRoad.South));
                    break;
                case 2:
                    model.addCar(new Car(Car.CarColor.RED, StartRoad.East));
                    break;
                case 3:
                    model.addCar(new Car(Car.CarColor.RED, StartRoad.West));
                    break;
                default:
                    model.addCar(new Car(Car.CarColor.RED, StartRoad.North));
                    break;
            }
            autoCarHandler.postDelayed(autoCarsRunnable, (long)((Math.random() * (4000 - 1000) + 1000)));
        };


        autoCars.setOnTouchListener(touchListener);
        autoCars.setOnClickListener(v -> {
            runOnUiThread(() -> {
                northLight.setClickable(true);
                southLight.setClickable(true);
                eastLight.setClickable(true);
                westLight.setClickable(true);
                topRoadAdder.setVisibility(View.GONE);
                bottomRoadAdder.setVisibility(View.GONE);
                rightRoadAdder.setVisibility(View.GONE);
                leftRoadAdder.setVisibility(View.GONE);
                ((CardView) autoLights).setCardBackgroundColor(getResources().getColor(R.color.asphalt_grey));
                ((CardView) autoCars).setCardBackgroundColor(getResources().getColor(R.color.button_green));
            });

            // runnable with handler that goes until auto lights is enabled
            autoLightHandler.removeCallbacks(autoLightsRunnable);
            autoCarHandler.removeCallbacks(autoCarsRunnable);
            autoCarHandler.post(autoCarsRunnable);
        });


        runOnUiThread(() -> {
            northLight.setClickable(false);
            southLight.setClickable(false);
            eastLight.setClickable(false);
            westLight.setClickable(false);
            topRoadAdder.setVisibility(View.VISIBLE);
            bottomRoadAdder.setVisibility(View.VISIBLE);
            rightRoadAdder.setVisibility(View.VISIBLE);
            leftRoadAdder.setVisibility(View.VISIBLE);
        });
        autoLightHandler.post(autoLightsRunnable);
    }

    private boolean menuOnClick(MenuItem menuItem, StartRoad road) throws InterruptedException {
        if (menuItem.getItemId() == 1) {
            model.changeLightsGreen(road);
        } else {
            model.changeLightsGreen(road.equals(StartRoad.North) || road.equals(StartRoad.South) ? StartRoad.East : StartRoad.North);
        }
        return false;
    }
    private PopupMenu getPopupMenu(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.getMenu().add(0, 0 , 0, "RED");
        menu.getMenu().add(0, 1 , 1,"GREEN");

        return menu;
    }

    private void crossIntersection(StartRoad road) {
        boolean road1Empty;
        boolean road2Empty;
        StartRoad oppositeRoad = TrafficSimModel.getOppositeRoad(road);

        int j = 0;
        // will do until road queues are empty
        while (road.equals(model.getCurrCrossing().first) || road.equals(model.getCurrCrossing().second)) {
            long crossingAnimDuration = 5000 + 500L * j;
            j++;

            Log.d(TAG, "roadLayout: " + getRoadLayout(road));

            // change these to other
            List<View> road1CarViews = carViewQueue.get(road);
            List<View> road2CarViews = carViewQueue.get(oppositeRoad);

            road1Empty = road1CarViews.isEmpty();
            road2Empty = road2CarViews.isEmpty();


            if (road1Empty && road2Empty) return;
            try {
                if (!road1Empty) {
                    model.carLeaves(road);
                    carCrossIntersection(road1CarViews, crossingAnimDuration, road);

                } if (!road2Empty) {
                    model.carLeaves(oppositeRoad);
                    carCrossIntersection(road2CarViews, crossingAnimDuration, oppositeRoad);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String translation = road.equals(StartRoad.North) || oppositeRoad.equals(StartRoad.South) ? "translationY" : "translationX";

            for (int i = 0; i < road1CarViews.size(); i++) {
                float endValY = 0;
                if (i > 0) {
                    endValY += i * 150;
                }

                ObjectAnimator.ofFloat(road1CarViews.get(i), translation, endValY)
                        .setDuration(4000)
                        .start();
            }

            for (int i = 0; i < road2CarViews.size(); i++) {
                float endValY = 0;
                if (i > 0) {
                    endValY += -1 * (i * 150);
                }

                ObjectAnimator.ofFloat(road2CarViews.get(i), translation, endValY)
                        .setDuration(4000)
                        .start();
            }
        }
    }

    private void carCrossIntersection(List<View> carViews, long crossingAnimDuration, StartRoad startRoad) {
        View car = carViews.remove(0);
        float value = 0;
        String propertyName = "";
        switch (startRoad) {
            case South:
                value = -2000;
                propertyName = "translationY";
                break;
            case North:
                value = 2000;
                propertyName = "translationY";
                break;
            case East:
                value = -2000;
                propertyName = "translationX";
                break;
            case West:
                value = 2000;
                propertyName = "translationX";
                break;
        }

        ObjectAnimator anim = ObjectAnimator.ofFloat(car, propertyName, value);
        anim.setDuration(crossingAnimDuration);
        anim.start();
    }

    private void addCar(ConstraintLayout road, StartRoad startRoad) {
        Queue<Car> queue = model.getCarQueue(startRoad);
        Log.d(TAG, "addCar: " + startRoad);

        LinearLayout newLin = new LinearLayout(this);
        newLin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        newLin.setOrientation(LinearLayout.VERTICAL);


        float fromVal = 0f;
        float endVal = 0f;
        int layout = 0;

        switch (startRoad) {
            case North:
                fromVal = -1 * (float) road.getHeight() / 2;
                endVal = queue.size() == 1 ? -10 : -1 * (queue.size() - 1) * 150;
                layout = R.layout.car_start_north;
                break;
            case South:
                fromVal = (float) road.getHeight() / 2;
                endVal = queue.size() == 1 ? 10 : (queue.size() - 1) * 150;
                layout = R.layout.car_start_south;
                break;
            case East:
                fromVal = ((float) road.getWidth() / 2);
                endVal = queue.size() == 1 ? 10 : (queue.size() - 1) * 150;
                layout = R.layout.car_start_east;
                break;
            case West:
                fromVal = -1 * ((float) road.getWidth() / 2);
                endVal = queue.size() == 1 ? -10 : -1 * (queue.size() - 1) * 150;
                layout = R.layout.car_start_west;
                break;
        }

        // add car image view to linear layout
        View view = View.inflate(this, layout, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
        view.setId(View.generateViewId());
        newLin.addView(view);
        newLin.setId(View.generateViewId());
        newLin.setElevation(1);

        carViewQueue.get(startRoad).add(newLin);

        road.addView(newLin);

        String translation = startRoad.equals(StartRoad.North) || startRoad.equals(StartRoad.South) ? "translationY" : "translationX";
        ObjectAnimator.ofFloat(newLin,  translation, fromVal, endVal)
                .setDuration(2000)
                .start();

        road.setConstraintSet(getConstraintSet(road, startRoad, newLin));

        if (model.isRoadGreen(startRoad)) crossIntersection(startRoad);
    }

    private ConstraintSet getConstraintSet(ConstraintLayout road, StartRoad startRoad, View view) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(road);
        switch (startRoad) {
            case North:
                constraintSet.connect(view.getId(),ConstraintSet.START,R.id.road_vertical,ConstraintSet.START,0);
                constraintSet.connect(view.getId(),ConstraintSet.BOTTOM,R.id.middle_square_vertical,ConstraintSet.TOP,20);
                break;
            case South:
                constraintSet.connect(view.getId(),ConstraintSet.END,R.id.road_vertical,ConstraintSet.END,0);
                constraintSet.connect(view.getId(),ConstraintSet.TOP,R.id.middle_square_vertical,ConstraintSet.BOTTOM,20);
                break;
            case East:
                constraintSet.connect(view.getId(),ConstraintSet.TOP,R.id.road_horizontal,ConstraintSet.TOP,0);
                constraintSet.connect(view.getId(),ConstraintSet.START,R.id.middle_square_horizontal,ConstraintSet.END,20);
                break;
            case West:
                constraintSet.connect(view.getId(),ConstraintSet.BOTTOM,R.id.road_horizontal,ConstraintSet.BOTTOM,0);
                constraintSet.connect(view.getId(),ConstraintSet.END,R.id.middle_square_horizontal,ConstraintSet.START,20);
        }

        return constraintSet;

    }

    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener touchListener = (v, event) -> {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                v.setScaleX(1.2f);
                v.setScaleY(1.2f);
                break;
            }
            case MotionEvent.ACTION_UP: {
                v.setScaleX(1.0f);
                v.setScaleY(1.0f);
                break;
            }
        }
        return false;
    };

    @Override
    public void updateLight(StartRoad road, TrafficLight.Color color) {
        switch (color) {
            case RED:
                setRedLightsPair(road);
                break;
            case YELLOW:
                setYellowLightsPair(road);
                break;
            case GREEN:
                setGreenLightsPair(road);
        }
    }

    @Override
    public void updateCar(StartRoad road) {
        addCar(getRoadLayout(road), road);
    }

    public void setGreenLightsPair(StartRoad road) {
        turnLightsGreen(road);
        turnLightsGreen(TrafficSimModel.getOppositeRoad(road));
        setElevationOfRoad(road);
        Log.d(TAG, "setting green light: " + road);
        crossIntersection(road);
    }

    private void setRedLightsPair(StartRoad road) {
        turnLightsRed(road);
        turnLightsRed(TrafficSimModel.getOppositeRoad(road));
    }

    private void setYellowLightsPair(StartRoad road) {
        turnLightsYellow(road);
        turnLightsYellow(TrafficSimModel.getOppositeRoad(road));
    }

    private void turnLightsRed(StartRoad road) {
        RelativeLayout trafficLight = getTrafficLight(road);
        runOnUiThread(() -> {
            trafficLight.findViewById(R.id.red_light).setAlpha(1);
            trafficLight.findViewById(R.id.yellow_light).setAlpha(.3f);
            trafficLight.findViewById(R.id.green_light).setAlpha(.3f);
        });

    }

    private void turnLightsYellow(StartRoad road) {
        RelativeLayout trafficLight = getTrafficLight(road);
        runOnUiThread(() -> {
            trafficLight.findViewById(R.id.red_light).setAlpha(.3f);
            trafficLight.findViewById(R.id.yellow_light).setAlpha(1);
            trafficLight.findViewById(R.id.green_light).setAlpha(.3f);
        });
    }

    private void turnLightsGreen(StartRoad road) {
        RelativeLayout trafficLight = getTrafficLight(road);
        runOnUiThread(() -> {
            trafficLight.findViewById(R.id.red_light).setAlpha(.3f);
            trafficLight.findViewById(R.id.yellow_light).setAlpha(.3f);
            trafficLight.findViewById(R.id.green_light).setAlpha(1);
        });
    }

    private RelativeLayout getTrafficLight(StartRoad road) {
        LinearLayout trafficLightHolder = null;
        switch (road) {
            case North:
                trafficLightHolder = findViewById(R.id.north_traffic_light);
                break;
            case South:
                trafficLightHolder = findViewById(R.id.south_traffic_light);
                break;
            case East:
                trafficLightHolder = findViewById(R.id.east_traffic_light);
                break;
            case West:
                trafficLightHolder = findViewById(R.id.west_traffic_light);
                break;
        }

        return (RelativeLayout) trafficLightHolder.getChildAt(0);
    }

    private ConstraintLayout getRoadLayout(StartRoad road) {
        return (road.equals(StartRoad.North) || road.equals(StartRoad.South) ? roadVertical : roadHorizontal);
    }

    private void setElevationOfRoad(StartRoad road) {
        if (getRoadLayout(road) == roadVertical) {
            roadVertical.setElevation(1);
            roadHorizontal.setElevation(.8f);
        } else {
            roadVertical.setElevation(.8f);
            roadHorizontal.setElevation(1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoCarHandler.removeCallbacks(autoCarsRunnable);
        autoLightHandler.removeCallbacks(autoLightsRunnable);
    }
}
