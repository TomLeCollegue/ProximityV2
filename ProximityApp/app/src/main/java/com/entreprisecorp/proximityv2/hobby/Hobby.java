package com.entreprisecorp.proximityv2.hobby;

public class Hobby {
    private String name;
    private int points;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Hobby(String name, int points) {
        this.name = name;
        this.points = points;
    }
}
