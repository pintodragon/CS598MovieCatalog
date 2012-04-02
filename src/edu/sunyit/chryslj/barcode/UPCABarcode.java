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

        int black = Color.rgb(0, 0, 0);

        for (int x = 0; x < width; x++)
        {
            int pixel = binaryImage.getPixel(x, readHeight);

            if (pixel == black)
            {
                sb.append("B ");
            }
            else
            {
                sb.append("W ");
            }
        }

        Log.d(TAG, "ReadHeight: " + readHeight + " Value: " + sb.toString());

        readHeight = height / 2;

        sb = new StringBuffer();

        black = Color.rgb(100, 100, 100);

        for (int x = 0; x < width; x++)
        {
            int pixel = binaryImage.getPixel(x, readHeight);

            sb.append(pixel + " ");
        }

        Log.d(TAG, "ReadHeight: " + readHeight + " Value: " + sb.toString());

        return 0;
    }

}
