package edu.sunyit.chryslj.ws;

import edu.sunyit.chryslj.movie.Movie;

/**
 * Interface describing the method used by this application for looking up
 * information about a Movie.
 * 
 * @author Justin Chrysler
 * 
 */
public interface MovieLookup
{
    public Movie lookupMovieByBarcode(String barcode);

    public Movie lookupMovieByTitle(String title);

    public Movie gatherMoreInformation(Movie movie);
}
