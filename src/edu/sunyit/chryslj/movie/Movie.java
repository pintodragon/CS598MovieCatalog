package edu.sunyit.chryslj.movie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.sunyit.chryslj.movie.enums.Genre;
import edu.sunyit.chryslj.movie.enums.MediaFormat;
import edu.sunyit.chryslj.movie.enums.Rating;

/**
 * A Serializable instance of the movie information that we store in the movie
 * table.
 * 
 * @author Justin Chrysler
 * 
 */
public class Movie implements Serializable
{
    private static final long serialVersionUID = -8950948224949570678L;

    private int id = -1;
    private String title = "";
    private Rating rated = Rating.UNRATED;
    private Genre genre = Genre.UNKNOWN;
    private int personalRating = 0;
    private MediaFormat format = MediaFormat.DVD;
    private Short runTime = 0;
    private List<String> associatedLists = new ArrayList<String>();

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the rated
     */
    public Rating getRated()
    {
        return rated;
    }

    /**
     * @param rated
     *            the rated to set
     */
    public void setRated(Rating rated)
    {
        this.rated = rated;
    }

    /**
     * @return the genre
     */
    public Genre getGenre()
    {
        return genre;
    }

    /**
     * @param genre
     *            the genre to set
     */
    public void setGenre(Genre genre)
    {
        this.genre = genre;
    }

    /**
     * @return the personalRaiting
     */
    public int getPersonalRating()
    {
        return personalRating;
    }

    /**
     * @param personalRaiting
     *            the personalRaiting to set
     */
    public void setPersonalRating(int personalRating)
    {
        this.personalRating = personalRating;
    }

    /**
     * @return the format
     */
    public MediaFormat getFormat()
    {
        return format;
    }

    /**
     * @param format
     *            the format to set
     */
    public void setFormat(MediaFormat format)
    {
        this.format = format;
    }

    /**
     * @return the runTime
     */
    public Short getRunTime()
    {
        return runTime;
    }

    /**
     * @param runTime
     *            the runTime to set
     */
    public void setRunTime(Short runTime)
    {
        this.runTime = runTime;
    }

    /**
     * @return the associatedLists
     */
    public List<String> getAssociatedLists()
    {
        return associatedLists;
    }

    /**
     * @param associatedLists
     *            the associatedLists to set
     */
    public void setAssociatedLists(List<String> associatedLists)
    {
        this.associatedLists = associatedLists;
    }

    /**
     * @param id
     *            the associatedLists to set
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Used by our list adapter.
     */
    @Override
    public String toString()
    {
        return title;
    }
}
