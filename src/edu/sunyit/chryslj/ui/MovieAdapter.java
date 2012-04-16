package edu.sunyit.chryslj.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;

public class MovieAdapter extends ArrayAdapter<Movie>
{
    private List<Movie> items;
    private String sortedBy;

    private Context context;

    /**
     * 
     * @param context
     * @param textViewResourceId
     * @param items
     * @param sortedBy
     *            the String that corresponds to a column within the movie
     *            table.
     */
    public MovieAdapter(Context context, int textViewResourceId,
                        List<Movie> items)
    {
        super(context, textViewResourceId, items);
        this.items = items;
        this.context = context;
        sortedBy = "None";
    }

    public void setSortedBy(String sortedBy)
    {
        this.sortedBy = sortedBy;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE);
            convertView =
                    layoutInflater.inflate(R.layout.movie_list_item, null);
        }

        Movie currentMovie = items.get(position);

        if (currentMovie != null)
        {
            TextView movieTitle =
                    (TextView) convertView
                            .findViewById(R.id.movie_list_item_title);
            if (movieTitle != null)
            {
                String title =
                        context.getResources().getString(
                                R.string.movie_list_item_title,
                                currentMovie.getTitle());
                movieTitle.setText(title);
            }

            TextView movieRated =
                    (TextView) convertView
                            .findViewById(R.id.movie_list_item_rated);
            if (movieRated != null)
            {
                String rated =
                        context.getResources().getString(
                                R.string.movie_list_item_rated,
                                currentMovie.getRated());
                movieRated.setText(rated);
            }

            TextView sortedByView =
                    (TextView) convertView
                            .findViewById(R.id.movie_list_item_sort);
            TextView sortedByValue =
                    (TextView) convertView
                            .findViewById(R.id.movie_list_item_sort_val);

            if (sortedByView != null && sortedByValue != null)
            {
                String valueTxt = "";

                if (sortedBy.equals("Title"))
                {
                    valueTxt = currentMovie.getTitle();
                }
                else if (sortedBy.equals("Rated"))
                {
                    valueTxt = currentMovie.getRated().getTitle();
                }
                else if (sortedBy.equals("Personal Rating"))
                {
                    valueTxt = "" + currentMovie.getPersonalRating();
                }
                else if (sortedBy.equals("Genre"))
                {
                    valueTxt = currentMovie.getGenre().getTitle();
                }
                else if (sortedBy.equals("Format"))
                {
                    valueTxt = currentMovie.getFormat().getTitle();
                }
                else if (sortedBy.equals("Runtime"))
                {
                    valueTxt = "" + currentMovie.getRunTime();
                }

                String viewTxt =
                        context.getResources().getString(
                                R.string.movie_list_item_sort, sortedBy);
                sortedByView.setText(viewTxt);

                valueTxt =
                        context.getResources().getString(
                                R.string.movie_list_item_sort_val, valueTxt);
                sortedByValue.setText(valueTxt);
            }
        }

        return convertView;
    }
}
