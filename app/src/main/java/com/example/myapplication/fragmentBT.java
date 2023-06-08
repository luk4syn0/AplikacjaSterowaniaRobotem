package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class fragmentBT extends Fragment {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    private static final String TAG = "MyActivity";

    private BluetoothAdapter bluetoothAdapter;
    private TextView connectionStatusTextView;

    //TODO
    // Ogarnąć te permisje bo aż oczy bolą od tego bałaganu
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bt, container, false);

        // Inicjalizacja BluetoothAdapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Sprawdzenie, czy urządzenie wspiera BT
        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(getContext(), "Urządzenie nie obsługuje Bluetooth.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        // Sprawdzenie uprawnień lokalizacji (wymagane od Androida 6.0)
        if (!hasLocationPermission()) {
            requestLocationPermission();
        }

        // Inicjalizacja pola TextView dla stanu połączenia
        connectionStatusTextView = view.findViewById(R.id.textViewConnectionStatus);

        // Inicjalizacja listy urządzeń
        ListView deviceListView = view.findViewById(R.id.listViewDevices);
        ArrayList<String> deviceListNazwy = getPairedDevices().get(0);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, deviceListNazwy);
        deviceListView.setAdapter(adapter);

        Log.d(TAG, "setPairedDevice: Done ");

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ArrayList<String> deviceListAdresy = getPairedDevices().get(1);

                String adress = (String) deviceListAdresy.get(position);
                Log.d(TAG, "onItemClick: " + adress);
//
//                Toast.makeText(getActivity(), "Selected device: " + selectedDevice, Toast.LENGTH_SHORT).show();

                //TODO
                // Dopisać thread na serwer, bo to na głównym wątku nie powinno działać:
                // BluetoothDevice selectedDevice = bluetoothManager.getAdapter().getRemoteDevice(adress);
                // connectToDevice(selectedDevice);

            }
        });

        // Inicjalizacja przycisku skanowania
        Button refreshButton = view.findViewById(R.id.buttonRefresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePairedDevices();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Włączenie Bluetooth, jeśli jest wyłączony
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }


    //TODO
    // Użyć przy połączeniu tę funkcję obsługującą zmianę napisu odnośnie statusu połączenia
    private void onConnectionStateChanged(boolean isConnected) {
        if (isConnected) {
            connectionStatusTextView.setText("Połączono");
        } else {
            connectionStatusTextView.setText("Rozłączono");
        }
    }


    public void connectToDevice(BluetoothDevice device) {
        BluetoothSocket socket = null;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standardowy UUID dla SPP (Serial Port Profile)

        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            Log.d(TAG, "Połączono. Status: " + socket.isConnected());

            Log.d(TAG, "COS " + socket.toString());

            // Tutaj możesz wykonywać operacje na połączonym urządzeniu Bluetooth

        } catch (IOException e) {
            e.printStackTrace();
            // Obsłuż błędy połączenia
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Aktualizacja sparowanych urządzeń w liście (aktualizacja adaptera)
    private void updatePairedDevices() {
        ArrayList<String> deviceListNazwy = getPairedDevices().get(0);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, deviceListNazwy);
        adapter.notifyDataSetChanged();

        Log.d(TAG, "updatePairedDevice: Done ");
    }

    // Funkcja pomocnicza.
    // Zwraca ArrayListy nazw[0] oraz adresów[1] sparowanych urządzeń
    public ArrayList<ArrayList<String>> getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> deviceListAdresy = new ArrayList<>();
        ArrayList<String> deviceListNazwy = new ArrayList<>();

        for (BluetoothDevice device : pairedDevices) {
            deviceListNazwy.add(device.getName());
            deviceListAdresy.add(device.getAddress());
        }

        ArrayList<ArrayList<String>> arrays = new ArrayList<>();
        arrays.add(deviceListNazwy);
        arrays.add(deviceListAdresy);
        return arrays;
    }

    //TODO
    // Dodac permisje

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_LOCATION,
                    1
            );
        }
        Log.v(TAG, "Perms Checked");
    }
}