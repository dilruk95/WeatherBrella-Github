package com.example.sahandilruk.weatherbrella;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Sahan Dilruk on 12/4/2016.
 */

public class cityPreference {
    SharedPreferences prefs;
    public cityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // If the user has not chosen a city yet, return
    // Sydney as the default city
    String getCity(){
        return prefs.getString("city", "Colombo, SL");
    }
//colombo,SL
    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }

}
