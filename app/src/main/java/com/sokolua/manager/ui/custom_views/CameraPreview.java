package com.sokolua.manager.ui.custom_views;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sokolua.manager.utils.App;

import java.io.IOException;

import static com.sokolua.manager.ui.activities.RootActivity.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;


    private void setupCamera(){
        if (mCamera != null ) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setPictureFormat(ImageFormat.JPEG);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
        }

        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCamera = App.getCameraInstance();
        if (mCamera != null) {

            setupCamera();

        }
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCamera = App.getCameraInstance();

        if (mCamera != null) {
            setupCamera();

        }
    }

//    public CameraPreview(Context context, Camera camera) {
//        super(context);
//        mCamera = camera;
//
//        if (mCamera != null) {
//            setupCamera();
//        }
//    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        mHolder = surfaceHolder;
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (mHolder == null || mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        mHolder = surfaceHolder;

        // start preview with new settings
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mHolder ==null || mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mHolder == null || mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

    }


    public boolean takePicture(Camera.PictureCallback takePictureCallBack) {
        if (mCamera != null  ){
            mCamera.startPreview();
            mCamera.takePicture(null, null, takePictureCallBack);
            return true;
        }
        return false;
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


}
