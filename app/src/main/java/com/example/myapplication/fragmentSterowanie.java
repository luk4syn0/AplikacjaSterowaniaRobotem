package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class fragmentSterowanie extends Fragment {

    public fragmentSterowanie() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sterowanie, container, false);
        // Initialize and set up your views here


        //TODO
        // Ogarnąć pobranie danych i wydzielenie ich a następnie cykliczną aktualizacje tutaj V

        TextView przedni = view.findViewById(R.id.textDaneCzujnikPrzod);
        TextView dolny = view.findViewById(R.id.textDaneCzujnikDol);
        TextView temperatura = view.findViewById(R.id.textDaneCzujnikTemperatura);
        TextView wilgotnosc = view.findViewById(R.id.textDaneCzujnikWilgotnosc);

        przedni.setText(((MainActivity)getActivity()).daneBT);
        dolny.setText(((MainActivity)getActivity()).daneBT);
        temperatura.setText(((MainActivity)getActivity()).daneBT);
        wilgotnosc.setText(((MainActivity)getActivity()).daneBT);

        return view;
    }



}
