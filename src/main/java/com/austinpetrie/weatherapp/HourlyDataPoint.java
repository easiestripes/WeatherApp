package com.austinpetrie.weatherapp;

public class HourlyDataPoint extends DataPoint {

    // Optional fields
    private double precipAccumulation;

    public double getPrecipAccumulation() {
        return precipAccumulation;
    }

    public void setPrecipAccumulation(double precipAccumulation) {
        this.precipAccumulation = precipAccumulation;
    }
}
