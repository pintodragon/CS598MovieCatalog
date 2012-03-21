package edu.sunyit.chryslj.movie;

import java.util.List;

import edu.sunyit.chryslj.movie.enums.MediaFormat;
import edu.sunyit.chryslj.movie.enums.Rating;

public class Movie
{
	private String title = "";
	private Rating rated;
	private String genre = "";
	private int personalRaiting;
	private MediaFormat format;
	private Short runTime;
	private List<String> associatedLists;

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
	public String getGenre()
	{
		return genre;
	}

	/**
	 * @param genre
	 *            the genre to set
	 */
	public void setGenre(String genre)
	{
		this.genre = genre;
	}

	/**
	 * @return the personalRaiting
	 */
	public int getPersonalRaiting()
	{
		return personalRaiting;
	}

	/**
	 * @param personalRaiting
	 *            the personalRaiting to set
	 */
	public void setPersonalRaiting(int personalRaiting)
	{
		this.personalRaiting = personalRaiting;
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
	 * 
	 */
	@Override
	public String toString()
	{
		return title;
	}
}
