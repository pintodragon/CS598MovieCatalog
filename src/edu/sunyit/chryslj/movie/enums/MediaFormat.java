package edu.sunyit.chryslj.movie.enums;

public enum MediaFormat
{
	VHS("VHS", "Video Home System"),
	DVD("DVD", "Digital Versatile Disc"),
	BLURAY("Blu-ray", "Blue Ray");

	private String title;
	private String name;

	MediaFormat(String title, String name)
	{
		this.title = title;
		this.name = name;
	}

	public String getTitle()
	{
		return title;
	}

	public String getName()
	{
		return name;
	}
}
