package edu.sunyit.chryslj;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieManagementSystem;
import edu.sunyit.chryslj.movie.enums.Genre;
import edu.sunyit.chryslj.movie.enums.MediaFormat;
import edu.sunyit.chryslj.movie.enums.Rating;

public class MovieCatalogActivity extends ListActivity
{
	// TODO Horrible name. Only testsing at the moment
	private MovieManagementSystem movieMangSystem;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		movieMangSystem = new MovieManagementSystem(this);
		movieMangSystem.open();

		List<Movie> values = movieMangSystem.getAllMovies();

		ArrayAdapter<Movie> adapter = new ArrayAdapter<Movie>(this,
		        android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	// TESTING!!!!!
	public void onClick(View view)
	{
		@SuppressWarnings("unchecked")
		ArrayAdapter<Movie> adapter = (ArrayAdapter<Movie>) getListAdapter();
		Movie movie = new Movie();
		movie.setTitle("Testing");
		movie.setRated(Rating.G);
		movie.setGenre(Genre.COMEDY);
		movie.setPersonalRaiting(2);
		movie.setFormat(MediaFormat.DVD);
		movie.setRunTime((short) 10);

		System.out.println("View id: " + view.getId());
		switch (view.getId())
		{
			case R.id.add:
				movie = movieMangSystem.createMovie(movie);
				adapter.add(movie);
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