package edu.sunyit.chryslj.movie;

public class List
{
	private String title = "";

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
