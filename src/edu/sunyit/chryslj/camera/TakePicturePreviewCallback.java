package edu.sunyit.chryslj.camera;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;

public class TakePicturePreviewCallback implements PreviewCallback
{
    private Handler handler;

    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void onPreviewFrame(byte[] arg0, Camera arg1)
    {

    }
}
