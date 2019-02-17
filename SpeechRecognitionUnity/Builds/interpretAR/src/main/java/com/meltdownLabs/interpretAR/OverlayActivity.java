package com.meltdownLabs.interpretAR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

import com.meltdownLabs.interpretAR.R;

import java.util.Hashtable;

//use this for mapping ???
public class OverlayActivity extends View {
    private static final String TAG = "OverlayActivity";
    public static final String DEBUG_TAG = "OverlayActivity Log";

    String stringResult = "Accelerometer Data";
    private Drawable CustomImage;


    public OverlayActivity(Context context, String arg) {
        super(context);


        // this is where we will hardcode our word to our ASL ID
        // the ASL ID will probably be a string of the drawable resource ID
        String[] arrayOfString = { "today;ASL00", "hello;ASL01", "bye;ASL03" };

        Hashtable<String, String> hashTable = new Hashtable<String, String>();
        for(String s: arrayOfString){
            String[] array = s.split(";");
            String sKey ="", sValue="";
            if(array.length > 1){
                sKey = array[0]; sValue = array[1];
                hashTable.put(sKey, sValue);
            }
        }
        stringResult=arg;

        // check if key exists
        if( hashTable.containsKey(arg)){
            //System.out.print("two = " + hashTable.get("two"));
            stringResult=hashTable.get(arg);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(DEBUG_TAG, "onDraw");
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //if key was found draw this image
        if(stringResult.equals("ASL01")) {
            // Draw something fixed (for now) over the camera view
            Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);
            contentPaint.setColor(Color.RED);
            int height = canvas.getHeight() / 2;
            int width = canvas.getWidth() / 2;
            canvas.drawBitmap(b, width, height, contentPaint);

        }
        else if (stringResult.equals("Accelerometer Data")){
            // Draw something fixed (for now) over the camera view
            Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.help);
            contentPaint.setColor(Color.RED);
            int height = canvas.getHeight() / 2;
            int width = canvas.getWidth() / 2;
            canvas.drawBitmap(b, width, height, contentPaint);
        }
        // Draw something fixed (for now) over the camera view
        Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        contentPaint.setTextAlign(Align.CENTER);
        contentPaint.setTextSize(20);
        contentPaint.setColor(Color.RED);
        canvas.drawText(stringResult, canvas.getWidth()/2, (canvas.getHeight()*3)/4, contentPaint);


    }

}

