package edu.sunyit.chryslj.camera;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.sunyit.chryslj.R;

public class TakePicturePreviewCallback implements PreviewCallback
{
    private static final String TAG = TakePicturePreviewCallback.class
            .getSimpleName();
    private Handler handler;

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

        Message message = Message.obtain(handler, R.id.preview_taken, width,
                height, data);
        handler.sendMessage(message);
    }
}
