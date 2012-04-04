package edu.sunyit.chryslj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import edu.sunyit.chryslj.barcode.BarcodeProcessor;
import edu.sunyit.chryslj.barcode.UPCABarcode;

public class TestBMPActivity extends Activity
{
    private static final String TAG = TestBMPActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_test);

        // File imgFile = new File("/mnt/sdcard/picture.jpg");
        //
        // if (imgFile.exists())
        // {
        // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
        // .getAbsolutePath());
        //
        // BarcodeProcessor bp = new BarcodeProcessor();
        //
        // Bitmap binImage = bp.generateBinaryImage(myBitmap);
        // ImageView myImage = (ImageView) findViewById(R.id.binary);
        // myImage.setImageBitmap(binImage);
        // UPCABarcode upacAB = new UPCABarcode();
        // upacAB.decodeImage(binImage);
        // myImage.setVisibility(ImageView.VISIBLE);
        // // Log.i(TAG, "Image exists");
        // }
        // else
        // {
        // Log.e(TAG, "Image doesn't exists!");
        // }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Intent intent = getIntent();

        if (intent != null)
        {
            // imageData is the YCrCB data acquired from the preview.
            byte[] imageData = intent
                    .getByteArrayExtra(getString(R.string.ycrcb_image_data));
            int width = intent.getIntExtra(
                    getString(R.string.ycrcb_image_width), 0);
            int height = intent.getIntExtra(
                    getString(R.string.ycrcb_image_height), 0);

            BarcodeProcessor bp = new BarcodeProcessor();
            Bitmap binImage = bp.generateBinaryImage(width, height, imageData);
            UPCABarcode upacAB = new UPCABarcode();
            upacAB.decodeImage(binImage);

            ImageView myImage = (ImageView) findViewById(R.id.binary);
            myImage.setImageBitmap(binImage);
            myImage.setVisibility(ImageView.VISIBLE);
        }
    }
}
