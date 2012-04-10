package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieListActivity extends Activity
{
    private static final String TAG = MovieListActivity.class.getName();
    private MovieManagementSystem movieManagementSystem;

    private TableLayout headerTableLayout;
    private TableLayout bodyTableLayout;

    private boolean isSorted = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list);

        // Connect to the movie management system.
        movieManagementSystem = new MovieManagementSystem(this);
        movieManagementSystem.open();

        headerTableLayout =
                (TableLayout) findViewById(R.id.movie_main_table_layout);
        String sortedBy =
                getResources().getString(R.string.movie_table_sort, "None");
        TableRow headerRow = (TableRow) headerTableLayout.getChildAt(0);
        ((TextView) headerRow.getChildAt(headerRow.getChildCount() - 1))
                .setText(sortedBy);
        bodyTableLayout =
                (TableLayout) findViewById(R.id.movie_data_table_layout);

        updateTableLayouts();
    }

    @Override
    protected void onResume()
    {
        movieManagementSystem.open();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        movieManagementSystem.close();
        super.onPause();
    }

    private void updateTableLayouts()
    {
        List<Movie> movieList = movieManagementSystem.getAllMovies();

        if (movieList.isEmpty())
        {
            findViewById(R.id.movie_list_table_container).setVisibility(
                    View.GONE);
            bodyTableLayout.removeAllViews();
            findViewById(R.id.movie_list_empty_textview).setVisibility(
                    View.VISIBLE);
        }
        else
        {
            findViewById(R.id.movie_list_empty_textview).setVisibility(
                    View.GONE);
            findViewById(R.id.movie_list_table_container).setVisibility(
                    View.VISIBLE);
            bodyTableLayout.removeAllViews();

            for (int movieIndex = 0; movieIndex < movieList.size(); movieIndex++)
            {
                Movie currentMovie = movieList.get(movieIndex);
                TableRow newTableRow =
                        new TableRow(bodyTableLayout.getContext());
                newTableRow.addView(
                        createIndexView(movieIndex + 1,
                                bodyTableLayout.getContext()), 0);
                newTableRow.addView(
                        createTitleView(currentMovie.getTitle(),
                                bodyTableLayout.getContext()), 1);
                newTableRow.addView(
                        createCategoryView(currentMovie.getPersonalRaiting(),
                                bodyTableLayout.getContext()), 2);

                if (isSorted)
                {
                    // TODO Figure out the sorted thing.
                    newTableRow.addView(
                            createSortedView(currentMovie.getTitle(),
                                    bodyTableLayout.getContext()), 3);
                }
                else
                {
                    bodyTableLayout.setColumnCollapsed(3, true);
                }

                bodyTableLayout.addView(newTableRow);
            }

            // Force a redraw.
            bodyTableLayout.invalidate();
        }
    }

    private TextView createIndexView(int index, Context context)
    {
        TextView textView = new TextView(context);
        LayoutParams params = textView.getLayoutParams();
        params.width =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                        getResources().getDisplayMetrics());
        textView.setLayoutParams(params);
        textView.setText(index);

        return textView;
    }

    private TextView createTitleView(String title, Context context)
    {
        TextView textView = new TextView(context);
        LayoutParams params = textView.getLayoutParams();
        params.width =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        60, getResources().getDisplayMetrics());
        textView.setLayoutParams(params);
        textView.setText(title);

        return textView;
    }

    private TextView createCategoryView(int personalRating, Context context)
    {
        TextView indexView = new TextView(context);
        LayoutParams params = indexView.getLayoutParams();
        params.width =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        40, getResources().getDisplayMetrics());
        indexView.setLayoutParams(params);
        indexView.setText(personalRating);

        return indexView;
    }

    private TextView createSortedView(String sorted, Context context)
    {
        TextView textView = new TextView(context);
        LayoutParams params = textView.getLayoutParams();
        params.width =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        55, getResources().getDisplayMetrics());
        textView.setLayoutParams(params);
        textView.setText(sorted);

        return textView;
    }
}
