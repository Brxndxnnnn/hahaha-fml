package com.example.MyNotificationListenerService;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.util.Log;

import java.util.UUID;

public class BluetoothHelper {
    private static final String TAG = "BLEHelper";
    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothLeScanner bluetoothLeScanner;
    private String targetDeviceName = "BlueNRG"; // Replace with your BLE device name

    // UUIDs for the service and characteristic you want to read/write
    private static final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    private BluetoothGatt bluetoothGatt;

    public BluetoothHelper() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    @SuppressLint("MissingPermission")
    public void startScan() {
        bluetoothLeScanner.startScan(scanCallback);
        Log.d(TAG, "Started BLE scan");
    }

    @SuppressLint("MissingPermission")
    public void stopScan() {
        bluetoothLeScanner.stopScan(scanCallback);
        Log.d(TAG, "Stopped BLE scan");
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "Found device: " + device.getName() + ", " + device.getAddress());
            if (device.getName() != null && device.getName().equals(targetDeviceName) && device.getAddress().equals("E4:22:9C:C9:20:BA")) {
                stopScan();
                connectToDevice(device);
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        Log.d(TAG, "Connecting to device: " + device.getName());
        device.connectGatt(null, false, gattCallback);
    }

    @SuppressLint("MissingPermission")
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.d(TAG, "Connected to GATT server.");
                bluetoothGatt = gatt;
                bluetoothGatt.discoverServices(); // Start service discovery
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT server.");
                bluetoothGatt = null;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(SERVICE_UUID);
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                    if (characteristic != null) {
                        writeToCharacteristic(characteristic, "Phone connected"); // Send data to characteristic
                    }
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        public void writeToCharacteristic(BluetoothGattCharacteristic characteristic, String data) {
            // Convert the data to bytes and set it on the characteristic
            byte[] dataBytes = data.getBytes();
            characteristic.setValue(dataBytes);

            // Write the characteristic
            boolean success = bluetoothGatt.writeCharacteristic(characteristic);
            if (success) {
                Log.d(TAG, "Data written to characteristic: " + data);
            } else {
                Log.e(TAG, "Failed to write characteristic");
            }
        }
    };
}
