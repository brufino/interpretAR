package com.example.capstone_project;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    public static final String ASL = "com.example.myfirstapp.MESSAGE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    /**
     * Called when the user taps the Send button

    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
     */

    public void accessCamera(View view) {
        //do something in response to go
        Intent intent = new Intent(this, TranslatarActivity.class);
        //intent.putExtra(ASL);
        startActivity(intent);
    }
}
