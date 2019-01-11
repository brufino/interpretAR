package com.microsoft.cognitiveservices.speech.samples.captions_translatar;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.text.TextUtils;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;






public class TranslatarActivity extends AppCompatActivity {

    //
    // Configuration for speech recognition
    //

    private static final String TAG = "TranslatarActivity";

    String stringResult = "hello";
    OverlayActivity arContent;
    FrameLayout arViewPane;

    // Replace below with your own subscription key
    private static String SpeechSubscriptionKey = "64d8f363b6eb4014aaf538c18d597b55";
    // Replace below with your own service region (e.g., "westus").
    private static String SpeechRegion = "westus";



    private Camera mCamera;

    //
    //Setting up microphone stream
    //

    private MicrophoneStream microphoneStream;
    private MicrophoneStream createMicrophoneStream() {
        if (microphoneStream != null) {
            microphoneStream.close();
            microphoneStream = null;
        }

        microphoneStream = new MicrophoneStream();
        return microphoneStream;
    }


    private TextView recognizedTextView;

    //    private Button recognizeButton;
    //    private Button recognizeIntermediateButton;
    private Button recognizeContinuousButton;
    //    private Button recognizeIntentButton;
    //
    // Configuration for intent recognition TODO: INTENT RECOGNITION FOR ASL
    //

