package edu.sunyit.chryslj.camera;

import java.io.IOException;

import edu.sunyit.chryslj.R;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
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
    private Handler handler;

    private AutoFocusCallbackImpl autoFocusCallback;

    public BarcodePreview(Context context, Camera camera)
    {
        super(context);
        this.camera = camera;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        autoFocusCallback = new AutoFocusCallbackImpl();
        autoFocusCallback.setHandler(null, 0);
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
            int width, int height)
    {
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        previewCamera();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
    }

    // TODO Use this method
    public void stopPreview()
    {
        if (camera != null && isPreviewRunning)
        {
            isPreviewRunning = false;
            autoFocusCallback.setHandler(null, 0);
            camera.stopPreview();
        }
    }

    public void previewCamera()
    {
        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            isPreviewRunning = true;
            doAutoFocus();
        }
        catch (IOException ioe)
        {
            Log.e(TAG, "Error setting the camera preview: " + ioe.getMessage());
        }
    }
    
    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }
    
    public void doAutoFocus()
    {
        if (isPreviewRunning)
        {
            try
            {
                autoFocusCallback.setHandler(handler, R.id.auto_focus);
                camera.autoFocus(autoFocusCallback);
            }
            catch(Exception exp)
            {
                // For now do nothing.
                Log.d(TAG, "Exception during auto focus call. Ignoring.");
            }
        }
    }
}
