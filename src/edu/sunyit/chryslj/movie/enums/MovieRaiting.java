package edu.sunyit.chryslj.movie.enums;

public enum MovieRaiting
{
	G("G", "General Audiences. All Ages Admitted."),
	PG("PG", "Parental Guidance Suggested. Some Material May Not Be Suitable For Children."),
	PG13("PG-13", "Parents Strongly Cautioned. Some Material May Be Inappropriate For Children Under 13."),
	R("R", "Restricted. Children Under 17 Require Accompanying Parent or Adult Guardian."),
	NC17("NC-17", "No One 17 and Under Admitted.");

	private String shortHand;
	private String briefDescription;

	MovieRaiting(String shortHand, String briefDescription)
	{
		this.shortHand = shortHand;
		this.briefDescription = briefDescription;
	}

	public String getBriefDescription()
	{
		return briefDescription;
	}

	@Override
	public String toString()
	{
		return shortHand;
	}
}
