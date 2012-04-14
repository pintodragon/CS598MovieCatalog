package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieListActivity extends Activity implements OnClickListener
{
    private static final String TAG = MovieListActivity.class.getSimpleName();
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
        movieManagementSystem = new MovieManagementSystem(getApplication());

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
        super.onResume();
        bodyTableLayout.removeAllViews();
        updateTableLayouts();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    private void updateTableLayouts()
    {
        movieManagementSystem.open();
        List<Movie> movieList = movieManagementSystem.getAllMovies();
        movieManagementSystem.close();

        if (movieList.isEmpty())
        {
            findViewById(R.id.movie_list_table_container).setVisibility(
                    View.INVISIBLE);
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
                TableRow newTableRow = new TableRow(getApplication());
                newTableRow.setLayoutParams(new TableRow.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                newTableRow.setBackgroundColor(
                        android.R.drawable.list_selector_background);
                newTableRow.setOnClickListener(this);
                newTableRow.setClickable(true);

                // Get the DP value of the column in the table row. 5 dp is
                // equivalent to 5 pixels.
                int dPValue = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 5, 
                        getResources().getDisplayMetrics());
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        dPValue, LayoutParams.FILL_PARENT);
                newTableRow.addView(createView(movieIndex + 1, 
                        bodyTableLayout.getContext()), 0, layoutParams);

                // Get the DP value of the column in the table row. 60 dp is
                // equivalent to 60 pixels.
                dPValue = (int) TypedValue.applyDimension( 
                        TypedValue.COMPLEX_UNIT_DIP, 60, getResources()
                                        .getDisplayMetrics());
                layoutParams = new TableRow.LayoutParams(dPValue, 
                        LayoutParams.FILL_PARENT);
                newTableRow.addView(createView(currentMovie.getTitle(), 
                        bodyTableLayout.getContext()), 1, layoutParams);

                // Get the DP value of the column in the table row. 40 dp is
                // equivalent to 40 pixels.
                dPValue = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 40, getResources()
                                        .getDisplayMetrics());
                layoutParams = new TableRow.LayoutParams(dPValue, 
                        LayoutParams.FILL_PARENT);
                newTableRow.addView(createView(
                        currentMovie.getPersonalRaiting(),
                        bodyTableLayout.getContext()), 2, layoutParams);

                if (isSorted)
                {
                    // TODO Figure out the sorted thing.
                    newTableRow.addView(createView(currentMovie.getTitle(),
                                    bodyTableLayout.getContext()), 3);
                }
                else
                {
                    headerTableLayout.setColumnCollapsed(3, true);
                    bodyTableLayout.setColumnCollapsed(3, true);
                }

                bodyTableLayout.addView(newTableRow, 
                        new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT));
            }

            // Force a redraw.
            bodyTableLayout.setVisibility(View.VISIBLE);
            bodyTableLayout.invalidate();
        }
    }

    private TextView createView(int value, Context context)
    {
        TextView textView = new TextView(context);
        int dPValue =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                        getResources().getDisplayMetrics());
        textView.setPadding(dPValue, dPValue, dPValue, dPValue);
        textView.setText("" + value);

        return textView;
    }

    private TextView createView(String value, Context context)
    {
        TextView textView = new TextView(context);
        int dPValue =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                        getResources().getDisplayMetrics());
        textView.setPadding(dPValue, dPValue, dPValue, dPValue);
        textView.setText(value);

        return textView;
    }

    /**
     * Our button handler. When a user clicks on the manual or camera add
     * buttons this method is called.
     * 
     * @param view
     */
    public void onButtonClick(View view)
    {
        Log.d(TAG, "View: " + view.getId());
        switch (view.getId())
        {
            case R.id.movie_add_manual:
                Intent intent = new Intent();
                intent.setClass(view.getContext(), MovieInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.movie_add_camera:
                intent = new Intent();
                intent.setClass(view.getContext(), CameraPreviewActivity.class);
                startActivityForResult(intent, R.id.TAKE_PICTURE_REQUEST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Log.d(TAG, "Result returned to activity");
            switch (requestCode)
            {
                case R.id.TAKE_PICTURE_REQUEST:
                    // imageData is the YCrCB data acquired from the preview.
                    byte[] imageData =
                            data.getByteArrayExtra(
                                    getString(R.string.ycrcb_image_data));
                    int width =
                            data.getIntExtra(
                                    getString(R.string.ycrcb_image_width), 0);
                    int height =
                            data.getIntExtra(
                                    getString(R.string.ycrcb_image_height), 0);

                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    intent.setClass(getApplication(),
                            BarcodeActivity.class);
                    intent.putExtra(getString(R.string.ycrcb_image_data),
                            imageData);
                    intent.putExtra(getString(R.string.ycrcb_image_width),
                            width);
                    intent.putExtra(getString(R.string.ycrcb_image_height),
                            height);
                    startActivityForResult(intent, R.id.DECODE_PICTURE);
                    break;
                case R.id.DECODE_PICTURE:
                    Movie aquiredMovie = (Movie) data.getSerializableExtra(
                            getString(R.string.aquired_movie_info));
                    if (aquiredMovie != null)
                    {
                        Intent movieInfoIntent = new Intent();
                        movieInfoIntent.putExtra(
                                getString(R.string.aquired_movie_info), 
                                aquiredMovie);
                        movieInfoIntent.setClass(getApplication(),
                                MovieInfoActivity.class);
                        startActivity(movieInfoIntent);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view instanceof TableRow)
        {
            TableRow clickedTableRow = (TableRow) view;
            view.setBackgroundColor(Color.CYAN);
            Log.d(TAG, "View: " + clickedTableRow.getChildCount());
            Log.d(TAG, "View: " +
                    ((TextView) clickedTableRow.getChildAt(0)).getText()
                            .toString());

            TextView titleView = (TextView) clickedTableRow.getChildAt(1);

            Intent intent = new Intent();
            intent.putExtra(
                    getResources().getString(R.string.movie_info_intent_title),
                    titleView.getText().toString());
            intent.setClass(getApplication(), MovieInfoActivity.class);
            startActivity(intent);
        }
    }
}
