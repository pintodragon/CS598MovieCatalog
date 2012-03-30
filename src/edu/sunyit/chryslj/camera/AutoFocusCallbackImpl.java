package edu.sunyit.chryslj.camera;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AutoFocusCallbackImpl implements AutoFocusCallback
{
    private static final long FOCUS_MS = 1500L;

    private Handler focusHandler;
    private int message;

    public void setHandler(Handler focusHandler, int message)
    {
        this.focusHandler = focusHandler;
        this.message = message;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera)
    {
        if (focusHandler != null)
        {
            Message message = focusHandler.obtainMessage(this.message, success);
            focusHandler.sendMessageDelayed(message, FOCUS_MS);
            focusHandler = null;
        }
        Log.d("AutoFocus", "Autofocus called");
    }
}
