package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BluetoothSocket socket;
    Handler bt_handler;
    int handlerState;
    OutputStream outputStream;
    InputStream inputStream;
    ConnectedThread connectedThread;

    String TAG = "My Activity";

    String daneBT = "CheckBT";
    String[] podzieloneDane = new String[4];

    String czujPrzod;
    String czujDol;
    String czujTemp;
    String czujWilg;

    boolean sterowanieAktywne = false;

    FragmentManager manager = getSupportFragmentManager();





    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Inicjalizacja handlera dla BT
        bt_handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;

                    Log.v(TAG, readMessage);
                    daneBT = readMessage;
                    if (daneBT.length() > 7) {
                        String x = daneBT.substring(daneBT.indexOf("'")+2, daneBT.indexOf("~"));
                        podzieloneDane = x.split("\\|");
                        czujPrzod = podzieloneDane[0];
                        czujDol = podzieloneDane[1];
                        czujWilg = podzieloneDane[2];
                        czujTemp = podzieloneDane[3];
                    }




                }
            }
        };




//        final ConstraintLayout drawer = (ConstraintLayout) findViewById(R.id.leftDrawer);
        ImageButton navMenuButton = (ImageButton) findViewById(R.id.navMenuMainButton);

        final boolean[] isDrawerVisible = {false};
        navMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobierz referencję do ConstraintLayout o nazwie "drawer"
                ConstraintLayout drawer = findViewById(R.id.leftDrawer);

                // Ustaw czas trwania animacji w milisekundach
                int animDuration = 1000;

                // Utwórz animację Alpha dla ConstraintLayout
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(drawer, View.ALPHA, 0f, 1f);
                alphaAnimator.setDuration(animDuration);

                // Ustaw flagę, aby śledzić stan widoczności ConstraintLayout

                //Rozważ animację dla parametru isDrawerVisible
                if (isDrawerVisible[0]) {
                    // Ukryj ConstraintLayout
                    alphaAnimator.reverse();
                } else {
                    // Pokaż ConstraintLayout
                    drawer.setVisibility(View.VISIBLE);
                    alphaAnimator.start();
                }
                // Zmień stan flagi
                isDrawerVisible[0] = !isDrawerVisible[0];


                // Dodaj listener do animacji
                alphaAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isDrawerVisible[0]) {
                            // Ukryj ConstraintLayout po zakończeniu animacji
                            drawer.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });



        // Dodanie funkcjonalności do przycisku kierującego do kontrolera w "navbarze"
        ImageButton goToKontrolerButton = (ImageButton) findViewById(R.id.leftDrawerKontrolerImageButton);
        goToKontrolerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragmentController = new fragmentSterowanie();

                // Rozpocznij transakcję fragmentu
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Zastąp bieżący fragment nowym fragmentem
                transaction.replace(R.id.widokGlowneMenu, fragmentController, "sterowanko");

                // Dodaj transakcję do stosu wstecz
                transaction.addToBackStack(null);

                // Wykonaj transakcję
                transaction.commit();
            }
        });

        // Dodanie funkcjonalności do przycisku kierującego do bluetooth w "navbarze"
        ImageButton goToBLEButton = (ImageButton) findViewById(R.id.leftDrawerBLE);
        goToBLEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragmentController = new fragmentBT();

                // Rozpocznij transakcję fragmentu
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Zastąp bieżący fragment nowym fragmentem
                transaction.replace(R.id.widokGlowneMenu, fragmentController);

                // Dodaj transakcję do stosu wstecz
                transaction.addToBackStack(null);

                // Wykonaj transakcję
                transaction.commit();




            }
        });

        // Dodanie funkcjonalności do przycisku kierującego do informacji w "navbarze"
        ImageButton goToInfoButton = (ImageButton) findViewById(R.id.leftDrawerInfoImageButton);
        goToInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragmentInfo = new fragmentInformacje();

                // Rozpocznij transakcję fragmentu
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Zastąp bieżący fragment nowym fragmentem
                transaction.replace(R.id.widokGlowneMenu, fragmentInfo);

                // Dodaj transakcję do stosu wstecz
                transaction.addToBackStack(null);

                // Wykonaj transakcję
                transaction.commit();
            }
        });





    }
    public void connectToDevice(BluetoothDevice device) {
//        BluetoothSocket socket = null;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standardowy UUID dla SPP (Serial Port Profile)
//
        try {
            socket=device.createInsecureRfcommSocketToServiceRecord(uuid);
            socket.connect();
            outputStream=socket.getOutputStream();
            inputStream=socket.getInputStream();
            connectedThread = new ConnectedThread(socket);
            connectedThread.start();
            Log.d(TAG, "connectToDevice: Wykonane");



        }catch(Exception e){
            /** Handle the exception here **/
        }
    }

    private class ConnectedThread extends Thread {
        InputStream inputStream=null;
        int avilableBytes=0;

        public ConnectedThread(BluetoothSocket socket){
            InputStream temp=null;
            try{
                temp=socket.getInputStream();
            }catch (IOException e){
                e.printStackTrace();
            }
            inputStream=temp;
        }

        public void run() {
            try{
                int bytes;
                while (true){
                    try{
                        avilableBytes=inputStream.available();
                        byte[] buffer=new byte[avilableBytes];
                        if (avilableBytes>0){
                            bytes=inputStream.read(buffer);
                            final String readMessage=new String(buffer);
                            if (bytes>=3){
                                bt_handler.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                            }
                            else {
                                SystemClock.sleep(100);
                            }
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void write(byte[] bytesToSend) {
            try {
                outputStream.write(bytesToSend);
                Log.d(TAG, "write: OutputStream WYSLANO");
                // Share the sent message with the UI activity.
                Message writtenMsg = bt_handler.obtainMessage(
                        handlerState, -1, -1, new String(bytesToSend));  // Cast byte[] do Stringa w tym miejscu napsuł mi dużo krwi :)
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

//                // Send a failure message back to the activity.
//                Message writeErrorMsg =
//                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
//                Bundle bundle = new Bundle();
//                bundle.putString("toast",
//                        "Couldn't send data to the other device");
//                writeErrorMsg.setData(bundle);
//                handler.sendMessage(writeErrorMsg);
            }
        }


    }
    public void wyslij(byte[] bytesToSend) {
        if (socket.isConnected()) {
            connectedThread.write(bytesToSend);
            Log.d(TAG, "wyslij: Wyslano " + new String(bytesToSend));
        }
        else {
            Log.d(TAG, "wyslij: Cos sie zepsulo");
        }

    }
}