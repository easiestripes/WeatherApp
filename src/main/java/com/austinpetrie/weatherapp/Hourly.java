package com.austinpetrie.weatherapp;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class Hourly extends DataBlock {

    // Required fields
    @NotNull
    private ArrayList<HourlyDataPoint> data;
}
