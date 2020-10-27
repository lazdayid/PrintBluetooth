package com.lazday.printbluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.btsdk.BluetoothService;
import com.zj.btsdk.PrintPic;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PrintActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks, BluetoothHandler.HandlerInterface{

    private final String TAG = PrintActivity.class.getSimpleName();
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;

    private BluetoothService mService = null;
    private boolean isPrinterReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        setupView();
        setupListener();
        setupBluetooth();
    }

    @AfterPermissionGranted(RC_BLUETOOTH)
    private void setupBluetooth() {
        String[] params = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission",
                    RC_BLUETOOTH, params);
            return;
        }
        mService = new BluetoothService(this, new BluetoothHandler(this));
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
    }

    @Override
    public void onDeviceConnected() {
        isPrinterReady = true;
        textStatus.setText("Terhubung dengan perangkat");
    }

    @Override
    public void onDeviceConnecting() {
        textStatus.setText("Sedang menghubungkan...");
    }

    @Override
    public void onDeviceConnectionLost() {
        isPrinterReady = false;
        textStatus.setText("Koneksi perangkat terputus");
    }

    @Override
    public void onDeviceUnableToConnect() {
        textStatus.setText("Tidak dapat terhubung ke perangkat");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult: bluetooth aktif");
                } else
                    Log.i(TAG, "onActivityResult: bluetooth harus aktif untuk menggunakan fitur ini");
                break;
            case RC_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {
//                    String address = data.getExtras().getString(DeviceActivity.EXTRA_DEVICE_ADDRESS);
//                    BluetoothDevice mDevice = mService.getDevByMac(address);
//                    mService.connect(mDevice);
                }
                break;
        }
    }

    private void requestBluetooth() {
        if (mService != null) {
            if (!mService.isBTopen()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
            }
        }
    }

    private void setupListener(){
        buttonPrintText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mService.isAvailable()) {
                    Log.i(TAG, "printText: perangkat tidak support bluetooth");
                    return;
                }
                if (isPrinterReady) {
                    if (editPrint.getText().toString().isEmpty()) {
                        Toast.makeText(PrintActivity.this, "Cant print null text", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mService.write(PrinterCommands.ESC_ALIGN_CENTER);
                    mService.sendMessage(editPrint.getText().toString(), "");
                    mService.write(PrinterCommands.ESC_ENTER);
                } else {
                    if (mService.isBTopen())
                        startActivityForResult(new Intent(PrintActivity.this, DeviceActivity.class), RC_CONNECT_DEVICE);
                    else
                        requestBluetooth();
                }
            }
        });
        buttonPrintImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPrinterReady) {
                    PrintPic pg = new PrintPic();
                    pg.initCanvas(400);
                    pg.initPaint();
                    pg.drawImage(0, 0, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Londree/struk_londree.png");
                    byte[] sendData = pg.printDraw();
                    mService.write(PrinterCommands.ESC_ALIGN_CENTER);
                    mService.write(sendData);
                }
            }
        });
    }

    private void setupView(){
        editPrint = findViewById(R.id.edit_print);
        buttonPrintText = findViewById(R.id.button_print_text);
        buttonPrintImage = findViewById(R.id.button_print_image);
        imagePrint = findViewById(R.id.image_print);
        textStatus = findViewById(R.id.text_status);
    }

    EditText editPrint;
    TextView buttonPrintText, buttonPrintImage;
    ImageView imagePrint;
    TextView textStatus;
}