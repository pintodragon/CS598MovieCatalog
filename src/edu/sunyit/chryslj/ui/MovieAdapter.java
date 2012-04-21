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

/**
 * An extension of the ArrayAdapter to handle Movies. This is used by our
 * ListView to display the Movies.
 * 
 * @author Justin Chrysler
 * 
 */
public class MovieAdapter extends ArrayAdapter<Movie>
{
    private static final String TAG = MovieAdapter.class.getSimpleName();
    private List<Movie> items;
    private String sortedBy;
    private Context context;

    private int selectedIndex = -1;
    private boolean colorHightlighted = true;

    /**
     * Creates a new MovieAdapter.
     * 
     * @param context
     *            application context to use.
     * @param textViewResourceId
     *            the ResourceID of the layout to use for each item.
     * @param items
     *            the underlying List to be used to store the movie information.
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

    /**
     * An item was clicked so set it as selected.
     * 
     * @param selectedIndex
     *            the index of the item clicked.
     */
    public void setSelectedIndex(int selectedIndex)
    {
        Log.d(TAG, "SelectedIndex: " + selectedIndex);
        this.selectedIndex = selectedIndex;
    }

    /**
     * Should we hightlight the selected item?
     * 
     * @param colorHightlighted
     *            wheter to highlight or not.
     */
    public void setColorHightlighted(boolean colorHightlighted)
    {
        this.colorHightlighted = colorHightlighted;
    }

    /**
     * Set the sort by value.
     * 
     * @param sortedBy
     *            the sort by value.
     */
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
                    valueTxt = String.valueOf(currentMovie.getPersonalRating());
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
                    valueTxt = String.valueOf(currentMovie.getRunTime());
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
     *            the movie we would like to add to the list.
     * @return whether the movie is already in the list or not.
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

    /**
     * Remove a previously selected Movie only if one was selected previously.
     */
    public void removeSelectedMovie()
    {
        if (selectedIndex != -1)
        {
            remove(getSelectedMovie());
            selectedIndex = -1;
        }
    }

    /**
     * Get the currently selected Movie.
     * 
     * @return the selected Movie or null if one wasn't selected.
     */
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

    /**
     * Update information about a movie that is already in the list.
     * 
     * @param addedMovie
     *            the movie with updated information.
     */
    public void updateMovie(Movie addedMovie)
    {
        for (int index = 0; index < items.size(); index++)
        {
            if (addedMovie.getId() == items.get(index).getId())
            {
                items.set(index, addedMovie);
                break;
            }
        }
    }
}
