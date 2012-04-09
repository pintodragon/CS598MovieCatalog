package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieListActivity extends ListActivity
{
    private static final String TAG = MovieListActivity.class.getName();
    private MovieManagementSystem movieMangementSystem;

    private ArrayAdapter<Movie> adapter = null;
    private ListView listView = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        movieMangementSystem = new MovieManagementSystem(this);
        movieMangementSystem.open();

        List<Movie> values = movieMangementSystem.getAllMovies();

        adapter =
                new ArrayAdapter<Movie>(this,
                        android.R.layout.simple_list_item_1, values);
        listView = getListView();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
            }
        });
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
