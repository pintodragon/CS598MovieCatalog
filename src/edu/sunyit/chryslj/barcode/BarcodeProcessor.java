package edu.sunyit.chryslj.barcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * 
 * @author Justin Chrysler
 * 
 */
public class BarcodeProcessor
{

    // TODO FOR TESTING ONLY.
    public BarcodeProcessor()
    {
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
     * @param image
     * @return
     */
    public Bitmap generateBinaryImage(Bitmap image)
    {
        // TODO great candidate for a new thread
        int width = image.getWidth();
        int height = image.getHeight();

        final int localWidth = 10;// width / 4;
        final int localHeight = 10;// height / 4;
        Bitmap binaryImage = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);

        Bitmap grayBMP = convertToGrayScale(image);

        // Lets partition the image off into a few grids. If the grid we
        // currently want to use would exceed the size of teh bitmap then
        // adjust to be within the bounds.

        for (int gridX = 0; gridX < width; gridX += localWidth)
        {
            if ((gridX + localWidth) > width)
            {
                gridX = width - localWidth;
            }

            for (int gridY = 0; gridY < height; gridY += localHeight)
            {
                if ((gridY + localHeight) > height)
                {
                    gridY = height - localHeight;
                }

                int[] gridPixelData = new int[localWidth * localHeight];
                grayBMP.getPixels(gridPixelData, 0, localWidth, gridX, gridY,
                        localWidth, localHeight);

                int binaryThreshold = otsuBinaryThreashold(gridPixelData);

                for (int column = 0; column < localWidth; column++)
                {
                    for (int row = 0; row < localHeight; row++)
                    {
                        int newRed;
                        int newGreen;
                        int newBlue;
                        int pixelValue = getPixelValue(grayBMP.getPixel(column +
                                gridX, row + gridY));
                        if (pixelValue >= binaryThreshold)
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

                        binaryImage.setPixel(column + gridX, row + gridY,
                                Color.rgb(newRed, newGreen, newBlue));
                    }
                }
            }
        }

        return binaryImage;
    }

    /**
     * Using Otsu's algorithm to generate an image with only two values. This
     * helps reduce the work needed to detect edges, or in our case to detect
     * lines of a barcode.
     * 
     * @see <a href="http://en.wikipedia.org/wiki/Otsu's_method">Otsu's
     *      method</a>
     * @param grayBMP
     * @return
     */
    private int otsuBinaryThreashold(int[] grayPixelData)
    {
        int binaryThreshold = 0;
        float sum = 0;
        float sumB = 0;
        int backgroundWeight = 0;
        int foregroundWegiht = 0;
        float maxVariance = 0;

        int[] histogram = generateImageHistogram(grayPixelData);
        int numberOfPixels = grayPixelData.length;

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
    private int[] generateImageHistogram(int[] pixelData)
    {
        int[] histogram = new int[256];

        for (int index = 0; index < histogram.length; index++)
        {
            histogram[index] = 0;
        }

        for (int colorIndex : pixelData)
        {
            int piexlValue = getPixelValue(colorIndex);

            histogram[piexlValue]++;
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
}
