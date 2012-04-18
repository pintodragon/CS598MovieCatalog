package edu.sunyit.chryslj.movie;

import java.util.Comparator;

public class MovieComparator implements Comparator<Movie>
{
    private String compareKey = "";

    // an integer < 0 if lhs is less than rhs, 0 if they are equal, and > 0 if
    // lhs is greater than rhs.
    @Override
    public int compare(Movie obj1, Movie obj2)
    {
        int compareValue = 0;

        if (obj1.equals(obj2))
        {
            compareValue = 0;
        }
        else if (compareKey.equals("Title"))
        {
            String title1 = obj1.getTitle();
            String title2 = obj2.getTitle();

            compareValue = title1.compareTo(title2);
        }
        else if (compareKey.equals("Rated"))
        {
            int rated1 = obj1.getRated().getId();
            int rated2 = obj2.getRated().getId();

            compareValue = rated1 - rated2;
        }
        else if (compareKey.equals("Personal Rating"))
        {
            int personalRating1 = obj1.getPersonalRating();
            int personalRating2 = obj2.getPersonalRating();

            compareValue = personalRating2 - personalRating1;
        }
        else if (compareKey.equals("Genre"))
        {
            int genre1 = obj1.getGenre().getId();
            int genre2 = obj2.getGenre().getId();

            compareValue = genre1 - genre2;
        }
        else if (compareKey.equals("Format"))
        {
            int format1 = obj1.getFormat().getId();
            int format2 = obj2.getFormat().getId();

            compareValue = format1 - format2;
        }
        else if (compareKey.equals("Runtime"))
        {
            short runtime1 = obj1.getRunTime();
            short runtime2 = obj2.getRunTime();

            compareValue = (int) (runtime2 - runtime1);
        }

        return compareValue;
    }

    public void setCompareKey(String compareKey)
    {
        this.compareKey = compareKey;
    }
}
