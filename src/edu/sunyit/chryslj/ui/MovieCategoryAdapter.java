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
import edu.sunyit.chryslj.movie.MovieCategory;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

/**
 * An extension of the ArrayAdapter to handle Categories. This is used by our
 * ListView to display the Categories.
 * 
 * @author Justin Chrysler
 * 
 */
public class MovieCategoryAdapter extends ArrayAdapter<MovieCategory>
{
    private static final String TAG = MovieCategoryAdapter.class
            .getSimpleName();
    private List<MovieCategory> items;

    private MovieManagementSystem movieMangementSystem;

    private int selectedIndex = -1;

    /**
     * Creates a new MovieCategoryAdapter.
     * 
     * @param context
     *            application context to use.
     * @param textViewResourceId
     *            the ResourceID of the layout to use for each item.
     * @param items
     *            the underlying List to be used to store the Category
     *            information.
     */
    public MovieCategoryAdapter(Context context, int textViewResourceId,
                                List<MovieCategory> items)
    {
        super(context, textViewResourceId, items);
        this.items = items;

        movieMangementSystem = new MovieManagementSystem(
                context);
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
                    layoutInflater.inflate(R.layout.category_list_item, null);
        }

        MovieCategory currentMovieCategory = items.get(position);

        if (currentMovieCategory != null)
        {
            TextView categoryTitle =
                    (TextView) convertView.findViewById(R.id.category_title);
            if (categoryTitle != null)
            {
                categoryTitle.setText(currentMovieCategory.getTitle());
            }

            TextView categoryCount =
                    (TextView) convertView.findViewById(R.id.category_count);
            if (categoryCount != null)
            {
                movieMangementSystem.open();
                int movieCount =
                        movieMangementSystem
                                .getNumMoviesInCategory(currentMovieCategory
                                        .getId());
                movieMangementSystem.close();

                categoryCount.setText(String.valueOf(movieCount));
            }
        }

        return convertView;
    }

    /**
     * An item was clicked so set it as selected.
     * 
     * @param selectedIndex
     *            the index of the item clicked.
     */
    public void setSelectedIndex(int selectedIndex)
    {
        this.selectedIndex = selectedIndex;
    }

    /**
     * Get the previously selected Category.
     * 
     * @return the previously selected Category or null if one was not selected.
     */
    public MovieCategory getSelectedCategory()
    {
        MovieCategory category = null;

        if (selectedIndex != -1)
        {
            try
            {
                category = items.get(selectedIndex);
            }
            catch (IndexOutOfBoundsException iobe)
            {
                // Movie wasn't valid so still return null.
            }
        }

        return category;
    }

    /**
     * Remove a selected Category if one was selected.
     */
    public void removeSelectedCategory()
    {
        if (selectedIndex != -1)
        {
            items.remove(selectedIndex);
        }

        selectedIndex = -1;
    }

    /**
     * Overloaded remove (ArrayAdapter has a remove as well) to remove a
     * Category based on it's ID.
     * 
     * @param categoryId
     *            the ID of the Category to remove.
     */
    public void remove(int categoryId)
    {
        for (int index = 0; index < items.size(); index++)
        {
            if (categoryId == items.get(index).getId())
            {
                Log.d(TAG, "Category to remove: " + items.get(index).getTitle());
                remove(items.get(index));
                break;
            }
        }
    }
}
