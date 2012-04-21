package edu.sunyit.chryslj.barcode;

import android.graphics.Color;
import android.util.Log;
import edu.sunyit.chryslj.exceptions.InvalidImageException;

/**
 * This class is an implementation of a UPC-A bar code reader. An UPC-A bar code
 * consists of 12 numbers encoded via a 7 module wide image.
 * 
 * @author Justin Chrysler
 * 
 */
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

    // The decoded barcode will be placed within this string builder.
    private StringBuilder barcodeBuilder = null;

    // The width of a module
    private int moduleWidth;

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
     *            the binary row data of the image we want to decode.
     * @return the decoded UPC-A value to be used to search for the product
     *         (Movie).
     */
    @Override
    public String decodeImage(int[] binaryRowData) throws InvalidImageException
    {
        int xOffset = locateFirstBar(binaryRowData);
        barcodeBuilder = new StringBuilder();
        moduleWidth = 1;

        // Starting guard
        xOffset = getPastGuard(binaryRowData, xOffset);
        // Starting with Odd parity (Space first) so when we hit a bar we
        // increment our digitIndex.

        // Start decoding. First half is 6 digits long.
        for (int digit = 0; digit < 6; digit++)
        {
            xOffset =
                    aquireDigit(binaryRowData, xOffset, SPACE_COLOR,
                            moduleWidth);
        }

        // Middle guard
        xOffset = getMiddlePastGuard(binaryRowData, xOffset);
        // Only care about the new x offset.
        // Log.d(TAG, "Second set of numbers start at: " + xOffset);

        // Second half is also 6 digits long.
        for (int digit = 0; digit < 6; digit++)
        {
            xOffset =
                    aquireDigit(binaryRowData, xOffset, BAR_COLOR, moduleWidth);
        }

        Log.d(TAG, "Barcode: " + barcodeBuilder.toString());

        if (!isCheckDigitValid(barcodeBuilder.toString()))
        {
            Log.e(TAG, "Check digit did not match checksum");
            throw new InvalidImageException(
                    "Check digit did not match checksum");
        }

        return barcodeBuilder.toString();
    }

    /**
     * This method reads the next digit from a binary row of image data. It
     * reads in the image data pixel by pixel and stores the widths of the
     * expected bars in an array. Each digit is a compilation of 4 different
     * sets of modules of different sizes. This information is then used to
     * match the digit read from the bar code to the digit pattern of a UPC-A
     * bar code.
     * 
     * @param binaryRowData
     *            the binary row data of the image we want to decode.
     * @param currentOffset
     *            the current location within the row that we are decoding.
     * @param startColor
     *            the expected starting color of the module for the next digit.
     * @param moduleWidth
     *            the normalized width of a single module.
     * @return the last location that was used within the binaryRowData.
     * @throws InvalidImageException
     *             if we did not find a digit.
     */
    private int aquireDigit(int[] binaryRowData, int currentOffset,
            int startColor, int moduleWidth) throws InvalidImageException
    {
        int[] digitWidths = new int[4];
        int digitIndex = 0;
        int previousPixel = startColor;
        int xOffset = currentOffset;

        for (int x = xOffset; x < binaryRowData.length; x++)
        {
            if (binaryRowData[x] != previousPixel)
            {
                int totalWidth = digitWidths[digitIndex];
                int discrepancy = digitWidths[digitIndex] % moduleWidth;

                if (discrepancy != 0)
                {
                    int adjustment = moduleWidth - discrepancy;

                    // Check the rounded up value of the division by 2. This
                    // helps cover the case where we want to check the
                    // discrepancy for odd numbers by value in the 1's place in
                    // the condition. AKA 3 / 2 = 1.5 and we want any
                    // discrepancy value of 1 or lower to result in true.
                    if (discrepancy < (int) Math.ceil(moduleWidth / 2.0))
                    {
                        totalWidth -= discrepancy;
                    }
                    else
                    {
                        totalWidth += adjustment;
                    }
                }
                digitWidths[digitIndex] = totalWidth / moduleWidth;

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

        // Append the digit to the string builder.
        barcodeBuilder.append(matchDigitPattern(digitWidths));

        return xOffset;
    }

    /**
     * This method validates the guard pattern and also helps determine the
     * width of a given module. The guard pattern is a single bar, a single
     * space and a single bar. The average width of all three is used as the
     * width of a module.
     * 
     * @param binaryRowData
     *            the binary row data of the image we want to decode.
     * @param xOffset
     *            the starting location in the row of data that we think the
     *            guard pattern is located at.
     * @return the location right after the guard pattern.
     * @throws InvalidImageException
     *             thrown if the guard pattern is invalid.
     */
    private int getPastGuard(int[] binaryRowData, int xOffset)
            throws InvalidImageException
    {
        int barWidth = 0;
        int spaceWidth = 0;
        int currentX = xOffset;
        int totalBarWidth = 0;
        int currentColor = BAR_COLOR;

        int currentPixel = binaryRowData[currentX++];

        for (int guardModule = 0; guardModule < 3; guardModule++)
        {
            barWidth = 0;
            while (currentPixel == currentColor &&
                    currentX < binaryRowData.length)
            {
                barWidth++;
                currentPixel = binaryRowData[currentX++];
            }

            if (currentColor == SPACE_COLOR)
            {
                spaceWidth = barWidth;
            }
            currentColor =
                    (currentColor == BAR_COLOR) ? SPACE_COLOR : BAR_COLOR;

            totalBarWidth += barWidth;
        }

        if (Math.abs(barWidth - spaceWidth) > MODULE_WIDTH_VAR)
        {
            Log.e(TAG, "Invalid guard patern.");
            throw new InvalidImageException(
                    "Invalid guard patern.");
        }

        moduleWidth = (int) Math.ceil(totalBarWidth / 3.0);

        return currentX;
    }

    /**
     * The middle guard could be in the pattern of space, bar, space, bar,
     * space. The second set of digits starts with a bar so we are going to
     * iterate through the spaces until we hit the bars we are looking for.
     * 
     * @param binaryRowData
     *            the binary row data of the image we want to decode.
     * @param xOffset
     *            the starting location in the row of data that we think the
     *            guard pattern is located at.
     * @return the location right after the guard pattern.
     * @throws InvalidImageException
     *             thrown if the guard pattern is invalid.
     */
    private int getMiddlePastGuard(int[] binaryRowData, int xOffset)
            throws InvalidImageException
    {
        int currentX = xOffset;

        for (int x = currentX; x < binaryRowData.length; x++)
        {
            if (binaryRowData[x] == BAR_COLOR)
            {
                currentX = x;
                break;
            }
        }

        // Offset returned as second element of the array.
        currentX = getPastGuard(binaryRowData, currentX);

        for (int x = currentX; x < binaryRowData.length; x++)
        {
            if (binaryRowData[x] == BAR_COLOR)
            {
                currentX = x;
                break;
            }
        }

        return currentX;
    }

    /**
     * Usually a UPC-A bar code has a quiet zone of 9 modules. We have no idea
     * how many pixels are per module yet so we are going to assume 2 pixel per
     * module for now and keep looking for a bar until the we have a quiet zone
     * of at least 18.
     * 
     * @param binaryRowData
     *            the binary row data of the image we want to decode.
     * @return the location of what we think is the first bar in the image data.
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

    /**
     * Match the given digit bar widths with the digit pattern expected by a
     * UPC-A encoding.
     * 
     * @param digitWidths
     *            an array of length 4 with the widths of the sets of modules we
     *            think is a digit.
     * @return the digit that we have decoded.
     * @throws InvalidImageException
     *             if the digit does not match an expected pattern.
     */
    private int matchDigitPattern(int[] digitWidths)
            throws InvalidImageException
    {
        int digitValue = -1;

        for (int index = 0; index < DIGIT_PATTERNS.length; index++)
        {
            boolean isMatch = true;
            for (int parityIndex = 0; parityIndex < DIGIT_PATTERNS[index].length; parityIndex++)
            {
                if (Math.abs(digitWidths[parityIndex] -
                        DIGIT_PATTERNS[index][parityIndex]) != 0)
                {
                    isMatch = false;
                    break;
                }
            }

            if (isMatch)
            {
                digitValue = index;
                isMatch = true;
                break;
            }
        }

        if (digitValue == -1)
        {
            throw new InvalidImageException(
                    "The digit did not match an expected pattern.");
        }

        return digitValue;
    }

    /**
     * The check digit is calculated according to a few rules. You use the first
     * eleven digits of the bar code to determine if the check digit is valid.
     * <ol>
     * <li>Calculate the sum of all the digits in the odd positions and multiply
     * the result by 3.</li>
     * <li>Calculate the sum of all the digits in the even positions.</li>
     * <li>Add the results of step 1 and 2.</li>
     * <li>If 10 minus the result of step 3 mod 10 is the check digit then the
     * bar code is valid.</li>
     * </ol>
     * 
     * @param barcode
     *            a string representation of the bar code.
     * @return if the check digit is valid or not.
     */
    private boolean isCheckDigitValid(String barcode)
    {
        boolean isValid = false;

        if (barcode.length() != 12)
        {
            isValid = false;
        }
        else
        {
            int oddSum = 0;
            int evenSum = 0;
            int checkDigit = Integer.parseInt("" + barcode.charAt(11));

            for (int charPos = 0; charPos < barcode.length() - 1; charPos++)
            {
                int digit = Integer.parseInt("" + barcode.charAt(charPos));
                // Because we start at 0 as an index pos % 2 == 0 indicates an
                // odd position digit.
                if ((charPos % 2) == 0)
                {
                    // Log.d(TAG, "Add: " + digit + " to " + oddSum);
                    oddSum = oddSum + digit;
                }
                else
                {
                    evenSum = evenSum + digit;
                }
            }

            // Log.d(TAG, "EvenSum: " + evenSum + " OddSum: " + oddSum);

            oddSum = oddSum * 3;

            int finalSum = evenSum + oddSum;
            isValid =
                    ((finalSum % 10) == 0) ? true
                            : ((10 - (finalSum % 10)) == checkDigit);
            // Log.d(TAG, "FinalSum: " + finalSum + " CheckDigit: " + checkDigit
            // +
            // " Checksum: " + (10 - (finalSum % 10)));
        }

        return isValid;
    }
}
