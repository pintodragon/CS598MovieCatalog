package edu.sunyit.chryslj.camera;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
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
    private static final String TAG = BarcodeCameraActivity.class.getName();

    // List of desired picture sizes ranging from largest to smallest.
    private String[] desiredPictureSizes = { "1600x1200" };

    private Camera deviceCamera = null;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private OverlayView overlayView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.barcode_preview);

        deviceCamera = getCameraInstance();
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

        overlayView = (OverlayView) findViewById(R.id.surface_overlay);
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

    /**
     * By default the camera will use the already set default size for images.
     * If the value being used happens to be the largest that the camera can
     * support there is a possibility we will run out of memory later on. To
     * reduce the chance of this happening we need to determine a supported
     * picture size that is also less then or equal to the size of the current
     * display.
     * 
     * Using a Point rather than a Camera.Size. You can instantiate a Point with
     * out an active Camera. You can not instantiate a Size without referencing
     * a valid camera.
     * 
     * @param cameraParameters
     * @param point
     * @return
     */
    private Point getPictureSize(Parameters cameraParameters, Point screenSize)
    {
        Point pictureSizeToUse = null;
        List<Size> supprotedSizes = cameraParameters.getSupportedPictureSizes();

        // Lets use a really large number to start. This will always get set at
        // least once.
        for (Size currSize : supprotedSizes)
        {
            int currWidth = currSize.width;
            int currHeight = currSize.height;
            Log.d(TAG, "All SupportedSize: " + currWidth + "x" + currHeight);

            for (String desiredSize : desiredPictureSizes)
            {
                String dimensions[] = desiredSize.split("x");
                if (currWidth == Integer.parseInt(dimensions[0]) &&
                        currHeight == Integer.parseInt(dimensions[1]))
                {
                    pictureSizeToUse = new Point(currWidth, currHeight);
                    return pictureSizeToUse;
                }
            }
        }

        // This should never happen but in the event that we didn't find a size
        // use the size that the camera is already using.
        if (pictureSizeToUse == null)
        {
            pictureSizeToUse = new Point(
                    cameraParameters.getPreviewSize().width,
                    cameraParameters.getPreviewSize().height);
            Log.i(TAG, "Unable to find a valid supported picture size, using" +
                    " the cameras default: " + pictureSizeToUse);
        }

        Log.i(TAG, "PictureSize found: " + pictureSizeToUse.x + "x" +
                pictureSizeToUse.y);

        return pictureSizeToUse;
    }

    private void startCamera(SurfaceHolder holder, int width, int height)
    {
        try
        {
            deviceCamera.stopPreview();
        }
        catch (Exception e)
        {
            // Ignore: Preview wasn't started.
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
            overlayView.setCamera(deviceCamera);
            deviceCamera.startPreview();
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
        surfaceHolder.removeCallback(this);
        try
        {
            deviceCamera.stopPreview();
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
}
