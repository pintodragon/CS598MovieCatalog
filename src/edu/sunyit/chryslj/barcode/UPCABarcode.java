package edu.sunyit.chryslj.barcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class UPCABarcode implements BarcodeDecoder
{
	private static final String TAG = UPCABarcode.class.getName();

	@Override
	public long decodeImage(Bitmap binaryImage)
	{
		int width = binaryImage.getWidth();
		int height = binaryImage.getHeight();

		int readHeight = height / 2;

		StringBuffer sb = new StringBuffer();

		int white = Color.rgb(255, 255, 255);
		int black = Color.rgb(0, 0, 0);

		for (int x = 0; x < width; x++)
		{
			int pixel = binaryImage.getPixel(x, readHeight);

			if (pixel == white)
			{
				sb.append("W ");
			}
			else
			{
				sb.append("B ");
			}
		}

		Log.d(TAG, "ReadHeight: " + readHeight + " Value: " + sb.toString());

		return 0;
	}

}
