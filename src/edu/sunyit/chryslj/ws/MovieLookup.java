package edu.sunyit.chryslj.ws;

public interface MovieLookup
{
    public String lookupMovieByBarcode(String barcode);

    public String lookupMovieByTitle(String title);
}
