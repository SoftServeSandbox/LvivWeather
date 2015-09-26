package com.example.administrator.testproject1;

import android.graphics.Bitmap;
import android.widget.TextView;

/**
 * Created by Administrator on 9/26/2015.
 */

public class WeatherData {

    private static final WeatherData sInstance = new WeatherData();

    public String txtViewDescription;
    public String tempMin;
    public String tempMax;

    public Bitmap icon;

    public static WeatherData getInstance() {
        return sInstance;
    }

    private WeatherData() {
    }

    public void SetTxtViewDescription (String txtViewDescription) {
        this.txtViewDescription = txtViewDescription;
    }

    public void SetTempMin (String tempMin) {
        this.tempMin = tempMin;
    }

    public void SetTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public void SetIcon(Bitmap icon) {
        this.icon = icon;
    }
}