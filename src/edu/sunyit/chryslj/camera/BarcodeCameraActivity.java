package edu.sunyit.chryslj.camera;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import edu.sunyit.chryslj.R;

public class BarcodeCameraActivity extends Activity implements
        SurfaceHolder.Callback
{
    private static final String TAG = BarcodeCameraActivity.class
            .getSimpleName();

    private Camera deviceCamera = null;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private OverlayView overlayView;
    private CameraHandler cameraHandler;

    private boolean previewRunning = false;

    private AutoFocusCallbackImpl autoFocusCallbackImpl = new AutoFocusCallbackImpl();
    private TakePicturePreviewCallback takePicPreviewCallback;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.barcode_preview);

        overlayView = (OverlayView) findViewById(R.id.overlay_view);

        deviceCamera = getCameraInstance();
        cameraHandler = new CameraHandler(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        surfaceView = (SurfaceView) findViewById(R.id.preview_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setSizeFromLayout();
        surfaceHolder.addCallback(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stopCamera(); // release the camera immediately on pause event
    }

    private void initCameraProperties()
    {
        Parameters cameraParameters = deviceCamera.getParameters();

        WindowManager manager = (WindowManager) getApplication()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        cameraParameters.setPreviewSize(width, height);
        overlayView.setPreviewSize(new Point(width, height));

        if (cameraParameters.getSupportedFlashModes().contains(
                Parameters.FLASH_MODE_TORCH))
        {
            cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        }

        if (cameraParameters.getSupportedFocusModes().contains(
                Parameters.FOCUS_MODE_AUTO))
        {
            cameraParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        }

        deviceCamera.setParameters(cameraParameters);
    }

    /**
     * 
     * @return
     */
    public static Camera getCameraInstance()
    {
        Camera camera = null;

        try
        {
            camera = Camera.open();
        }
        catch (Exception exc)
        {
            Log.e(TAG, "Unable to get camera instance: " + exc.getMessage());
        }

        return camera;
    }

    private void startCamera(SurfaceHolder holder, int width, int height)
    {
        if (previewRunning)
        {
            try
            {
                deviceCamera.stopPreview();
                previewRunning = false;
            }
            catch (Exception e)
            {
                // Ignore: Preview wasn't started.
            }
        }

        try
        {
            deviceCamera.setPreviewDisplay(holder);
            Log.d(TAG, "starting cam");
        }
        catch (IOException e1)
        {
            Log.d(TAG, "Unable to set display: " + e1.getMessage());
        }

        try
        {
            initCameraProperties();
            // overlayView.setCamera(deviceCamera);
            deviceCamera.startPreview();
            previewRunning = true;
            cameraHandler.sendEmptyMessage(R.id.preview_running);
            doAutoFocus();
        }
        catch (Exception e)
        {
            Log.d(TAG, "Exception: " + (deviceCamera != null) +
                    " exception val: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy()
    {
        stopCamera();
        super.onDestroy();
    }

    private void stopCamera()
    {
        autoFocusCallbackImpl.setHandler(null, 0);
        surfaceHolder.removeCallback(this);
        try
        {
            if (previewRunning)
            {
                deviceCamera.stopPreview();
                previewRunning = false;
            }

            deviceCamera.setPreviewCallback(null);
        }
        catch (Exception e)
        {
            // Ignore.
        }
        deviceCamera.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height)
    {
        startCamera(holder, width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub

    }

    public void drawOverlay()
    {
        Log.d(TAG, "Draw the overlay");
        WindowManager manager = (WindowManager) getApplication()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        overlayView.setPreviewSize(new Point(width, height));
        overlayView.invalidate();
    }

    public void doAutoFocus()
    {
        if (deviceCamera != null && previewRunning)
        {
            autoFocusCallbackImpl.setHandler(cameraHandler, R.id.auto_focus);
            deviceCamera.autoFocus(autoFocusCallbackImpl);
        }
    }
}
