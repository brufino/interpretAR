package com.microsoft.cognitiveservices.speech.samples.captions_translatar;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.util.concurrent.Future;

import static android.Manifest.permission.*;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Note: we need to request the permissions
        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);


        //changing project title
        setTitle("InterpretAR");

        //defining the array in spinner languageIN
        Spinner spinner = (Spinner) findViewById(R.id.LanguageIN);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.LanguageIN, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        //defining the second array in spinner LanguageOUT
        Spinner spinnerout = (Spinner) findViewById(R.id.ClosedCaptions);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterout = ArrayAdapter.createFromResource(this,R.array.LanguageOUT, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterout.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerout.setAdapter(adapterout);

    }

    public void accessCamera(View view) {
        //do something in response to go
        Intent intent = new Intent(this, TranslatarActivity.class);
        //intent.putExtra(ASL);
        startActivity(intent);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pirates:
                if (checked)
                    //ASL ="OFF";
                    break;
            case R.id.radio_ninjas:
                if (checked)
                    //ASL="ON";
                    break;
        }
    }
}