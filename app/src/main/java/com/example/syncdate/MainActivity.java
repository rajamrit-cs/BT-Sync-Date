package com.example.syncdate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private OutputStream outputStream;
    private InputStream inStream;

    Button sync_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sync_btn = findViewById(R.id.sync_button);
        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
                if (blueAdapter != null) {
                    if (blueAdapter.isEnabled()) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                        if(bondedDevices.size() > 0) {
                            Object[] devices = (Object []) bondedDevices.toArray();
                            BluetoothDevice device = (BluetoothDevice) devices[0];
                            ParcelUuid[] uuids = device.getUuids();
                            try {
                                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                                try {
                                    socket.connect();
                                }catch (IOException e){
                                    Toast.makeText(getApplicationContext(), "Socket Connection Failed.", Toast.LENGTH_SHORT).show();
                                }
                                try {
                                    outputStream = socket.getOutputStream();
                                }catch (IOException e){
                                    Toast.makeText(getApplicationContext(), "Socket Get Output Stream Failed.", Toast.LENGTH_SHORT).show();
                                }
                                try {
                                    Date c = Calendar.getInstance().getTime();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                                    outputStream.write(df.format(c).getBytes());
                                    Toast.makeText(getApplicationContext(), "Successfully Written Date.", Toast.LENGTH_SHORT).show();
                                }catch (IOException e){
                                    Toast.makeText(getApplicationContext(), "Socket Write Failed.", Toast.LENGTH_SHORT).show();
                                }

                                try {
                                    socket.close();
                                }catch (IOException e){
                                    Toast.makeText(getApplicationContext(), "Socket Close Failed.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
//                                throw new RuntimeException(e);
                                Toast.makeText(getApplicationContext(), "No Bluetooth device is Connected.", Toast.LENGTH_SHORT).show();
                            }
                        }
//                        Log.e("error", "No appropriate paired devices.");
                        Toast.makeText(getApplicationContext(), "No appropriate paired devices.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Bluetooth is disabled.", Toast.LENGTH_SHORT).show();
//                        Log.e("error", "Bluetooth is disabled.");
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Adapter is null.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void write(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

    public void run() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;

        while (true) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}