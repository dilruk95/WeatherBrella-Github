package com.example.sahandilruk.weatherbrella;


import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Sahan Dilruk on 12/4/2016.
 */

public class Connect extends Fragment {

    NotificationCompat.Builder notification;
    private static final int uID = 45678;
    Context context;
    String status = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();

        View view = inflater.inflate(R.layout.connect_fragment, container, false);
        Button cnt = (Button)view.findViewById(R.id.buttonConnect);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
           // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        /*status = Weather.GetWeather();
        notification = new NotificationCompat.Builder(context);
        notification.setAutoCancel(true);

        if(status.equals("FEW CLOUDS")) {
            notification.setSmallIcon(R.drawable.umbrella);
            notification.setTicker("hy");
            notification.setContentTitle("Hellooo");
            notification.setContentText("Im The Body");

            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            manager.notify(uID, notification.build());
        }*/


        System.out.println(status);
        Log.e("sasda","sadas");
       return view;
    }


}
