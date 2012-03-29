package edu.sunyit.chryslj.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.R.id;
import edu.sunyit.chryslj.barcode.BarcodeProcessor;
import edu.sunyit.chryslj.barcode.UPCABarcode;

public class BarcodeCameraActivity extends Activity
{
    private final static String TAG = BarcodeCameraActivity.class.getName();

    private Camera deviceCamera = null;
    private Parameters cameraParameters = null;
    private BarcodePreview barcodePreview;

    // TODO pull these callbacks out to their own classes.
    private PictureCallback pictureCallback = new PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            Bitmap myBitmap = BitmapFactory.decodeByteArray(data, 0,
                    data.length);
            BarcodeProcessor bp = new BarcodeProcessor();
            Bitmap binImage = bp.generateBinaryImage(myBitmap);
            UPCABarcode upacAB = new UPCABarcode();
            upacAB.decodeImage(binImage);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_preview);

        deviceCamera = getCameraInstance();

        if (deviceCamera != null)
        {
            cameraParameters = deviceCamera.getParameters();

            cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            // TODO look into focus modes.
            cameraParameters.setFocusMode(Parameters.FOCUS_MODE_FIXED);
            cameraParameters.setPictureFormat(ImageFormat.JPEG);

            Button captureButton = (Button) findViewById(id.button_capture);
            Log.e(TAG, "Button: " + captureButton);
            captureButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deviceCamera.takePicture(null, null, pictureCallback);
                }
            });

            barcodePreview = new BarcodePreview(this, deviceCamera);
            FrameLayout preview = (FrameLayout) findViewById(id.barcode_preview);
            preview.addView(barcodePreview);
        }
    }

    /**
     * 
     * @return
     */
    public static Camera getCameraInstance()
    {
        Camera camera = null;

        try
        {
            camera = Camera.open();
        }
        catch (Exception exc)
        {
            Log.e(TAG, "Unable to get camera instance: " + exc.getMessage());
        }

        return camera;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        releaseCamera(); // release the camera immediately on pause event
    }

    private void releaseCamera()
    {
        if (deviceCamera != null)
        {
            deviceCamera.release(); // release the camera for other applications
            deviceCamera = null;
        }
    }
}
