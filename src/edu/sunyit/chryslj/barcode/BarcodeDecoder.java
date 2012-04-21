package edu.sunyit.chryslj.barcode;

import edu.sunyit.chryslj.exceptions.InvalidImageException;

/**
 * This interface defines the methods required for this application that all
 * barcode readers will use.
 * 
 * @author Justin Chrysler
 * 
 */
public interface BarcodeDecoder
{
    public String decodeImage(int[] binaryRowData) throws InvalidImageException;
}
