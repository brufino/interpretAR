package com.meltdownlabs.interpretAR;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.unity3d.player.*;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

public class UnityPlayerActivity extends Activity
{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code


    FrameLayout arViewPane;


    // Setup activity layout
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mUnityPlayer = new UnityPlayer(this);
        //setContentView(mUnityPlayer);
        //mUnityPlayer.requestFocus();
        //UnityPlayer mUnityPlayer = new UnityPlayer(this);

/*        setContentView(R.layout.activity_unity_player);
        arViewPane = (FrameLayout)findViewById(R.id.unity_player_layout);
        arViewPane.addView(mUnityPlayer.getView());
        mUnityPlayer.requestFocus();*/

        mUnityPlayer = new UnityPlayer(this);
        if (mUnityPlayer.getSettings ().getBoolean ("hide_status_bar", true))
        {
            setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
            getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        //setContentView(mUnityPlayer);
        //Set the content to main
        setContentView(R.layout.activity_unity_player);

        //Inflate the frame layout from XML
        this.arViewPane = (FrameLayout)findViewById(R.id.unity_player_layout);

        //Add the mUnityPlayer view to the FrameLayout, and set it to fill all the area available
        this.arViewPane.addView(mUnityPlayer.getView(),
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        mUnityPlayer.destroy();
        super.onDestroy();
        System.out.println("onDestroy");
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
        System.out.println("onPause");
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
        System.out.println("onResume");

    }

    @Override protected void onStart()
    {
        super.onStart();
        mUnityPlayer.start();
        System.out.println("onStart");
    }

    @Override protected void onStop()
    {
        super.onStop();
        mUnityPlayer.stop();
        System.out.println("onStop");
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }

}

/* //microsoft paste this in onstart if want microsoft captions in ASL portion
    setRecognizedText("This is working");
//translation part
// Initialize SpeechSDK and request required permissions.
        try {
                // a unique number within the application to allow
                // correlating permission request responses with the request.
                int permissionRequestId = 5;

                // Request permissions needed for speech recognition
                ActivityCompat.requestPermissions(UnityPlayerActivity.this, new String[]{RECORD_AUDIO, INTERNET}, permissionRequestId);
                } catch (Exception ex) {
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


        UnityPlayerActivity.this.runOnUiThread(() -> {
        System.out.println("I am translating main code");

        AudioConfig audioInput = null;
        ArrayList<String> content = new ArrayList<>();
        clearTextBox();
        try {
        content.clear();
        // audioInput = AudioConfig.fromDefaultMicrophoneInput();
        audioInput = AudioConfig.fromStreamInput(createMicrophoneStream());
        reco = new SpeechRecognizer(speechConfig, audioInput);
        Log.i(logTag, "using mic");
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
        } catch (Exception ex) {
        System.out.println(ex.getMessage());
        displayException(ex);
        }
        });*/

/*    //Defining methods used:
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
        UnityPlayerActivity.this.runOnUiThread(() -> {
            if (erase) {
                if(messageCC.equals("ON")){//captions
                    if(messageASL.equals("ON")){//captions+asl
                        recognizedTextView.setText(s);
                        stringResult = recognizedTextView.getText().toString();
                        //arContent = new OverlayActivity(getApplicationContext(), stringResult); //TEST
                        //arViewPane.addView(arContent); // TEST
                    }
                    else{//cations, NO ASL
                        recognizedTextView.setText(s);
                    }
                }
                else{//no captions
                    if(messageASL.equals("ON")){//NO captions, ASL
                        recognizedTextView.setText("");
                        stringResult = s.toString();
                        //arContent = new OverlayActivity(getApplicationContext(), stringResult); //TEST
                        //arViewPane.addView(arContent); // TEST
                    }
                    else{//no captions, no ASL
                        recognizedTextView.setText("you chose no captions and no ASL, therefore only camera preview displaying");
                    }
                }
            } else  {
                if(messageCC.equals("ON")){//captions
                    if(messageASL.equals("ON")){//captions+asl
                        recognizedTextView.setText(s);
                        stringResult = recognizedTextView.getText().toString();
                        //arContent = new OverlayActivity(getApplicationContext(), stringResult); //TEST
                        //arViewPane.addView(arContent); // TEST
                    }
                    else{//cations, NO ASL
                        recognizedTextView.setText(s);
                    }
                }
                else{//no captions
                    if(messageASL.equals("ON")){//NO captions, ASL
                        recognizedTextView.setText("");
                        stringResult = s.toString();
                        //arContent = new OverlayActivity(getApplicationContext(), stringResult); //TEST
                        //arViewPane.addView(arContent); // TEST
                    }
                    else{//no captions, no ASL
                        recognizedTextView.setText("you chose no captions and no ASL, therefore only camera preview displaying");
                    }
                }
            }
        });
    }

    private <T> void setOnTaskCompletedListener(Future<T> task, UnityPlayerActivity.OnTaskCompletedListener<T> listener) {
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

private void releaseAudio() {
        if (microphoneStream != null) {
        microphoneStream.close();
        microphoneStream = null;
        }
        }

private void releaseTranslation(){
        if (continuousListeningStarted) {
        if (reco != null) {
final Future<Void> task = reco.stopContinuousRecognitionAsync();
        setOnTaskCompletedListener(task, result -> {
        Log.i("reco3", "Continuous recognition stopped.");
        continuousListeningStarted = false;
        });
        }
        else {
        continuousListeningStarted = false;
        }
        clearTextBox();
        return;
        }

        clearTextBox();
        }*/

/*
onCreate class:
        Bundle extras = getIntent().getExtras();
        //messageCC = extras.getString("radioChosenCC");
        //debug
        messageCC = "ON";
        messageASL = extras.getString("radioChosenASL");
        recognizedTextView = findViewById(R.id.recognizedText);*/

/*
global parameters:
private boolean continuousListeningStarted = false;
private SpeechRecognizer reco = null;
private static final String logTag = "reco 3";
        String messageASL= "";
        String messageCC = "";


// Replace below with your own subscription key
private static String SpeechSubscriptionKey = "6481c204d4d34b449aae4b63011abc76";
// Replace below with your own service region (e.g., "westus").
private static String SpeechRegion = "westus";


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
*/
