package edu.sunyit.chryslj.camera;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.sunyit.chryslj.R;

/**
 * This handler will be used to run all camera related things such as calling
 * the auto focus callback, displaying the overlay, etc.
 * 
 * @author Justin Chrysler
 * 
 */
public class CameraHandler extends Handler
{
    private static final String TAG = CameraHandler.class.getSimpleName();

    private enum State
    {
        START,
        GETIMAGE,
        DONE
    };

    private BarcodeCameraActivity barcodeCameraActivity;
    private State currentState;

    public CameraHandler(BarcodeCameraActivity barcodeCameraActivity)
    {
        this.barcodeCameraActivity = barcodeCameraActivity;
        currentState = State.START;
    }

    @Override
    public void handleMessage(Message message)
    {
        Log.d(TAG, "Handler recieved: " + message.what);
        switch (message.what)
        {
            case R.id.auto_focus:
                Log.d(TAG, "Handler recieved auto_focus");
                // Only call for auto focus if we are still looking for an
                // image.
                if (currentState == State.GETIMAGE)
                {
                    barcodeCameraActivity.doAutoFocus();
                }
                break;
            case R.id.preview_running:
                Log.d(TAG, "Handler recieved preview_running");
                // Draw the guide lines
                previewRunning();
                break;
            case R.id.take_preview:
                // Call the one shot preview callback.
                Log.d(TAG, "Handler recieved take_preview");
                currentState = State.DONE;
                barcodeCameraActivity.takeOneShotPreview();
                break;
            case R.id.preview_taken:
                Log.d(TAG, "Handler recieved take_preview");
                byte[] data = (byte[]) message.obj;
                // TODO Might be incorrectly returning null.
                barcodeCameraActivity.sendPictureToReader(data);
                break;
        }
    }

    private void previewRunning()
    {
        Log.d(TAG, "Preview Running was run!");
        if (currentState == State.START)
        {
            Log.d(TAG, "Draw overlay");
            barcodeCameraActivity.drawOverlay();
            currentState = State.GETIMAGE;
        }
    }
}
