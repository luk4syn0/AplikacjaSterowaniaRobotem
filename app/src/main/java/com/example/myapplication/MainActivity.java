package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView textView = (TextView) findViewById(R.id.topText);

        String topowaWiadomosc = getString(R.string.topText);
        textView.setText(topowaWiadomosc);

//        final ConstraintLayout drawer = (ConstraintLayout) findViewById(R.id.leftDrawer);
        ImageButton imageButton = (ImageButton) findViewById(R.id.navMenuMainButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
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
                final boolean[] isDrawerVisible = {false};

                // Pobierz referencję do przycisku
                ImageButton button = findViewById(R.id.navMenuMainButton);

                // Ustaw listener dla przycisku
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                    }
                });

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
    }
}