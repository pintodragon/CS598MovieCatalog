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

	// TODO FOR TESTING ONLY.
	public BarcodeProcessor()
	{
	}

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
	public long decodeImage(Bitmap image, BarcodeDecoder type)
	{
		// TODO
		return 0;
	}

	/**
	 * 
	 * @param image
	 * @return
	 */
	public BarcodeDecoder determineType(Bitmap image)
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
	 * 
	 * @param image
	 * @return
	 */
	public Bitmap generateBinaryImage(Bitmap image)
	{
		// TODO great candidate for a new thread
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
				int newRed;
				int newGreen;
				int newBlue;
				int pixelValue = getPixelValue(grayBMP.getPixel(column, row));
				if (pixelValue > binaryThreshold)
				{
					newRed = 255;
					newGreen = 255;
					newBlue = 255;
				}
				else
				{
					newRed = 0;
					newGreen = 0;
					newBlue = 0;
				}

				binaryImage.setPixel(column, row,
				        Color.rgb(newRed, newGreen, newBlue));
			}
		}

		return binaryImage;
	}

	/**
	 * 
	 * @param image
	 * @return
	 */
	private int[] generateImageHistogram(Bitmap image)
	{
		int[] histogram = new int[256];

		for (int index = 0; index < histogram.length; index++)
		{
			histogram[index] = 0;
		}

		for (int column = 0; column < image.getWidth(); column++)
		{
			for (int row = 0; row < image.getHeight(); row++)
			{
				int colorIndex = image.getPixel(column, row);
				int piexlValue = getPixelValue(colorIndex);

				histogram[piexlValue]++;
			}
		}

		return histogram;
	}

	/**
	 * 
	 * @param pixel
	 * @return
	 */
	private int getPixelValue(int pixel)
	{
		int r = Color.red(pixel);
		int g = Color.green(pixel);
		int b = Color.blue(pixel);

		return (int) ((r + g + b) / 3);
	}

	/**
	 * Using Otsu's algorithm to generate an image with only two values. This
	 * helps reduce the work needed to detect edges, or in our case to detect
	 * lines of a barcode.
	 * 
	 * This is the global version which during testing worked if the image had
	 * consistent lighting. If there was a brighter part of the image due to
	 * camera flash parts of the barcode were unreadable.
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Otsu's_method">Otsu's
	 *      method</a>
	 * @param grayBMP
	 * @return
	 */
	private int otsuBinaryThreashold(Bitmap grayBMP)
	{
		int binaryThreshold = 0;
		float sum = 0;
		float sumB = 0;
		int backgroundWeight = 0;
		int foregroundWegiht = 0;
		float maxVariance = 0;

		int[] histogram = generateImageHistogram(grayBMP);
		int numberOfPixels = grayBMP.getWidth() * grayBMP.getHeight();

		for (int index = 0; index < histogram.length; index++)
		{
			sum += index * histogram[index];
		}

		for (int index = 0; index < histogram.length; index++)
		{
			backgroundWeight += histogram[index];
			// Make sure the background weight isn't 0. Do not want to divide
			// by zero later on.
			if (backgroundWeight == 0)
			{
				continue;
			}

			foregroundWegiht = numberOfPixels - backgroundWeight;
			// Check if we have gone through the entire image.
			if (foregroundWegiht == 0)
			{
				break;
			}

			sumB += (float) (index * histogram[index]);

			float backgroundMean = sumB / backgroundWeight;
			float foregroundMean = (sum - sumB) / foregroundWegiht;

			// Calculate the variance between the classes
			float weightDiff = (backgroundMean - foregroundMean);
			// Otsu: w1(t)*w2(t)*[u1(t) - u2(t)]^2
			float betweenVariance = (float) backgroundWeight *
			        (float) foregroundWegiht * weightDiff * weightDiff;

			// See if there is a new max variance
			if (betweenVariance > maxVariance)
			{
				maxVariance = betweenVariance;
				binaryThreshold = index;
			}
		}

		return binaryThreshold;
	}
}
