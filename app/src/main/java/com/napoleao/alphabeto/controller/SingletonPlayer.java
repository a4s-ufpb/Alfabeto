package com.napoleao.alphabeto.controller;

/**
 * Class responsible for managing the player's score.
 */
public class SingletonPlayer {

    private float points = 3;

    private static SingletonPlayer player;

    /**
     * Constructor of the class.
     */
    private SingletonPlayer() {

    }

    /**
     * Method responsible for returning the instance of the class.
     *
     * @return
     */
    public static SingletonPlayer getPlayer() {
        if (player == null) {
            player = new SingletonPlayer();
        }
        return player;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public void resetPoint() {
        points = 3;
    }

}
