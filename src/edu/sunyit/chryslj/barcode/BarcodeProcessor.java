package edu.sunyit.chryslj.barcode;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;

/**
 * 
 * @author Justin Chrysler
 * 
 */
public class BarcodeProcessor
{

	private SurfaceHolder cameraHolder = null;
	private Camera deviceCamera = null;
	private Parameters cameraParameters = null;

	/**
	 * 
	 * @param cameraHolder
	 */
	public BarcodeProcessor(SurfaceHolder cameraHolder)
	{
		this.cameraHolder = cameraHolder;
		deviceCamera = Camera.open();
		cameraParameters = deviceCamera.getParameters();
		deviceCamera.release();

		cameraParameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
		// TODO look into focus modes.
		cameraParameters.setFocusMode(Parameters.FOCUS_MODE_EDOF);
	}

	/**
	 * 
	 * @return
	 */
	public Bitmap aquireImage()
	{
		Bitmap barcodeImage = null;

		deviceCamera = Camera.open();

		if (deviceCamera != null)
		{
			deviceCamera.setParameters(cameraParameters);
			try
			{
				deviceCamera.setPreviewDisplay(cameraHolder);
				deviceCamera.startPreview();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Release the camera now that we are done with it
			deviceCamera.stopPreview();
			deviceCamera.release();
		}

		return barcodeImage;
	}

	/**
	 * 
	 * @param image
	 * @param type
	 * @return
	 */
	public long interpretCode(Bitmap image, BarcodeType type)
	{
		// TODO
		return 0;
	}

	/**
	 * 
	 * @param image
	 * @return
	 */
	public BarcodeType determineType(Bitmap image)
	{
		// TODO
		return null;
	}

	/**
	 * 
	 * @param colorBMP
	 * @return
	 */
	private Bitmap convertToGrayScale(Bitmap colorBMP)
	{
		int width = colorBMP.getWidth();
		int height = colorBMP.getHeight();

		Bitmap grayBMP = Bitmap.createBitmap(width, height,
		        Bitmap.Config.RGB_565);
		Canvas c = new Canvas(grayBMP);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(colorBMP, 0, 0, paint);

		return grayBMP;
	}

	/**
	 * Using Otsu's algorithm to generate an image with only two values. This
	 * helps reduce the work needed to detect edges, or in our case to detect
	 * lines of a barcode.
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Otsu's_method">Otsu's
	 *      method</a>
	 * @param image
	 * @return
	 */
	private Bitmap generateBinaryImage(Bitmap image)
	{
		int width = image.getWidth();
		int height = image.getHeight();

		Bitmap grayBMP = convertToGrayScale(image);
		int binaryThreshold = otsuBinaryThreashold(grayBMP);

		Bitmap binaryImage = Bitmap.createBitmap(width, height,
		        Bitmap.Config.RGB_565);

		for (int column = 0; column < width; column++)
		{
			for (int row = 0; row < height; row++)
			{
				int newColor;
				if (grayBMP.getPixel(column, row) > binaryThreshold)
				{
					newColor = Color.WHITE;
				}
				else
				{
					newColor = Color.BLACK;
				}
				binaryImage.setPixel(column, row, newColor);
			}
		}

		image.recycle();
		grayBMP.recycle();

		return binaryImage;
	}

	/**
	 * 
	 * @param image
	 * @return
	 */
	private int[] generateImageHistogram(Bitmap image)
	{
		// An RGB 565 image has 32*64*32 possible pixel values.
		int[] histogram = new int[32 * 64 * 32];

		for (int index = 0; index < histogram.length; index++)
		{
			histogram[index] = 0;
		}

		for (int column = 0; column < image.getWidth(); column++)
		{
			for (int row = 0; row < image.getHeight(); row++)
			{
				int colorIndex = image.getPixel(column, row);
				histogram[colorIndex]++;
			}
		}

		return histogram;
	}

	/**
	 * 
	 * @param grayBMP
	 * @return
	 */
	private int otsuBinaryThreashold(Bitmap grayBMP)
	{
		int binaryThreshold = 0;
		int[] histogram = generateImageHistogram(grayBMP);
		int numberOfPixels = grayBMP.getWidth() * grayBMP.getHeight();

		float sumClassA = 0;
		for (int index = 0; index < histogram.length; index++)
		{
			sumClassA += index * histogram[index];
		}

		float sumClassB = 0;
		int classAProp = 0;
		int classBProp = 0;

		float maxVariance = 0;

		for (int index = 0; index < histogram.length; index++)
		{
			classAProp += histogram[index];

			if (classAProp == 0)
			{
				// Do not want to divide by 0
				continue;
			}

			classBProp = numberOfPixels - classAProp;

			if (classBProp == 0)
			{
				// Done
				break;
			}

			sumClassB += (float) (index * histogram[index]);
			float meanClassA = sumClassB / classAProp;
			float meanClassB = (sumClassA - sumClassB) / classBProp;

			float varianceBetweenClasses = (float) classAProp *
			        (float) classBProp * (meanClassA - meanClassB) *
			        (meanClassA - meanClassB);

			if (varianceBetweenClasses > maxVariance)
			{
				maxVariance = varianceBetweenClasses;
				binaryThreshold = index;
			}
		}

		return binaryThreshold;
	}
}
