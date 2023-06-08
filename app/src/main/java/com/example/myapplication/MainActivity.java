package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);







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
                transaction.replace(R.id.widokGlowneMenu, fragmentController);

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
}