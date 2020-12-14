package com.app.ShelfAware;
//imports

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

//a class to change settings

public class SettingsActivity extends AppCompatActivity {

    private final static String SHARED_PREF_FILE_NAME = "com.app.shelfaware";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    ConstraintLayout currentLayout;

    RadioButton BGlight;
    RadioButton BGdark;

    RadioButton LMrating;
    RadioButton LMalphabet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentLayout = findViewById(R.id.main_layout);

        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        BGlight = findViewById(R.id.RBlight);
        BGdark = findViewById(R.id.RBdark);

        LMalphabet = findViewById(R.id.RBalphabet);
        LMrating = findViewById(R.id.RBrating);


        configureRadioButtons();
        configureBackButton();

        boolean prefBGLight = sharedPreferences.getBoolean("BGLight", true);
        if (!prefBGLight){
            currentLayout.setBackgroundColor(Color.DKGRAY);
            BGlight.setChecked(false);
            BGdark.setChecked(true);
        }
        else{
            BGdark.setChecked(false);
            BGlight.setChecked(true);
        }

        boolean prefSortModeAlpha = sharedPreferences.getBoolean("SortModeAlpha", true);
        if(prefSortModeAlpha){
            LMalphabet.setChecked(true);
            LMrating.setChecked(false);
        }
        else{
            LMalphabet.setChecked(false);
            LMrating.setChecked(true);
        }




    }


    private void configureBackButton(){
        //find the back button by it's ID
        ImageView backButton = findViewById(R.id.IVbackButton);
        //set a listener to the back button for when it is clicked
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            //when the back button is clicked
            public void onClick(View view){
                //finish the activity, returning to the main activity
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });
    }

    private void configureRadioButtons(){

        BGlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BGdark.setChecked(false);
                prefsEditor.putBoolean("BGLight", true);
                prefsEditor.apply();
                currentLayout.setBackgroundColor(Color.WHITE);
            }
        });


        BGdark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BGlight.setChecked(false);
                prefsEditor.putBoolean("BGLight", false);
                prefsEditor.apply();
                currentLayout.setBackgroundColor(Color.DKGRAY);


            }
        });


        LMrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LMalphabet.setChecked(false);
                prefsEditor.putBoolean("LMAlphabet", false);
                prefsEditor.apply();


            }
        });


        LMalphabet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LMrating.setChecked(false);
                prefsEditor.putBoolean("LMAlphabet", true);
                prefsEditor.apply();
            }
        });

    }



}