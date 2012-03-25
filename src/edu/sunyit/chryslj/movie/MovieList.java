package edu.sunyit.chryslj.movie;

public class MovieList
{
	private int id = 0;
	private String title = "";

	/**
	 * @return the title
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

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
	 * 
	 */
	@Override
	public String toString()
	{
		return title;
	}
}
