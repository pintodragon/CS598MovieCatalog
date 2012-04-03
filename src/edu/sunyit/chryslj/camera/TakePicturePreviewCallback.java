package edu.sunyit.chryslj.camera;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.Message;
import edu.sunyit.chryslj.R;

public class TakePicturePreviewCallback implements PreviewCallback
{
    private Handler handler;

    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        Message message = Message.obtain(handler, R.id.preview_taken, data);
        handler.sendMessage(message);
    }
}
