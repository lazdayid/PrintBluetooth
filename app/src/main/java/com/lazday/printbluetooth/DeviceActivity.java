package com.lazday.printbluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zj.btsdk.BluetoothService;

public class DeviceActivity extends AppCompatActivity {

    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothService mService = null;
    private ArrayAdapter<String> newDeviceAdapter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDeviceAdapter.add(device.getName() + "\n" + device.getAddress());
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    setTitle("Pilih Perangkat");
                    if (newDeviceAdapter.getCount() == 0) {
                        newDeviceAdapter.add("Perangkat tidak ditemukan");
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setTitle("Bluetooth Device");
        setupView();
    }

    private void setupView(){
        textDeviceConnected = findViewById(R.id.text_device_connected);
        listDeviceConnected = findViewById(R.id.list_device_connected);
        textDeviceAvailable = findViewById(R.id.text_device_available);
        listDeviceAvailable = findViewById(R.id.list_device_available);
        buttonScan = findViewById(R.id.button_scan);
        textDeviceAvailable.setVisibility(View.GONE);
        textDeviceConnected.setVisibility(View.GONE);
    }

    TextView textDeviceConnected, textDeviceAvailable;
    RecyclerView listDeviceConnected, listDeviceAvailable;
    Button buttonScan;
}