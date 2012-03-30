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
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        previewCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
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
