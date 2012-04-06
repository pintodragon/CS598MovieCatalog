package edu.sunyit.chryslj.barcode;

import android.graphics.Color;
import android.util.Log;
import edu.sunyit.chryslj.exceptions.InvalidImageException;

public class UPCABarcode implements BarcodeDecoder
{
    private static final String TAG = UPCABarcode.class.getSimpleName();
    private static final int BAR_COLOR = Color.BLACK;
    private static final int SPACE_COLOR = Color.WHITE;
    // Number of pixels a module is allowed to vary from assumed width
    private static final int MODULE_WIDTH_VAR = 2;

    // These are the patterns that encode the numbers into an UPC-A barcode.
    // This information was aquired from: http://www.adams1.com/upccode.html
    private final int[][] DIGIT_PATTERNS = { { 3, 2, 1, 1 }, // 0
            { 2, 2, 2, 1 }, // 1
            { 2, 1, 2, 2 }, // 2
            { 1, 4, 1, 1 }, // 3
            { 1, 1, 3, 2 }, // 4
            { 1, 2, 3, 1 }, // 5
            { 1, 1, 1, 4 }, // 6
            { 1, 3, 1, 2 }, // 7
            { 1, 2, 1, 3 }, // 8
            { 3, 1, 1, 2 } // 9
            };

    /**
     * UPC-A barcodes start with a guard which is a bar, space, bar pattern. The
     * digits contain four values. These four values are the number of bars and
     * spaces within a seven module wide portion of the barcode. On the left of
     * the middle the digits have an Odd parity which means they start with
     * spaces. The middle is another guard of bar, space, bar. On the right of
     * the middle they start with Even parity. Finally at the end there is
     * another guard. If there are extra bars and lines after the final guard
     * these for for extended information. For movies the numbers would explain
     * collectors editions, dvd/blu-ray format, etc.
     * 
     * @param binaryRowData
     * @return the decoded UPC-A value to be used to search for the product
     *         (Movie).
     */
    @Override
    public String decodeImage(int[] binaryRowData) throws InvalidImageException
    {
        int xOffset = locateFirstBar(binaryRowData);
        int moduleWidth = 0;

        int[] widthAndNewStart = getPastGuard(binaryRowData, xOffset);
        moduleWidth = widthAndNewStart[0];
        xOffset = widthAndNewStart[1];
        if (xOffset == Integer.MAX_VALUE)
        {
            throw new InvalidImageException("No valid guard patern.");
        }

        // Starting with Odd parity (Space first) so when we hit a bar we
        // increment our digitIndex.
        StringBuilder barcodeBuilder = new StringBuilder();

        // Start decoding.
        for (int digit = 0; digit < 6; digit++)
        {
            int[] digitAndOffset =
                    aquireDigit(binaryRowData, xOffset, SPACE_COLOR,
                            moduleWidth);
            barcodeBuilder.append(digitAndOffset[0]);
            xOffset = digitAndOffset[1];
        }

        Log.d(TAG, "Barcode: " + barcodeBuilder.toString());

        return barcodeBuilder.toString();
    }

    private int[] aquireDigit(int[] binaryRowData, int currentOffset,
            int startColor, int moduleWidth)
    {
        int[] digitWidths = new int[4];
        int digitIndex = 0;
        int previousPixel = startColor;
        int xOffset = currentOffset;

        for (int x = xOffset; x < binaryRowData.length; x++)
        {
            if (binaryRowData[x] != previousPixel)
            {
                Log.d(TAG, "Digit: " + digitIndex + " WidthTotal: " +
                        digitWidths[digitIndex] + " Width: " +
                        (digitWidths[digitIndex] / moduleWidth));
                digitWidths[digitIndex] = digitWidths[digitIndex] / moduleWidth;

                digitIndex++;
                previousPixel = binaryRowData[x];

                if (digitIndex == digitWidths.length)
                {
                    // Done with first digit
                    xOffset = x;
                    break;
                }
            }
            digitWidths[digitIndex]++;
        }

        int[] digitAndOffset = new int[2];
        digitAndOffset[0] = matchDigitPattern(digitWidths);
        digitAndOffset[1] = xOffset;

        return digitAndOffset;
    }

    private int[] getPastGuard(int[] binaryRowData, int xOffset)
    {
        int barWidth = 0;
        int spaceWidth = 0;
        int currentX = xOffset;

        int currentPixel = binaryRowData[currentX++];

        while (currentPixel == BAR_COLOR)
        {
            barWidth++;
            currentPixel = binaryRowData[currentX++];
        }

        while (currentPixel == SPACE_COLOR)
        {
            spaceWidth++;
            currentPixel = binaryRowData[currentX++];
        }

        if (Math.abs(barWidth - spaceWidth) > MODULE_WIDTH_VAR)
        {
            Log.e(TAG, "Invalid module width");
            currentX = Integer.MAX_VALUE;
        }
        else
        {
            barWidth = 0;
            while (currentPixel == BAR_COLOR)
            {
                barWidth++;
                currentPixel = binaryRowData[currentX++];
            }

            if (Math.abs(barWidth - spaceWidth) > MODULE_WIDTH_VAR)
            {
                currentX = Integer.MAX_VALUE;
            }
        }

        int[] widthAndStart = new int[2];
        widthAndStart[0] = barWidth;
        widthAndStart[1] = currentX;

        return widthAndStart;
    }

    /**
     * Usually a UPC-A barcode has a quiet zone of 9 modules. We have no idea
     * how many pixels are per module yet so we are going to assume 2 pixel per
     * module for now and keep looking for a bar until the we have a quiet zone
     * of at least 18.
     * 
     * @param binaryRowData
     * @return
     */
    private int locateFirstBar(int[] binaryRowData)
    {
        // Assumption from method comment.
        final int QUIET_ZONE_VALID = 18;
        int xOffset = 0;
        int quietZone = 0;

        for (int x = 0; x < binaryRowData.length; x++)
        {
            // If we have a value that looks like it might be a bar
            if (binaryRowData[x] == BAR_COLOR)
            {
                // Make sure we have enough quietZone
                if (quietZone >= QUIET_ZONE_VALID)
                {
                    Log.d(TAG, "We found our first bar at: " + x);
                    xOffset = x;
                    break;
                }
                quietZone = 0;
            }
            else
            {
                quietZone++;
            }
        }

        return xOffset;
    }

    private int matchDigitPattern(int[] digitWidths)
    {
        int digitValue = 0;

        for (int index = 0; index < DIGIT_PATTERNS.length; index++)
        {
            boolean isMatch = true;
            for (int parityIndex = 0; parityIndex < DIGIT_PATTERNS[index].length; parityIndex++)
            {
                if (Math.abs(digitWidths[parityIndex] -
                        DIGIT_PATTERNS[index][parityIndex]) > MODULE_WIDTH_VAR)
                {
                    isMatch = false;
                }
            }

            if (isMatch)
            {
                digitValue = index;
                break;
            }
        }

        return digitValue;
    }

}
