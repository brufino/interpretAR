package com.meltdownlabs.interpretAR;

import android.content.Intent;
//import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.meltdownlabs.interpretAR.R;

//TODO: Figure how out to add larger 18.5 by 9 aspect ratio support for newer phones
public class MainActivity extends AppCompatActivity {


    //INITIALIZING  BUTTONS
    boolean ASLisOn = false;
    boolean CCisOn = true;
    int CCicon;
    int ASLicon;
    int inputLangIcon;
    int outputLangIcon;

    int langINcounter;
    int langOUTcounter;

    public TextView recognizedTextView;

    private ImageView langINicon;
    private ImageView langOUTicon;
    private ImageView featureCCicon;
    private ImageView featureASLicon;

    private String inputLanguage="";
    private String outputLanguage="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //changing project title
        setTitle("interpretAR");

        //initialize for error statement if no symbols are clicked
        recognizedTextView = findViewById(R.id.statement);


        langINicon=  findViewById(R.id.langINicon);
        langOUTicon=  findViewById(R.id.langOUTicon);
        featureCCicon= findViewById(R.id.featureCCicon);
        featureASLicon= findViewById(R.id.featureASLicon);

        //initialize ASL on
        featureASLicon.setImageResource(R.drawable.asl_on);
    }

    public void accessCamera(View view) {
        //do something in response to go
        if(!CCisOn){
            Intent intent = new Intent(this, TranslatarActivity.class);
            intent.putExtra("inputlang", inputLanguage); // pass input speech
            intent.putExtra("outputlang", outputLanguage); // pass output speech
            startActivity(intent);
        }
        else if(!ASLisOn){
            Intent intent = new Intent(this, UnityPlayerActivity.class);
            startActivity(intent);}
        }

    public void accessAboutUs(View view) {
        //do something in response to go
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    // controls the CC button
    public void CCbutonClicked(View view) {
        if (CCisOn) { // sets CC on and lang options on. lang starts with english and can toggle to different languages
            CCisOn = false;
            CCicon = R.drawable.cc_on;
            inputLangIcon = R.drawable.input_lang_on;
            outputLangIcon = R.drawable.output_lang_on;
            ASLicon= R.drawable.asl_off;
            ASLisOn= true;
            langINcounter=1;
            langOUTcounter=1;
            inputLanguage="en-US";
            outputLanguage="en-US";
        } else { //sets CC off, and language off (with english as default off logo)
            CCisOn = true;
            CCicon = R.drawable.cc_off;
            outputLangIcon = R.drawable.output_lang_off;
            inputLangIcon = R.drawable.input_lang_off;
            ASLicon= R.drawable.asl_on;
            ASLisOn= false;
        }
        featureCCicon.setImageResource(CCicon);
        langINicon.setImageResource(inputLangIcon);
        langOUTicon.setImageResource(outputLangIcon);
        featureASLicon.setImageResource(ASLicon);
    }

    // controls the ASL button
    public void ASLbuttonClicked(View view) {
        if (ASLisOn) {
            ASLisOn = false;
            ASLicon = R.drawable.asl_on;
            CCicon = R.drawable.cc_off;
            inputLangIcon = R.drawable.input_lang_off;
            outputLangIcon = R.drawable.output_lang_off;
            CCisOn= true;
        } else { //if off CC goes on
            ASLisOn = true;
            ASLicon = R.drawable.asl_off;
            CCicon = R.drawable.cc_on;
            inputLangIcon = R.drawable.input_lang_on;
            outputLangIcon = R.drawable.output_lang_on;
            langINcounter=1;
            langOUTcounter=1;
            inputLanguage="en-US";
            outputLanguage="en-US";
            CCisOn=false;
        }
        featureASLicon.setImageResource(ASLicon);
        featureCCicon.setImageResource(CCicon);
        langINicon.setImageResource(inputLangIcon);
        langOUTicon.setImageResource(outputLangIcon);
    }

    // controls the input language button
    //for all possible languages please refer too https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support
    //TODO: CREATE DRAWABLE ICONS FOR LANGUAGES BELOW
    public void InputLangSwitch(View view) {
        if (ASLisOn) {
            if (langINcounter == 1) {
                inputLangIcon = R.drawable.input_lang_off; //make this french icon
                inputLanguage="fr-CA"; //CANADIAN FRENCH
                langINcounter++;
            } else if (langINcounter == 2) {
                inputLangIcon = R.drawable.input_lang_on; //make this spanish icon
                inputLanguage="es-ES"; //SPANISH
                langINcounter++;
            } else if (langINcounter == 3) {
                inputLangIcon = R.drawable.input_lang_off; //make this german icon
                inputLanguage="de-DE"; //GERMAN
                langINcounter++;
            } else if (langINcounter == 4) {
                inputLangIcon = R.drawable.input_lang_on; //make this hindi icon
                inputLanguage = "hi-IN"; //HINDI
                langINcounter++;
            }else if (langINcounter == 5) {
                inputLangIcon = R.drawable.input_lang_off; //make this japanese icon
                inputLanguage = "ja-JP"; //JAPANESE
                langINcounter++;
            }
            else if (langINcounter == 6) {
                inputLangIcon = R.drawable.input_lang_on; //make this korean icon
                inputLanguage = "ko-KR"; //KOREAN
                langINcounter++;
            }
            else if (langINcounter == 7) {
                inputLangIcon = R.drawable.input_lang_on; //make this portugese icon
                inputLanguage = "pt-PT"; //PORTUGESE
                langINcounter++;
            }
            else if (langINcounter == 8) {
                inputLangIcon = R.drawable.input_lang_off; //make this chinese icon
                inputLanguage = "zh-CN"; //Chinese (MANDARIN simplified)
                langINcounter++;
            }else {
                inputLangIcon = R.drawable.input_lang_on; //good
                inputLanguage="en-US"; //ENGLISH
                langINcounter = 1; //toggles back to french
            }
            langINicon.setImageResource(inputLangIcon);
        }
    }

    //controls output language selection
    //for all possible languages please refer too https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support
    //TODO: CREATE DRAWABLE ICONS FOR LANGUAGES BELOW (x2)
    public void OutputLangSwitch(View view) {
        if (ASLisOn) {
            if (langOUTcounter == 1) {
                outputLangIcon = R.drawable.output_lang_off; //make this french icon
                outputLanguage="fr-CA"; //CANADIAN FRENCH
                langOUTcounter++;
            } else if (langOUTcounter == 2) {
                outputLangIcon = R.drawable.output_lang_on; //make this spanish icon
                outputLanguage="es-ES"; //SPANISH
                langOUTcounter++;
            } else if (langOUTcounter == 3) {
                outputLangIcon = R.drawable.output_lang_off; //make this German icon
                outputLanguage="de-DE"; //GERMAN
                langOUTcounter++;
            } else if (langOUTcounter == 4) {
                outputLangIcon = R.drawable.output_lang_off; //make this hindi icon
                outputLanguage="hi-IN"; //HINDI
                langOUTcounter++;
            } else if (langOUTcounter == 5) {
                outputLangIcon = R.drawable.output_lang_on; //make this japanese icon
                outputLanguage="ja-JP"; //JAPANESE
                langOUTcounter++;
            } else if (langOUTcounter == 6) {
                outputLangIcon = R.drawable.output_lang_off; //make this french icon
                outputLanguage="ko-KR"; //KOREAN
                langOUTcounter++;
            } else if (langOUTcounter == 7) {
                outputLangIcon = R.drawable.output_lang_on; //make this spanish icon
                outputLanguage="pt-PT"; //PORTUGESE
                langOUTcounter++;
            } else if (langOUTcounter == 8) {
                outputLangIcon = R.drawable.output_lang_off; //make this french icon
                outputLanguage="zh-CN"; //Chinese (MANDARIN simplified)
                langOUTcounter++;
            } else {
                outputLangIcon = R.drawable.output_lang_on; //good
                outputLanguage="en-US"; //ENGLISH
                langOUTcounter = 1; //toggles back to french
            }
            langOUTicon.setImageResource(outputLangIcon);
        }
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