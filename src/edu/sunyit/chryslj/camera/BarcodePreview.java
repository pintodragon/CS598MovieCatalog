package edu.sunyit.chryslj.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BarcodePreview extends SurfaceView implements
        SurfaceHolder.Callback
{
    private final static String TAG = BarcodePreview.class.getName();

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private boolean isPreviewRunning = false;

    private AutoFocusCallbackImpl autoFocusCallback;

    public BarcodePreview(Context context, Camera camera)
    {
        super(context);
        this.camera = camera;
        autoFocusCallback = new AutoFocusCallbackImpl();

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
            int width, int height)
    {
        // if (surfaceHolder.getSurface() == null)
        // {
        // // Surface does not exist
        // return;
        // }
        //
        // stopPreview();
        //
        // try
        // {
        // Parameters parameters = camera.getParameters();
        // Display display = ((WindowManager) getContext().getSystemService(
        // Context.WINDOW_SERVICE)).getDefaultDisplay();
        //
        // if (display.getRotation() == Surface.ROTATION_0)
        // {
        // parameters.setPreviewSize(height, width);
        // camera.setDisplayOrientation(90);
        // }
        //
        // if (display.getRotation() == Surface.ROTATION_90)
        // {
        // parameters.setPreviewSize(width, height);
        // }
        //
        // if (display.getRotation() == Surface.ROTATION_180)
        // {
        // parameters.setPreviewSize(height, width);
        // }
        //
        // if (display.getRotation() == Surface.ROTATION_270)
        // {
        // parameters.setPreviewSize(width, height);
        // camera.setDisplayOrientation(180);
        // }
        //
        // previewCamera();
        // }
        // catch (Exception exc)
        // {
        // Log.e(TAG, "Error starting the camera preview: " + exc.getMessage());
        // }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        previewCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        // This will be handled by the activity that uses this preview.
    }

    // TODO Use this method
    public void stopPreview()
    {
        if (camera != null && isPreviewRunning)
        {
            camera.stopPreview();
            autoFocusCallback.setHandler(null, 0);
            isPreviewRunning = false;
        }
    }

    public void previewCamera()
    {
        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            isPreviewRunning = true;
            camera.autoFocus(autoFocusCallback);
        }
        catch (IOException ioe)
        {
            Log.e(TAG, "Error setting the camera preview: " + ioe.getMessage());
        }
    }
}
