package edu.sunyit.chryslj.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.MovieCategory;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieCategoryInfoActivity extends Activity
{
    private static final String TAG = MovieCategoryInfoActivity.class
            .getSimpleName();
    public static final int TAKE_PICTURE_REQUEST = 0;

    private MovieManagementSystem movieMangementSystem;

    private MovieCategory movieCategory = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_info);

        movieMangementSystem = new MovieManagementSystem(
                getApplication());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null)
        {
            movieCategory =
                    (MovieCategory) intent
                            .getSerializableExtra(getString(R.string.aquired_category_info));
            if (movieCategory != null)
            {
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     * This method is called when a button on this activity is pressed.
     * 
     * @param view
     */
    public void onButtonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.category_info_add:
                // TODO Add a movie from those already in the system.
                break;
            case R.id.category_info_cancel:
                finish();
                break;
            case R.id.category_info_delete:
                showConfirmDialog();
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * Delete the movie that is currently being displayed on this view.
     */
    private void deleteCategory()
    {
        if (movieCategory != null)
        {
            movieMangementSystem.open();

            StringBuilder toastMessage = new StringBuilder();
            toastMessage.append(movieCategory.getTitle());

            if (movieMangementSystem.removeCategory(movieCategory))
            {
                toastMessage.append(" has been deleted!");
            }
            else
            {
                toastMessage.append(" was not deleted!");
            }

            Toast.makeText(getApplication(), toastMessage.toString(),
                    Toast.LENGTH_LONG).show();

            movieMangementSystem.close();
        }
    }

    /**
     * Show the confirm dialog on whether the movie selected should be deleted
     * or not.
     */
    private void showConfirmDialog()
    {
        if (movieCategory != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    this);

            builder.setMessage(
                    "Are you sure you want to delete \"" +
                            MovieCategoryInfoActivity.this.movieCategory
                                    .getTitle() + "\"?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    MovieCategoryInfoActivity.this
                                            .deleteCategory();
                                    MovieCategoryInfoActivity.this.finish();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    dialog.cancel();
                                }
                            });

            builder.create().show();
        }
        else
        {
            Toast.makeText(getApplication(),
                    "You can not delete that which does not exist!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
