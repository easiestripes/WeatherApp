package com.austinpetrie.weatherapp;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class Daily extends DataBlock {

    // Required fields
    @NotNull
    private ArrayList<DailyDataPoint> data;

    public ArrayList<DailyDataPoint> getData() {
        return data;
    }

    public void setData(ArrayList<DailyDataPoint> data) {
        this.data = data;
    }
}
