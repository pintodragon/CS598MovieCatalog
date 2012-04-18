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

public class MovieCategoryAdapter extends ArrayAdapter<MovieCategory>
{
    private static final String TAG = MovieCategoryAdapter.class
            .getSimpleName();
    private List<MovieCategory> items;

    private MovieManagementSystem movieMangementSystem;

    private int selectedIndex = -1;

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

    public void setSelectedIndex(int selectedIndex)
    {
        this.selectedIndex = selectedIndex;
    }

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

    public void removeSelectedCategory()
    {
        if (selectedIndex != -1)
        {
            items.remove(selectedIndex);
        }

        selectedIndex = -1;
    }

    public void remove(int id)
    {
        for (int index = 0; index < items.size(); index++)
        {
            if (id == items.get(index).getId())
            {
                Log.d(TAG, "Category to remove: " + items.get(index).getTitle());
                remove(items.get(index));
                break;
            }
        }
    }
}
