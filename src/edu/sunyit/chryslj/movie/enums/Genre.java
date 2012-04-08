package edu.sunyit.chryslj.movie.enums;

public enum Genre
{
    ACTION(0, "Action"),
    ADVENTURE(1, "Adventure"),
    ANIMATION(2, "Animation"),
    BIOGRAPHY(3, "Biography"),
    COMEDY(4, "Comedy"),
    CRIME(5, "Crime"),
    DOCUMENTARY(6, "Documentary"),
    DRAMA(7, "Drama"),
    FAMILY(8, "Family"),
    FANTASY(9, "Fantasy"),
    FILM_Noir(10, "Film-Noir"),
    GAME_SHOW(11, "Game-Show"),
    HISTORY(12, "History"),
    HOROR(13, "Horror"),
    MUSIC(14, "Music"),
    MUSICAL(15, "Musical"),
    MYSTERY(16, "Mystery"),
    NEWS(17, "News"),
    REALITY_TV(18, "Reality-TV"),
    ROMANCE(19, "Romance"),
    SCI_FI(20, "Sci-Fi"),
    SPORT(21, "Sport"),
    TALK_SHOW(22, "Talk-Show"),
    THRILLER(23, "Thriller"),
    WAR(24, "War"),
    WESTERN(25, "Western"),
    UNKNOWN(26, "Unknown");

    private int id;
    private String title;

    Genre(int id, String title)
    {
        this.id = id;
        this.title = title;
    }

    public static Genre getGenreByTitle(String title)
    {
        Genre genre = null;

        for (Genre possible : Genre.values())
        {
            if (title.equals(possible.getTitle()))
            {
                genre = possible;
                break;
            }
        }

        return genre;
    }

    public int getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    @Override
    public String toString()
    {
        return getTitle();
    }
}
