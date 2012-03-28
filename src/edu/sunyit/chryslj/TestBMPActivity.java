package edu.sunyit.chryslj;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import edu.sunyit.chryslj.barcode.BarcodeProcessor;
import edu.sunyit.chryslj.barcode.UPCABarcode;

public class TestBMPActivity extends Activity
{
	private static final String TAG = TestBMPActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_test);

		File imgFile = new File("/sdcard/clearbarcode.jpg");

		if (imgFile.exists())
		{
			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
			        .getAbsolutePath());

			BarcodeProcessor bp = new BarcodeProcessor();

			Bitmap binImage = bp.generateBinaryImage(myBitmap);
			ImageView myImage = (ImageView) findViewById(R.id.binary);
			myImage.setImageBitmap(binImage);
			UPCABarcode upacAB = new UPCABarcode();
			upacAB.decodeImage(binImage);
			myImage.setVisibility(ImageView.VISIBLE);
			Log.i(TAG, "Image exists");
		}
		else
		{
			Log.e(TAG, "Image doesn't exists!");
		}
	}
}
