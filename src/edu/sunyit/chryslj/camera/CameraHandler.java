package edu.sunyit.chryslj.camera;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.ui.CameraPreviewActivity;

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

    private CameraPreviewActivity cameraPreviewActivity;
    private State currentState;

    public CameraHandler(CameraPreviewActivity cameraPreviewActivity)
    {
        this.cameraPreviewActivity = cameraPreviewActivity;
        currentState = State.START;
    }

    /**
     * Handler the messages being sent around for the camera. This includes
     * starting the preview, calling auto focus and taking the picture.
     */
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
                    cameraPreviewActivity.doAutoFocus();
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
                cameraPreviewActivity.takeOneShotPreview();
                break;
            case R.id.preview_taken:
                Log.d(TAG, "Handler recieved take_preview");
                // The data being sent back is in YCrCB format.
                byte[] data = (byte[]) message.obj;

                // arg1 and arg2 were set in the picture preview call back as
                // the width and height of the preview screen.
                int width = message.arg1;
                int height = message.arg2;
                cameraPreviewActivity.sendPictureToReader(width, height, data);
                break;
        }
    }

    /**
     * If the current state is START then draw the overlay and start the
     * preview.
     */
    private void previewRunning()
    {
        if (currentState == State.START)
        {
            Log.d(TAG, "Draw overlay");
            cameraPreviewActivity.drawOverlay();
            currentState = State.GETIMAGE;
        }
    }
}
