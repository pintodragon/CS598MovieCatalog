package edu.sunyit.chryslj.camera;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class handles the callback when auto focus is called. In order to
 * continuously auto focus it uses a Handler to send a message back to itself
 * after a delay of <code>FOCUS_MS</code> milliseconds.
 * 
 * @author Justin Chrysler
 * 
 */
public class AutoFocusCallbackImpl implements AutoFocusCallback
{
    private static final String TAG = AutoFocusCallbackImpl.class
            .getSimpleName();
    // Auto focus every couple of seconds.
    private static final long FOCUS_MS = 2000L;

    private Handler focusHandler;
    private int focusMessage;

    /**
     * Set the handler to be used by this class.
     * 
     * @param focusHandler
     *            the handler to use for this class.
     * @param focusMessage
     *            the message id that represents this AutoFocusCallback.
     */
    public void setHandler(Handler focusHandler, int focusMessage)
    {
        this.focusHandler = focusHandler;
        this.focusMessage = focusMessage;
    }

    /**
     * When this callback is called we want to continue to focus by sending a
     * delayed message.
     */
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

    /**
     * Get the handler being used by this class.
     * 
     * @return the handler being used.
     */
    public Handler getFocusHandler()
    {
        return focusHandler;
    }
}
