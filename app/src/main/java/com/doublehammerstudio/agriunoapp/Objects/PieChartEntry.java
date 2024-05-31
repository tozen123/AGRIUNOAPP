package com.doublehammerstudio.agriunoapp.Objects;

public class PieChartEntry {
    private float number;
    private String name;
    private int color;

    public PieChartEntry(float number, String name, int color) {
        this.number = number;
        this.name = name;
        this.color = color;
    }

    public float getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
