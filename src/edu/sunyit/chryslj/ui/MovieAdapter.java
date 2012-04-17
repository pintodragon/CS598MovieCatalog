package edu.sunyit.chryslj.ui;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;

public class MovieAdapter extends ArrayAdapter<Movie>
{
    private static final String TAG = MovieAdapter.class.getSimpleName();
    private List<Movie> items;
    private String sortedBy;
    private Context context;

    private int selectedIndex = -1;
    private boolean colorHightlighted = true;

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

    public void setSelectedIndex(int selectedIndex)
    {
        Log.d(TAG, "SelectedIndex: " + selectedIndex);
        this.selectedIndex = selectedIndex;
    }

    public void setColorHightlighted(boolean colorHightlighted)
    {
        this.colorHightlighted = colorHightlighted;
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

            Log.d(TAG, "SelectedIndex: " + selectedIndex + " Position: " +
                    position);
        }

        if (colorHightlighted)
        {
            if (selectedIndex == position)
            {
                convertView.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.selected));
            }
            else
            {
                convertView.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.not_selected));
            }
        }

        Movie currentMovie = items.get(position);

        if (currentMovie != null)
        {
            TextView movieTitle =
                    (TextView) convertView
                            .findViewById(R.id.movie_list_item_title_val);
            if (movieTitle != null)
            {
                String title = currentMovie.getTitle();
                movieTitle.setText(title);
            }

            TextView movieRated =
                    (TextView) convertView
                            .findViewById(R.id.movie_list_item_rated_val);
            if (movieRated != null)
            {
                String rated = currentMovie.getRated().toString();
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
                sortedByValue.setText(valueTxt);
            }
        }

        return convertView;
    }

    /**
     * Check if the movie we want to add is already in the list.
     * 
     * @param addedMovie
     * @return
     */
    public boolean hasMovie(Movie addedMovie)
    {
        boolean hasMovie = false;

        for (int index = 0; index < items.size(); index++)
        {
            if (addedMovie.getId() == items.get(index).getId())
            {
                Log.d(TAG, "Found the movie: " + items.get(index).getId());
                hasMovie = true;
                break;
            }
        }

        return hasMovie;
    }

    public void removeSelectedMovie()
    {
        if (selectedIndex != -1)
        {
            remove(getSelectedMovie());
            selectedIndex = -1;
        }
    }

    public Movie getSelectedMovie()
    {
        Movie movie = null;

        if (selectedIndex != -1)
        {
            try
            {
                movie = items.get(selectedIndex);
            }
            catch (IndexOutOfBoundsException iobe)
            {
                // Movie wasn't valid so still return null.
            }
        }

        return movie;
    }
}
