package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieManagementSystem;
import edu.sunyit.chryslj.movie.enums.Genre;
import edu.sunyit.chryslj.movie.enums.MediaFormat;
import edu.sunyit.chryslj.movie.enums.Rating;

public class MovieListActivity extends ListActivity
{
    private static final String TAG = MovieListActivity.class.getName();
    // TODO Horrible name. Only testing at the moment
    private MovieManagementSystem movieMangSystem;

    // TODO Horrible due to possible threading issues
    private long selectedItem = -1;

    private ArrayAdapter<Movie> adapter = null;
    private ListView listView = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list);

        movieMangSystem = new MovieManagementSystem(this);
        movieMangSystem.open();

        List<Movie> values = movieMangSystem.getAllMovies();

        adapter =
                new ArrayAdapter<Movie>(this,
                        android.R.layout.simple_list_item_1, values);
        listView = (ListView) findViewById(R.id.temp_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                resetItemSelection();

                selectedItem = id;
                view.setSelected(true);
                view.setPressed(true);
                view.setBackgroundColor(Color.CYAN);
                Log.d(TAG, "Selected Id: " + id);
            }
        });
    }

    private void resetItemSelection()
    {
        for (int child = 0; child < listView.getChildCount(); child++)
        {
            View childView = listView.getChildAt(child);
            childView.setSelected(false);
            childView.setPressed(false);
            childView.setBackgroundColor(Color.BLACK);
        }
    }

    // TESTING!!!!!
    public void onClick(View view)
    {
        Log.d(TAG, "ViewId: " + R.id.delete_movie);
        switch (view.getId())
        {
            case R.id.delete_movie:
                Log.d(TAG, "Delete: " + selectedItem);
                resetItemSelection();
                movieMangSystem
                        .removeMovie(adapter.getItem((int) selectedItem));
                adapter.remove(adapter.getItem((int) selectedItem));
                break;
            case R.id.add_movie:
                Movie movie = new Movie();
                movie.setTitle("Testing" + Math.random());
                movie.setRated(Rating.G);
                movie.setGenre(Genre.COMEDY);
                movie.setPersonalRaiting(2);
                movie.setFormat(MediaFormat.DVD);
                movie.setRunTime((short) 10);
                if (movieMangSystem.addMovie(movie))
                {
                    adapter.add(movie);
                }
                break;
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.quit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume()
    {
        movieMangSystem.open();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        movieMangSystem.close();
        super.onPause();
    }
}
