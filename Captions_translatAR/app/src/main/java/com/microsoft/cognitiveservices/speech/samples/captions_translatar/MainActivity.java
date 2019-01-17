package com.microsoft.cognitiveservices.speech.samples.captions_translatar;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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


    //INITIALIZING RADIO BUTTONS
    String stringResultASL = "ON";
    String stringResultCC = "ON";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //changing project title
        setTitle("InterpretAR");

//        //defining the array in spinner languageIN
//        Spinner spinner = (Spinner) findViewById(R.id.LanguageIN);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.LanguageIN, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
//
//
//        //defining the second array in spinner LanguageOUT
//        Spinner spinnerout = (Spinner) findViewById(R.id.ClosedCaptions);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapterout = ArrayAdapter.createFromResource(this,R.array.LanguageOUT, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapterout.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinnerout.setAdapter(adapterout);

    }

    public void accessCamera(View view) {
        //do something in response to go
        Intent intent = new Intent(this, TranslatarActivity.class);
        intent.putExtra("radioChosenASL", stringResultASL); // pass "stringResultASL" to the next Activity
        intent.putExtra("radioChosenCC", stringResultCC); // pass "stringResultCC" to the next Activity
        startActivity(intent);
    }


    // controls the CC button
    boolean CCisOn = true;
    public void CCbutonClicked(View view) {
        ImageView button = (ImageView) view;
        int CCicon;

        if (CCisOn) {
            CCisOn = false;
            CCicon = R.drawable.cc_on;
        } else {
            CCisOn = true;
            CCicon = R.drawable.cc_off;
        }
        button.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), CCicon));

    }

    // controls the ASL button
    boolean ASLisOn = true;
    public void ASLbuttonClicked(View view) {
        ImageView button = (ImageView) view;
        int ASLicon;


        if (ASLisOn) {
            ASLisOn = false;
            ASLicon = R.drawable.asl_on;

        } else {
            ASLisOn = true;
            ASLicon = R.drawable.asl_off;
        }
        button.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), ASLicon));
    }

    // controls the input language button
    boolean inputLangOn = true;
    public void InputLangSwitch(View view) {
        ImageView button = (ImageView) view;
        int inputLangIcon;


        if (inputLangOn) {
            inputLangOn = false;
            inputLangIcon = R.drawable.input_lang_on;

        } else {
            inputLangOn = true;
            inputLangIcon = R.drawable.input_lang_off;
        }
        button.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), inputLangIcon));
    }

    // controls the ASL button
    boolean outputLangOn = true;
    public void OutputLangSwitch(View view) {
        ImageView button = (ImageView) view;
        int outputLangIcon;


        if (outputLangOn) {
            outputLangOn = false;
            outputLangIcon = R.drawable.output_lang_on;

        } else {
            outputLangOn = true;
            outputLangIcon = R.drawable.output_lang_off;
        }
        button.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), outputLangIcon));
    }


//    public void onRadioButtonClicked(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.radio_pirates:
//                if (checked)
//                    stringResultASL ="ON";
//                    break;
//            case R.id.radio_ninjas:
//                if (checked)
//                    stringResultASL ="OFF";
//                    break;
//        }
//    }

//    public void onRadioButtonClickedCC(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.radio_piratesCC:
//                if (checked)
//                    stringResultCC ="ON";
//                    break;
//            case R.id.radio_ninjasCC:
//                if (checked)
//                    stringResultCC ="OFF";
//                    break;
//        }
//    }

//    public void onLogoClicked(View view) {
//        Intent intent = new Intent(this, TranslatarActivity.class);
//        startActivity(intent);
//    }
}