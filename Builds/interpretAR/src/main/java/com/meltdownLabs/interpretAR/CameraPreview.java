package com.meltdownLabs.interpretAR;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
    private static final boolean DEBUG = true;

    private final Context mContext;
    private final SurfaceHolder mHolder;
    Activity mActivity;

    private Camera mCamera;

    public CameraPreview(Context context, Activity activity) {
        super(context);
        mContext = context;
        mHolder = getHolder();
        mActivity = activity;
        // This value is supposedly deprecated and set "automatically" when
        // needed.
        // Without this, the application crashes.
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (DEBUG) Log.i(TAG, "surfaceCreated(SurfaceHolder");

        mCamera = Camera.open();

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(mHolder);
            //mCamera.startPreview(); // added in surfaceChanged instead
        } catch (IOException e) {
            Log.e(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (DEBUG) Log.i(TAG, "surfaceChanged(SurfaceHolder, int, int, int");

        //was commented out before
        if (mHolder.getSurface() == null) {
            // Preview surface does not exist.
            return;
        }

        // Stop preview before making changes.
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // Tried to stop a non-existent preview, so ignore.
        }

        Camera.Parameters params = mCamera.getParameters();

        // Find an appropriate preview size that fits the surface
        List<Size> prevSizes = params.getSupportedPreviewSizes();
        for (Size s : prevSizes) {
            if ((s.height <= height) && (s.width <= width)) {
                params.setPreviewSize(s.width, s.height);
                break;
            }
        }
        // TODO: don't hardcode cameraId '0' here... figure this out later.
        setCameraDisplayOrientation(mContext, 0, mCamera);

        // Start preview with new settings.
        try {
            //mCamera.setPreviewDisplay(mHolder); // added in surfaceCreated instead
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (DEBUG) Log.i(TAG, "surfaceDestroyed(SurfaceHolder");
        // Take care of releasing the Camera preview in the floating window.
        // Shut down camera preview
        mCamera.stopPreview(); //added (delete comment if works)
        mCamera.release(); //added (delete comment if works)
    }

    public static void setCameraDisplayOrientation(Context context, int cameraId, Camera camera) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            // Compensate for the mirror image.
            result = (360 - result) % 360;
        } else {
            // Back-facing camera.
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}



//the code below was used in the initial work on interpretAR.... refer to this for debug
/*//** A basic Camera preview class *//*
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
} */