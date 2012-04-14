package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.MovieCategory;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieCategoryListActivity extends Activity implements
        OnClickListener
{
    private static final String TAG = MovieCategoryListActivity.class
            .getSimpleName();
    private MovieManagementSystem movieManagementSystem;

    private TableLayout headerTableLayout;
    private TableLayout bodyTableLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        movieManagementSystem = new MovieManagementSystem(
                getApplication());

        headerTableLayout =
                (TableLayout) findViewById(R.id.category_main_table_layout);
        bodyTableLayout =
                (TableLayout) findViewById(R.id.category_data_table_layout);

        updateTableLayouts();
    }

    @Override
    public void onClick(View arg0)
    {
        // TODO Auto-generated method stub

    }

    public void onButtonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.category_add:

                break;
            default:
                break;
        }
    }

    private void updateTableLayouts()
    {
        movieManagementSystem.open();
        List<MovieCategory> categoryList =
                movieManagementSystem.getAllCategories();

        // Clear all entries.
        bodyTableLayout.removeAllViews();

        for (int categoryIndex = 0; categoryIndex < categoryList.size(); categoryIndex++)
        {
            MovieCategory currentCategory = categoryList.get(categoryIndex);
            movieManagementSystem.open();
            int movieCount =
                    movieManagementSystem
                            .getNumMoviesInCategory(currentCategory.getId());
            movieManagementSystem.close();

            TableRow newTableRow = new TableRow(
                    getApplication());
            newTableRow.setLayoutParams(new TableRow.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            newTableRow
                    .setBackgroundColor(android.R.drawable.list_selector_background);
            newTableRow.setOnClickListener(this);
            newTableRow.setClickable(true);

            // Get the DP value of the column in the table row. 5 dp is
            // equivalent to 5 pixels.
            int dPValue =
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
                                    .getDisplayMetrics());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                    dPValue, LayoutParams.FILL_PARENT);
            newTableRow
                    .addView(
                            createView(categoryIndex + 1,
                                    bodyTableLayout.getContext()), 0,
                            layoutParams);

            // Get the DP value of the column in the table row. 150 dp is
            // equivalent to 150 pixels.
            dPValue =
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 150, getResources()
                                    .getDisplayMetrics());
            layoutParams = new TableRow.LayoutParams(
                    dPValue, LayoutParams.FILL_PARENT);
            newTableRow.addView(
                    createView(currentCategory.getTitle(),
                            bodyTableLayout.getContext()), 1, layoutParams);

            // Get the DP value of the column in the table row. 5 dp is
            // equivalent to 5 pixels.
            dPValue =
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
                                    .getDisplayMetrics());
            layoutParams = new TableRow.LayoutParams(
                    dPValue, LayoutParams.FILL_PARENT);
            newTableRow.addView(
                    createView(movieCount, bodyTableLayout.getContext()), 2,
                    layoutParams);

            bodyTableLayout.addView(newTableRow, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }

        // Force a redraw.
        bodyTableLayout.setVisibility(View.VISIBLE);
        bodyTableLayout.invalidate();
    }

    private TextView createView(int value, Context context)
    {
        TextView textView = new TextView(
                context);
        int dPValue =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                        getResources().getDisplayMetrics());
        textView.setPadding(dPValue, dPValue, dPValue, dPValue);
        textView.setText("" + value);

        return textView;
    }

    private TextView createView(String value, Context context)
    {
        TextView textView = new TextView(
                context);
        int dPValue =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                        getResources().getDisplayMetrics());
        textView.setPadding(dPValue, dPValue, dPValue, dPValue);
        textView.setText(value);

        return textView;
    }
}
