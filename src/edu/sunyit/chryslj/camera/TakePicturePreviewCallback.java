package edu.sunyit.chryslj.camera;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.sunyit.chryslj.R;

/**
 * Instead of taking a picture we will use a preview frame. When testing the
 * results from taking a picture were almost always blurry. The preview frame is
 * what we see on the screen and is a much sharper image.
 * 
 * @author Justin Chrysler
 * 
 */
public class TakePicturePreviewCallback implements PreviewCallback
{
    private static final String TAG = TakePicturePreviewCallback.class
            .getSimpleName();
    private Handler handler;

    /**
     * Set the handler used to take the picture data and send it to the decoding
     * activity.
     * 
     * @param handler
     *            the camera handler.
     */
    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }

    /**
     * The byte array data is in the format of YCrCB (YUV).
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        Camera.Parameters parameters = camera.getParameters();
        Log.d(TAG, "Getting the preview frame in YCrCB format");
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        Message message =
                Message.obtain(handler, R.id.preview_taken, width, height, data);
        handler.sendMessage(message);
    }
}
