package edu.sunyit.chryslj.barcode;

import android.graphics.Bitmap;

public interface BarcodeDecoder
{
	public long decodeImage(Bitmap binaryImage);
}
