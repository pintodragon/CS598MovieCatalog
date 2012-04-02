package edu.sunyit.chryslj.camera;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AutoFocusCallbackImpl implements AutoFocusCallback
{
    private static final String TAG = AutoFocusCallbackImpl.class
            .getSimpleName();
    private static final long FOCUS_MS = 1500L;

    private Handler focusHandler;
    private int focusMessage;

    public void setHandler(Handler focusHandler, int focusMessage)
    {
        this.focusHandler = focusHandler;
        this.focusMessage = focusMessage;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera)
    {
        if (focusHandler != null)
        {
            Message message = focusHandler.obtainMessage(focusMessage, success);
            focusHandler.sendMessageDelayed(message, FOCUS_MS);
            focusHandler = null;
        }
        Log.d(TAG, "Autofocus called");
    }

    public Handler getFocusHandler()
    {
        return focusHandler;
    }
}
