package edu.sunyit.chryslj.camera;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.sunyit.chryslj.R;

public class CameraHandler extends Handler
{
    private static final String TAG = CameraHandler.class.getName();
    
    private BarcodePreview barcodePreview;
    
    public CameraHandler(BarcodePreview barcodePreview)
    {
        this.barcodePreview = barcodePreview;
    }
    
    @Override
    public void handleMessage(Message message)
    {
        Log.d(TAG, "Handler recieved: " + message.what);
        switch (message.what) {
          case R.id.auto_focus:
              Log.d(TAG, "Handler recieved auto_focus");
              barcodePreview.doAutoFocus();
              break;
        }
    }
}
