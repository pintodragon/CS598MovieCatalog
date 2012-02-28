package edu.sunyit.chryslj.camera;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;

public class BarCodeReader {
	
	private SurfaceHolder cameraHolder = null;
	private Camera deviceCamera = null;
	private Parameters cameraParameters = null;
	
	public BarCodeReader(SurfaceHolder cameraHolder)
	{
		this.cameraHolder = cameraHolder;
		deviceCamera = Camera.open();
		cameraParameters = deviceCamera.getParameters();
		deviceCamera.release();
		
		cameraParameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
		// TODO look into focus modes.
		cameraParameters.setFocusMode(Parameters.FOCUS_MODE_EDOF);
	}
	
	public String readBarCode()
	{
		deviceCamera = Camera.open();
		
		if (deviceCamera != null)
		{
			deviceCamera.setParameters(cameraParameters);
			try {
				deviceCamera.setPreviewDisplay(cameraHolder);
				deviceCamera.startPreview();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Release the camera now that we are done with it
			deviceCamera.stopPreview();
			deviceCamera.release();
		}
		
		return "";
	}
	
	public Bitmap convertBMPToGray(Bitmap colorBMP)
	{
		int width = colorBMP.getWidth();
		int height = colorBMP.getHeight();
		
		Bitmap grayBMP = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(grayBMP);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);		
		c.drawBitmap(colorBMP, 0, 0, paint);
		
		return grayBMP;
	}
}
