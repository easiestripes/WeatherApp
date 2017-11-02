package com.austinpetrie.weatherapp;

public class GeoCoords {

    private final String lat;
    private final String lng;

    public GeoCoords(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}
