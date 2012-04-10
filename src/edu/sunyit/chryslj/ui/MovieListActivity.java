package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieListActivity extends Activity
{
    private static final String TAG = MovieListActivity.class.getName();
    private MovieManagementSystem movieMangementSystem;

    private TableLayout headerTableLayout;
    private TableLayout bodyTableLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list);

        movieMangementSystem = new MovieManagementSystem(this);
        movieMangementSystem.open();

        List<Movie> values = movieMangementSystem.getAllMovies();
        headerTableLayout =
                (TableLayout) findViewById(R.id.movie_main_table_layout);
        String sortedBy =
                getResources().getString(R.string.movie_table_sort, "None");
        TableRow headerRow = (TableRow) headerTableLayout.getChildAt(0);
        ((TextView) headerRow.getChildAt(headerRow.getChildCount() - 1))
                .setText(sortedBy);
        bodyTableLayout =
                (TableLayout) findViewById(R.id.movie_data_table_layout);
    }

    @Override
    protected void onResume()
    {
        movieMangementSystem.open();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        movieMangementSystem.close();
        super.onPause();
    }
}
