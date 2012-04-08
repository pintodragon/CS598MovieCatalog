package edu.sunyit.chryslj.ws;

import edu.sunyit.chryslj.movie.Movie;

public interface MovieLookup
{
    public Movie lookupMovieByBarcode(String barcode);

    public Movie lookupMovieByTitle(String title);

    public Movie gatherMoreInformation(Movie movie);
}
