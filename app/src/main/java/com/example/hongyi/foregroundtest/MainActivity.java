package com.example.hongyi.foregroundtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity{
    private MyReceiver broadcastreceiver;
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm:ss");

    public class MyReceiver extends BroadcastReceiver {
        private String lb_MAC = "MAC";
        private String lb_temp = "Temperature";
        private String lb_status = "Status";
        private String lb_TS = "TS";
        public MyReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String timestring = "Updated on: " + sdf.format(intent.getLongExtra("timestamp",-1));
            String MAC = intent.getStringExtra("name");
            String status = intent.getStringExtra("status");
            String temperature = intent.getStringExtra("temperature");
            String index = "";
            switch (MAC) {
                case Constants.SENSORS.SENSOR1:
                    index = "_1";
                    break;
                case Constants.SENSORS.SENSOR2:
                    index = "_2";
                    break;
                case Constants.SENSORS.SENSOR3:
                    index = "_3";
                    break;
                case Constants.SENSORS.SENSOR4:
                    index = "_4";
                    break;
            }
            if (!index.equals("")) {
                TextView TV_MAC = (TextView) findViewById(getResources().getIdentifier(lb_MAC+index,"id","com.example.hongyi.foregroundtest"));
                TV_MAC.setText(MAC);
                TextView TV_temperature = (TextView) findViewById(getResources().getIdentifier(lb_temp+index,"id","com.example.hongyi.foregroundtest"));
                if (!temperature.equals("-99999")) {
                    TV_temperature.setText(temperature);
                } else {
                    TV_temperature.setText("N/A");
                }
                TextView TV_Status = (TextView) findViewById(getResources().getIdentifier(lb_status+index,"id","com.example.hongyi.foregroundtest"));
                TV_Status.setText(status);
                TextView TV_TS = (TextView) findViewById(getResources().getIdentifier(lb_TS+index,"id","com.example.hongyi.foregroundtest"));
                TV_TS.setText(timestring);
            }
        }

        @Override
        public IBinder peekService(Context myContext, Intent service) {
            return super.peekService(myContext, service);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button1);
        button.setText("Stop Service");
        broadcastreceiver = new MyReceiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Constants.NOTIFICATION_ID.BROADCAST_TAG);
        registerReceiver(broadcastreceiver, intentfilter);
        Intent service = new Intent(MainActivity.this, ForegroundService.class);
        if (!ForegroundService.IS_SERVICE_RUNNING) {
            service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            ForegroundService.IS_SERVICE_RUNNING = true;
            startService(service);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastreceiver);
        super.onDestroy();
    }

    public void buttonClicked(View v) {
        Button button = (Button) v;
        Intent service = new Intent(MainActivity.this, ForegroundService.class);
        if (ForegroundService.IS_SERVICE_RUNNING) {
            service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            ForegroundService.IS_SERVICE_RUNNING = false;
            button.setText("Start Service");
            startService(service);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

}
