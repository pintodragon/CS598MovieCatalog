package edu.sunyit.chryslj.camera;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.R.id;

public class BarcodeCameraActivity extends Activity
{
    private static final String TAG = BarcodeCameraActivity.class.getName();

    private static final int MIN_NUM_PIXELS = 320 * 240;
    private static final int MAX_NUM_PIXELS = 854 * 480;

    private Camera deviceCamera = null;
    private BarcodePreview barcodePreview;

    // TODO pull these callbacks out to their own classes.
    private PictureCallback pictureCallback = new PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            Log.d(TAG, "Picture taken");
            Intent returnIntent = new Intent();
            returnIntent.putExtra("image", data);
            setResult(RESULT_OK, returnIntent);
            barcodePreview.stopPreview();
            finish();
        }

    };

    private PreviewCallback previewCallback = new PreviewCallback()
    {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.barcode_preview);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        deviceCamera = getCameraInstance();

        if (deviceCamera != null)
        {
            initCameraProperties();

            Button captureButton = (Button) findViewById(id.button_capture);
            captureButton.setEnabled(true);
            captureButton.setOnClickListener(new View.OnClickListener()
            {
                boolean pressed = false;

                // TODO Having an issue with pictures coming our dark. Might
                // have to use previewcall back instead.
                @Override
                public void onClick(View view)
                {
                    if (!pressed)
                    {
                        deviceCamera.takePicture(null, null, pictureCallback);
                        pressed = true;
                    }
                }
            });

            barcodePreview = new BarcodePreview(this, deviceCamera);
            FrameLayout preview = (FrameLayout) findViewById(id.barcode_preview);
            preview.addView(barcodePreview);
        }

        Intent intent = getIntent();

        if (intent != null)
        {

        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        releaseCamera(); // release the camera immediately on pause event
    }

    private void initCameraProperties()
    {
        Parameters cameraParameters = deviceCamera.getParameters();

        WindowManager manager = (WindowManager) getApplication()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        Point pictureSize = getPictureSize(cameraParameters, new Point(width,
                height));
        cameraParameters.setPictureSize(pictureSize.x, pictureSize.y);

        if (cameraParameters.getSupportedFlashModes().contains(
                Parameters.FLASH_MODE_TORCH))
        {
            cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            Log.d(TAG, "Flash set to Torch");
        }

        if (cameraParameters.getSupportedFocusModes().contains(
                Parameters.FOCUS_MODE_AUTO))
        {
            cameraParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
            Log.d(TAG, "Focus set to auto");
        }

        cameraParameters.setJpegQuality(100);

        deviceCamera.setParameters(cameraParameters);
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

    /**
     * By default the camera will use the already set default size for images.
     * If the value being used happens to be the largest that the camera can
     * support there is a possibility we will run out of memory later on. To
     * reduce the chance of this happening we need to determine a supported
     * picture size that is also less then or equal to the size of the current
     * display.
     * 
     * Using a Point rather than a Camera.Size. You can instantiate a Point with
     * out an active Camera. You can not instantiate a Size without referencing
     * a valid camera.
     * 
     * @param cameraParameters
     * @param point
     * @return
     */
    private Point getPictureSize(Parameters cameraParameters, Point screenSize)
    {
        Point pictureSizeToUse = null;
        List<Size> supprotedSizes = cameraParameters.getSupportedPictureSizes();

        // Lets use a really large number to start. This will always get set at
        // least once.
        int sizeDifferences = Integer.MAX_VALUE;
        for (Size currSize : supprotedSizes)
        {
            int currWidth = currSize.width;
            int currHeight = currSize.height;

            if ((currWidth * currHeight) < MIN_NUM_PIXELS ||
                    (currWidth * currHeight) > MAX_NUM_PIXELS)
            {
                // Either the size is too small or too large. Not a candidate.
                continue;
            }
            Log.d(TAG, "SupportedSize: " + currWidth + "x" + currHeight);

            // Check if the size is a portrait.
            if (currWidth < currHeight)
            {
                int temp = currWidth;
                currWidth = currHeight;
                currHeight = temp;
            }

            // If we have our exact screen size then return it and search no
            // more.
            if (currWidth == screenSize.x && currHeight == screenSize.y)
            {
                return screenSize;
            }

            // We know the supported size is within our valid ranges and that
            // it is not the same as the screen size. Lets determine if it is
            // the closest to our current screen size.
            int currSizeDifference = Math.abs(screenSize.x * currWidth -
                    screenSize.y * currHeight);
            if (currSizeDifference < sizeDifferences)
            {
                pictureSizeToUse = new Point(currWidth, currHeight);
                sizeDifferences = currSizeDifference;
            }
        }

        // This should never happen but in the event that we didn't find a size
        // use the size that the camera is already using.
        if (pictureSizeToUse == null)
        {
            pictureSizeToUse = new Point(
                    cameraParameters.getPreviewSize().width,
                    cameraParameters.getPreviewSize().height);
            Log.i(TAG, "Unable to find a valid supported picture size, using" +
                    " the cameras default: " + pictureSizeToUse);
        }

        return pictureSizeToUse;
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