    // Replace below with your own Language Understanding subscription key
    // The intent recognition service calls the required key 'endpoint key'.
    private static final String LanguageUnderstandingSubscriptionKey = "YourLanguageUnderstandingSubscriptionKey";
    // Replace below with the deployment region of your Language Understanding application
    private static final String LanguageUnderstandingServiceRegion = "YourLanguageUnderstandingServiceRegion";
    // Replace below with the application ID of your Language Understanding application
    private static final String LanguageUnderstandingAppId = "YourLanguageUnderstandingAppId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //creating camera preview on screen
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_translatar);

        arViewPane = (FrameLayout) findViewById(R.id.surface_camera);

        CameraPreview arDisplay = new CameraPreview(getApplicationContext(), this);
        arViewPane.addView(arDisplay);


        //translation part
        ///
        //
        //

        recognizedTextView = findViewById(R.id.recognizedText);

        //recognizeButton = findViewById(R.id.buttonRecognize);
        //recognizeIntermediateButton = findViewById(R.id.buttonRecognizeIntermediate);
        recognizeContinuousButton = findViewById(R.id.buttonRecognizeContinuous);
        //recognizeIntentButton = findViewById(R.id.buttonRecognizeIntent);

        // Initialize SpeechSDK and request required permissions.
        try {
            // a unique number within the application to allow
            // correlating permission request responses with the request.
            int permissionRequestId = 5;

            // Request permissions needed for speech recognition
            ActivityCompat.requestPermissions(TranslatarActivity.this, new String[]{RECORD_AUDIO, INTERNET}, permissionRequestId);
        }
        catch(Exception ex) {
            Log.e("SpeechSDK", "could not init sdk, " + ex.toString());
            recognizedTextView.setText("Could not initialize: " + ex.toString());
        }

        // create config
        final SpeechConfig speechConfig;
        try {
            speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            displayException(ex);
            return;
        }

        ///////////////////////////////////////////////////
        // recognize continuously
        ///////////////////////////////////////////////////
        recognizeContinuousButton.setOnClickListener(new View.OnClickListener() {
            private static final String logTag = "reco 3";
            private boolean continuousListeningStarted = false;
            private SpeechRecognizer reco = null;
            private AudioConfig audioInput = null;
            private String buttonText = "";
            private ArrayList<String> content = new ArrayList<>();


            @Override
            public void onClick(final View view) {
                final Button clickedButton = (Button) view;
                disableButtons();
                if (continuousListeningStarted) {
                    if (reco != null) {
                        final Future<Void> task = reco.stopContinuousRecognitionAsync();

                        setOnTaskCompletedListener(task, result -> {
                            Log.i(logTag, "Continuous recognition stopped.");
                            TranslatarActivity.this.runOnUiThread(() -> {
                                clickedButton.setText(buttonText);
                            });
                            enableButtons();
                            continuousListeningStarted = false;
                        });
                    } else {
                        continuousListeningStarted = false;
                    }
                    clearTextBox();
                    return;
                }

                clearTextBox();

                try {
                    content.clear();

                    // audioInput = AudioConfig.fromDefaultMicrophoneInput();
                    audioInput = AudioConfig.fromStreamInput(createMicrophoneStream());
                    reco = new SpeechRecognizer(speechConfig, audioInput);
                    reco.recognizing.addEventListener((o, speechRecognitionResultEventArgs) -> {
                        final String s = speechRecognitionResultEventArgs.getResult().getText();
                        Log.i(logTag, "Intermediate result received: " + s);
                        content.add(s);
                        setRecognizedText(s);
                        //setRecognizedText(TextUtils.join(" ", content));
                        content.remove(content.size() - 1);
                    });

                    reco.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                        final String s = speechRecognitionResultEventArgs.getResult().getText();
                        Log.i(logTag, "Final result received: " + s);
                        content.add(s);
                        //setRecognizedText(TextUtils.join(" ", content));
                        content.remove(content.size() - 1);
                    });

                    final Future<Void> task = reco.startContinuousRecognitionAsync();

                    setOnTaskCompletedListener(task, result -> {
                        continuousListeningStarted = true;
                        TranslatarActivity.this.runOnUiThread(() -> {
                            buttonText = clickedButton.getText().toString();
                            clickedButton.setText("Stop");
                            clickedButton.setEnabled(true);
                        });
                    });
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    displayException(ex);
                }
            }
        }
        );



    }



    private void displayException(Exception ex) {
        recognizedTextView.setText(ex.getMessage() + System.lineSeparator() + TextUtils.join(System.lineSeparator(), ex.getStackTrace()));
    }

    private void clearTextBox() {
        AppendTextLine("", true);
    }

    private void setRecognizedText(final String s) {
        AppendTextLine(s, true);
    }

    private void AppendTextLine(final String s, final Boolean erase) {
        TranslatarActivity.this.runOnUiThread(() -> {
            if (erase) {
                recognizedTextView.setText(s);
                stringResult=recognizedTextView.getText().toString();
                arContent = new OverlayActivity(getApplicationContext(),stringResult); //TEST
                arViewPane.addView(arContent); // TEST
            } else {
                String txt = recognizedTextView.getText().toString();
                recognizedTextView.setText(txt + System.lineSeparator() + s);
                stringResult=recognizedTextView.getText().toString();
                arContent = new OverlayActivity(getApplicationContext(),stringResult); //TEST
                arViewPane.addView(arContent); // TEST
            }
        });
    }

    private void disableButtons() {
        TranslatarActivity.this.runOnUiThread(() -> {
            //recognizeButton.setEnabled(false);
            //recognizeIntermediateButton.setEnabled(false);
            recognizeContinuousButton.setEnabled(false);
            //recognizeIntentButton.setEnabled(false);
        });
    }

    private void enableButtons() {
        TranslatarActivity.this.runOnUiThread(() -> {
            //recognizeButton.setEnabled(true);
            //recognizeIntermediateButton.setEnabled(true);
            recognizeContinuousButton.setEnabled(true);
            //recognizeIntentButton.setEnabled(true);
        });
    }

    private <T> void setOnTaskCompletedListener(Future<T> task, OnTaskCompletedListener<T> listener) {
        s_executorService.submit(() -> {
            T result = task.get();
            listener.onCompleted(result);
            return null;
        });
    }

    private interface OnTaskCompletedListener<T> {
        void onCompleted(T taskResult);
    }

    private static ExecutorService s_executorService;
    static {
        s_executorService = Executors.newCachedThreadPool();
    }

    // ALWAYS remember to release the camera when you are finished
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


}