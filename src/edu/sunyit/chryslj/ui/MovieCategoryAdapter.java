package edu.sunyit.chryslj.ui;

import java.util.List;

import android.content.Context;
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
    private List<MovieCategory> items;

    private MovieManagementSystem movieMangementSystem;

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
                categoryCount.setText("" + movieCount);

                movieMangementSystem.close();
            }
        }

        return convertView;
    }
}
