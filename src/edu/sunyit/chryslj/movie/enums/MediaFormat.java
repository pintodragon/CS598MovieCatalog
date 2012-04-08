package edu.sunyit.chryslj.movie.enums;

public enum MediaFormat
{
    VHS(0, "VHS", "Video Home System"),
    DVD(1, "DVD", "Digital Versatile Disc"),
    BLU_RAY(2, "Blu-ray", "Blue Ray");

    private int id;
    private String title;
    private String name;

    MediaFormat(int id, String title, String name)
    {
        this.id = id;
        this.title = title;
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return getTitle();
    }
}
