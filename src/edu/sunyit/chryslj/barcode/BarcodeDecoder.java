package edu.sunyit.chryslj.barcode;

import edu.sunyit.chryslj.exceptions.InvalidImageException;

public interface BarcodeDecoder
{
    public String decodeImage(int[] binaryRowData) throws InvalidImageException;
}
