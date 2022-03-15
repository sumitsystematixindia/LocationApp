package com.location.app.jni_models;

public class Position {

    private static int x = 0;
    private static int y = 0;

    public static void onPositionUpdate(int x_coordinate, int y_coordinate){
        synchronized (Position.class){
            x = x_coordinate;
            y = y_coordinate;
        }
    }

    public static int getX() {
        synchronized (Position.class){
            return x;
        }
    }

    public static int getY() {
        synchronized (Position.class){
            return y;
        }
    }
}
