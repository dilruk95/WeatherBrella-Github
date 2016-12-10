package com.example.sahandilruk.weatherbrella;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sahan Dilruk on 12/4/2016.
 */

public class Weather extends Fragment {
    TextView textViewInfo;
    Context context;
    Typeface weatherFont;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    static String status;
    Handler handler;

    NotificationCompat.Builder notification;
    private static final int uID = 45678;
    public Weather(){
        handler = new Handler();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment, container, false);
        TextView textViewInfo = (TextView) view.findViewById(R.id.info);
        cityField = (TextView)view.findViewById(R.id.city_field);
        updatedField = (TextView)view.findViewById(R.id.updated_field);
        detailsField = (TextView)view.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)view.findViewById(R.id.current_temprature_field);
        weatherIcon = (TextView)view.findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);

        context = getActivity();


        return view;
        //weather map api key: 094f2e87768d4cd2e308fac9e2350d84
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  weatherFont = Typeface.createFromAsset(getResources().openRawResource(), "fonts/weather.ttf");
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new cityPreference(getActivity()).getCity());

        if( status == "FEW CLOUDS")
        {
            notification.setSmallIcon(R.drawable.umbrella);
            notification.setTicker("hy");
            notification.setContentTitle("Hellooo");
            notification.setContentText("Im The Body");

            //Intent intent = new Intent(this, Weather.class);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(uID, notification.build());
        }
    }

    public static String GetWeather()
    {
        return status;
    }
    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            status = details.getString("description").toUpperCase(Locale.US);
            Log.e(status,status);
            System.out.print(status);
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

            //notification
            notification = new NotificationCompat.Builder(context);
            notification.setAutoCancel(true);

            if(status.equals("CLEAR SKY")) {
                notification.setSmallIcon(R.drawable.umbrella);
                notification.setTicker("WeatherBrella");
                notification.setContentTitle("It seems a bit Sunny outside");
                notification.setContentText("Dont forget to take out your WeatherBrella");

                NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                manager.notify(uID, notification.build());
            }
            else if(status.equals("LIGHT RAIN") || status.equals("MODERATE RAIN"))
            {
                notification.setSmallIcon(R.drawable.umbrella);
                notification.setTicker("WeatherBrella");
                notification.setContentTitle("It seems a to be raining outside");
                notification.setContentText("Dont forget to take out your WeatherBrella");

                NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                manager.notify(uID, notification.build());
            }



        }catch(Exception e){
            Log.e("weatherbrella", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }



    public void changeCity(String city){
        updateWeatherData(city);
    }

}





