package edu.sunyit.chryslj.ui;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.camera.AutoFocusCallbackImpl;
import edu.sunyit.chryslj.camera.CameraHandler;
import edu.sunyit.chryslj.camera.OverlayView;
import edu.sunyit.chryslj.camera.TakePicturePreviewCallback;

/**
 * This Activity handles the camera preview functionality. This includes
 * initializing the camera, calling for auto focus, listening for when we should
 * take a preview image and closing the camera instance.
 * 
 * @author Justin Chrysler
 */
public class CameraPreviewActivity extends Activity implements
        SurfaceHolder.Callback
{
    private static final String TAG = CameraPreviewActivity.class
            .getSimpleName();

    private Camera deviceCamera = null;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private OverlayView overlayView;
    private CameraHandler cameraHandler;

    private boolean previewRunning = false;

    private AutoFocusCallbackImpl autoFocusCallbackImpl =
            new AutoFocusCallbackImpl();
    private TakePicturePreviewCallback takePicPreviewCallback;

    // *******************//
    // Activity overrides //
    // *******************//
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

        try
        {
            deviceCamera = Camera.open();
        }
        catch (Exception exc)
        {
            Log.e(TAG, "Unable to get camera instance: " + exc.getMessage());
        }

        cameraHandler = new CameraHandler(
                this);
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
        stopCamera(); // release the camera immediately on pause event
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        stopCamera();
        super.onDestroy();
    }

    // ********************************************************************//
    // SurfaceHolder.Callback overrides. We only need the surface changed. //
    // ********************************************************************//
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height)
    {
        startCamera(holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // Do nothing
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // Do nothing
    }

    // ******************************//
    // BarcodeCameraActivity methods //
    // ******************************//

    /**
     * Tell the camera handler to take the next preview frame and send it to the
     * BarcodeActivty to be decoded.
     * 
     * @param view
     *            the OverlayView that was clicked.
     */
    public void onTakePicClick(View view)
    {
        if (view.getId() == R.id.overlay_view)
        {
            cameraHandler.sendEmptyMessage(R.id.take_preview);
        }
    }

    /**
     * Tell the camera to take the next preview frame.
     */
    public void takeOneShotPreview()
    {
        takePicPreviewCallback = new TakePicturePreviewCallback();
        takePicPreviewCallback.setHandler(cameraHandler);
        deviceCamera.setOneShotPreviewCallback(takePicPreviewCallback);
    }

    /**
     * Send the image data to the BarcodeActivity
     * 
     * @param data
     *            the YCrCB data of the preview image acquired.
     */
    public void sendPictureToReader(int width, int height, byte[] data)
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.ycrcb_image_data), data);
        returnIntent.putExtra(getString(R.string.ycrcb_image_width), width);
        returnIntent.putExtra(getString(R.string.ycrcb_image_height), height);
        setResult(RESULT_OK, returnIntent);
        stopCamera();
        finish();
    }

    public void drawOverlay()
    {
        Log.d(TAG, "Draw the overlay");
        WindowManager manager =
                (WindowManager) getApplication().getSystemService(
                        Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        overlayView.setPreviewSize(new Point(
                width, height));
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

    /**
     * Initialize the camera properties including the screen size, flash mode
     * and auto focus mode. Currently using the torch mode for the flash to keep
     * a constant source of light on the bar code as we preview it.
     */
    private void initCameraProperties()
    {
        Parameters cameraParameters = deviceCamera.getParameters();

        WindowManager manager =
                (WindowManager) getApplication().getSystemService(
                        Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        cameraParameters.setPreviewSize(width, height);
        overlayView.setPreviewSize(new Point(
                width, height));

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
     * Stop the camera and release it back to the system. If this isn't done
     * then the camera could be out of commission for other applications until
     * the phone is reinitialized.
     */
    private void stopCamera()
    {
        autoFocusCallbackImpl.setHandler(null, 0);
        surfaceHolder.removeCallback(this);

        if (deviceCamera != null)
        {
            if (previewRunning)
            {
                deviceCamera.stopPreview();
                previewRunning = false;
            }

            deviceCamera.release();
        }
    }

    /**
     * Start the camera and start the preview using the given SurfaceHolder.
     * 
     * @param holder
     *            the SurfaceHolder to display the preview on.
     */
    private void startCamera(SurfaceHolder holder)
    {
        if (deviceCamera != null)
        {
            if (previewRunning)
            {
                deviceCamera.stopPreview();
                previewRunning = false;
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

            initCameraProperties();
            // overlayView.setCamera(deviceCamera);
            deviceCamera.startPreview();
            previewRunning = true;
            cameraHandler.sendEmptyMessage(R.id.preview_running);
            doAutoFocus();
        }
    }
}
