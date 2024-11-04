package com.example.MyNotificationListenerService;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Initialises variables
    private TextView notificationTextView;
    BluetoothHelper bluetoothHelper = new BluetoothHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("fml", "fml la");
        super.onCreate(savedInstanceState);

        openNotificationAccessSettings(); // Enable notifications
        openBluetoothPermissionSettings(); // Enable bluetooth access

        // Set the UI of the app
        setContentView(R.layout.activity_main);
        notificationTextView = findViewById(R.id.notificationTextView);

        // Start scanning for BLE devices (our Tiny ST BLE)
//        bluetoothHelper.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void openNotificationAccessSettings() {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }

    public void openBluetoothPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}