/*private class OpenWeatherMapTask extends AsyncTask<Void, Void, String> {

    String cityName;
    TextView tvResult;
    TextView textViewInfo;
    String dummyAppid = "094f2e87768d4cd2e308fac9e2350d84";
    String queryWeather = "http://api.openweathermap.org/data/2.5/weather?q=";
    String queryDummyKey = "&appid=" + dummyAppid;

    OpenWeatherMapTask(String cityName, TextView tvResult) {
        this.cityName = cityName;
         this.tvResult = tvResult;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        String queryReturn;

        String query = null;
        try {
            query = queryWeather + URLEncoder.encode(cityName, "UTF-8") + queryDummyKey;
            queryReturn = sendQuery(query);
            result += ParseJSON(queryReturn);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            queryReturn = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            queryReturn = e.getMessage();
        }


        final String finalQueryReturn = query + "\n\n" + queryReturn;

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewInfo.setText(finalQueryReturn);
                //Code for the UiThread
            }
        });
               *//* Weather.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewInfo.setText(finalQueryReturn);
            }
        });*//*


        return result;
    }

    //
    @Override
    protected void onPostExecute(String s) {
        tvResult.setText(s);
    }

    private String sendQuery(String query) throws IOException {
        String result = "Sri lanka";

        URL searchURL = new URL(query);

        HttpURLConnection httpURLConnection = (HttpURLConnection)searchURL.openConnection();
        if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader,
                    8192);

            String line = null;
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
        }

        return result;
    }

    //
    private String ParseJSON(String json){
        String jsonResult = "";

        try {
            JSONObject JsonObject = new JSONObject(json);
            String cod = jsonHelperGetString(JsonObject, "cod");

            if(cod != null){
                if(cod.equals("200")){

                    jsonResult += jsonHelperGetString(JsonObject, "name") + "\n";
                    JSONObject sys = jsonHelperGetJSONObject(JsonObject, "sys");
                    if(sys != null){
                        jsonResult += jsonHelperGetString(sys, "country") + "\n";
                    }
                    jsonResult += "\n";

                    JSONObject coord = jsonHelperGetJSONObject(JsonObject, "coord");
                    if(coord != null){
                        String lon = jsonHelperGetString(coord, "lon");
                        String lat = jsonHelperGetString(coord, "lat");
                        jsonResult += "lon: " + lon + "\n";
                        jsonResult += "lat: " + lat + "\n";
                    }
                    jsonResult += "\n";

                    JSONArray weather = jsonHelperGetJSONArray(JsonObject, "weather");
                    if(weather != null){
                        for(int i=0; i<weather.length(); i++){
                            JSONObject thisWeather = weather.getJSONObject(i);
                            jsonResult += "weather " + i + ":\n";
                            jsonResult += "id: " + jsonHelperGetString(thisWeather, "id") + "\n";
                            jsonResult += jsonHelperGetString(thisWeather, "main") + "\n";
                            jsonResult += jsonHelperGetString(thisWeather, "description") + "\n";
                            jsonResult += "\n";
                        }
                    }

                    JSONObject main = jsonHelperGetJSONObject(JsonObject, "main");
                    if(main != null){
                        jsonResult += "temp: " + jsonHelperGetString(main, "temp") + "\n";
                        jsonResult += "pressure: " + jsonHelperGetString(main, "pressure") + "\n";
                        jsonResult += "humidity: " + jsonHelperGetString(main, "humidity") + "\n";
                        jsonResult += "temp_min: " + jsonHelperGetString(main, "temp_min") + "\n";
                        jsonResult += "temp_max: " + jsonHelperGetString(main, "temp_max") + "\n";
                        jsonResult += "sea_level: " + jsonHelperGetString(main, "sea_level") + "\n";
                        jsonResult += "grnd_level: " + jsonHelperGetString(main, "grnd_level") + "\n";
                        jsonResult += "\n";
                    }

                    jsonResult += "visibility: " + jsonHelperGetString(JsonObject, "visibility") + "\n";
                    jsonResult += "\n";

                    JSONObject wind = jsonHelperGetJSONObject(JsonObject, "wind");
                    if(wind != null){
                        jsonResult += "wind:\n";
                        jsonResult += "speed: " + jsonHelperGetString(wind, "speed") + "\n";
                        jsonResult += "deg: " + jsonHelperGetString(wind, "deg") + "\n";
                        jsonResult += "\n";
                    }

                    //...incompleted

                }else if(cod.equals("404")){
                    String message = jsonHelperGetString(JsonObject, "message");
                    jsonResult += "cod 404: " + message;
                }
            }else{
                jsonResult += "cod == null\n";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            jsonResult += e.getMessage();
        }

        return jsonResult;
    }

    //
    private String jsonHelperGetString(JSONObject obj, String k){
        String v = null;
        try {
            v = obj.getString(k);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    private JSONObject jsonHelperGetJSONObject(JSONObject obj, String k){
        JSONObject o = null;

        try {
            o = obj.getJSONObject(k);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return o;
    }

    private JSONArray jsonHelperGetJSONArray(JSONObject obj, String k){
        JSONArray a = null;

        try {
            a = obj.getJSONArray(k);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return a;
    }





}}*/
