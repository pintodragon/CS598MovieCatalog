package edu.sunyit.chryslj.movie.enums;

public enum Rating
{
	G(0, "G", "General Audiences. All Ages Admitted."),
	PG(1, "PG", "Parental Guidance Suggested. Some Material May Not Be Suitable For Children."),
	PG_13(2, "PG-13", "Parents Strongly Cautioned. Some Material May Be Inappropriate For Children Under 13."),
	R(3, "R", "Restricted. Children Under 17 Require Accompanying Parent or Adult Guardian."),
	NC_17(4, "NC-17", "No One 17 and Under Admitted.");

	private int id;
	private String title;
	private String description;

	Rating(int id, String title, String description)
	{
		this.id = id;
		this.title = title;
		this.description = description;
	}

	public int getId()
	{
		return id;
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
