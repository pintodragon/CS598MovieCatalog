package edu.sunyit.chryslj.movie.enums;

public enum MediaFormat
{
	VHS("VHS", "Video Home System"),
	DVD("DVD", "Digital Versatile Disc"),
	BLURAY("Blu-ray", "Blue Ray");

	private String shortHand;
	private String name;

	MediaFormat(String shortHand, String name)
	{
		this.shortHand = shortHand;
		this.name = name;
	}

	public String getShortHand()
	{
		return shortHand;
	}

	public String getName()
	{
		return name;
	}
}
