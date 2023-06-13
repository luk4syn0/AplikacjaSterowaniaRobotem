package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class fragmentSterowanie extends Fragment {

    public fragmentSterowanie() {
        // Required empty public constructor
    }

    boolean gora = false;
    boolean dol = false;
    boolean lewo = false;
    boolean prawo = false;

    boolean swiatlaLewo = false;

    boolean swiatlaPrawo = false;

    String kodSterowanie = "0";
    String kodSterowanieBufor = "0";

    String kodSwiatla = "0";

    String kodSwiatlaBufor = "0";

    TextView przedni;
    TextView dolny;
    TextView temperatura;
    TextView wilgotnosc;

    Runnable runnable;

    Handler handler = new Handler();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sterowanie, container, false);
        // Initialize and set up your views here


        //TODO
        // Ogarnąć pobranie danych i wydzielenie ich a następnie cykliczną aktualizacje tutaj V




        ImageButton doPrzodu = view.findViewById(R.id.imageButtonGora);
        doPrzodu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    gora = true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gora = false;

                }
                sprawdzOrazWyslij();
                return false;
            }
        });

        ImageButton doTylu = view.findViewById(R.id.imageButtonDol);
        doTylu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    dol = true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    dol = false;

                }
                sprawdzOrazWyslij();
                return false;
            }
        });

        ImageButton wLewo = view.findViewById(R.id.imageButtonLewo);
        wLewo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lewo = true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    lewo = false;

                }
                sprawdzOrazWyslij();
                return false;
            }
        });

        ImageButton wPrawo = view.findViewById(R.id.imageButtonPrawo);
        wPrawo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    prawo = true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    prawo = false;

                }
                sprawdzOrazWyslij();
                return false;
            }
        });

        ImageButton kierunekPrawo = view.findViewById(R.id.imageButtonKierunekPrawo);
        kierunekPrawo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!swiatlaLewo && !swiatlaPrawo) {
                    swiatlaPrawo = true;
                } else if (swiatlaLewo && !swiatlaPrawo) {
                    swiatlaLewo = false;
                    swiatlaPrawo = true;
                } else {
                    swiatlaPrawo = false;
                }
                sprawdzOrazWyslij();
            }
        });

        ImageButton kierunekLewo = view.findViewById(R.id.imageButtonKierunekLewo);
        kierunekLewo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!swiatlaLewo && !swiatlaPrawo) {
                    swiatlaLewo = true;
                } else if (!swiatlaLewo && swiatlaPrawo) {
                    swiatlaLewo = true;
                    swiatlaPrawo = false;
                } else {
                    swiatlaLewo = false;
                }
                sprawdzOrazWyslij();
            }
        });
        przedni = view.findViewById(R.id.textDaneCzujnikPrzod);
        dolny = view.findViewById(R.id.textDaneCzujnikDol);
        temperatura = view.findViewById(R.id.textDaneCzujnikTemperatura);
        wilgotnosc = view.findViewById(R.id.textDaneCzujnikWilgotnosc);

        ((MainActivity)getActivity()).sterowanieAktywne = true;
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, 300);
                aktualizujUi();
            }
        }, 300);
        return view;
    }

    public void sprawdzOrazWyslij() {
        Log.d("My Activity", "sprawdzOrazWyslij: Sprawdzam");
        if (gora) {
            if (lewo) {
                kodSterowanie = "2";
            }
            else {
                if (prawo) {
                    kodSterowanie = "3";
                }
                else {
                    kodSterowanie = "1";
                }
            }
        } else {
            if (dol) {
                if (lewo) {
                    kodSterowanie = "6";
                }
                else {
                    if (prawo) {
                        kodSterowanie = "7";
                    }
                    else {
                        kodSterowanie = "8";
                    }
                }
            }
            else {
                if (lewo) {
                    kodSterowanie = "4";
                }
                else {
                    if (prawo) {
                        kodSterowanie = "5";
                    }
                    else {
                        kodSterowanie = "0";
                    }
                }
            }
        }

        if (swiatlaLewo) {
            kodSwiatla = "1";
        } else if (swiatlaPrawo) {
            kodSwiatla = "2";
        } else {
            kodSwiatla = "0";
        }


        if (kodSterowanie != kodSterowanieBufor || kodSwiatla != kodSwiatlaBufor) {
            Log.d("My Activity", "sprawdzOrazWyslij: Wywołam teraz funkcję .wyslij()");
            String pakiet = "`"+kodSterowanie+kodSwiatla+"~";
            kodSterowanieBufor = kodSterowanie;
            kodSwiatlaBufor = kodSwiatla;
            ((MainActivity)getActivity()).wyslij(pakiet.getBytes());
        }

    }

    public void aktualizujUi() {
        przedni.setText(((MainActivity)getActivity()).czujPrzod);
        dolny.setText(((MainActivity)getActivity()).czujDol);
        temperatura.setText(((MainActivity)getActivity()).czujTemp);
        wilgotnosc.setText(((MainActivity)getActivity()).czujWilg);
    }



}
