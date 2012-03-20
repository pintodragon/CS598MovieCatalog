package edu.sunyit.chryslj.movie.enums;

public enum MovieRaiting
{
	G("G", "General Audiences. All Ages Admitted."),
	PG("PG", "Parental Guidance Suggested. Some Material May Not Be Suitable For Children."),
	PG13("PG-13", "Parents Strongly Cautioned. Some Material May Be Inappropriate For Children Under 13."),
	R("R", "Restricted. Children Under 17 Require Accompanying Parent or Adult Guardian."),
	NC17("NC-17", "No One 17 and Under Admitted.");

	private String title;
	private String description;

	MovieRaiting(String title, String description)
	{
		this.title = title;
		this.description = description;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	@Override
	public String toString()
	{
		return title;
	}
}
