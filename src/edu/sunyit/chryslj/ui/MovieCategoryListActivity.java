package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.MovieCategory;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieCategoryListActivity extends Activity implements
        OnClickListener
{
    private static final String TAG = MovieCategoryListActivity.class
            .getSimpleName();
    private MovieManagementSystem movieManagementSystem;

    private TableLayout bodyTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        movieManagementSystem = new MovieManagementSystem(
                getApplication());
        bodyTableLayout =
                (TableLayout) findViewById(R.id.category_data_table_layout);

        updateTableLayouts();
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

            movieManagementSystem.open();
            MovieCategory movieCategory =
                    movieManagementSystem.getCategory(titleView.getText()
                            .toString());
            movieManagementSystem.close();

            // TODO Should do a check to make sure the movie isn't null.
            Intent intent = new Intent();
            intent.putExtra(getString(R.string.aquired_category_info),
                    movieCategory);
            intent.setClass(getApplication(), MovieCategoryInfoActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateTableLayouts();
    }

    public void onButtonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.category_add:
                addNewCategory();
                updateTableLayouts();
            default:
                break;
        }
    }

    private void addNewCategory()
    {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.category_add_dialog, null);
        // (ViewGroup) findViewById(R.id.category_add_dialog));
        final EditText titleBox =
                (EditText) layout.findViewById(R.id.category_add_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);
        builder.setView(layout);
        builder.setCancelable(true);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                MovieCategoryListActivity.this.addNewCategory(titleBox
                        .getText().toString());
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    protected void addNewCategory(String categoryTitle)
    {
        if (!"".equals(categoryTitle))
        {
            movieManagementSystem.open();
            MovieCategory newCategory =
                    movieManagementSystem.getCategory(categoryTitle);

            StringBuilder toastText = new StringBuilder();
            toastText.append(categoryTitle + " ");

            if (newCategory == null)
            {
                newCategory = new MovieCategory();
                newCategory.setTitle(categoryTitle);
                if (movieManagementSystem.addCategory(newCategory))
                {
                    toastText.append("added successfully!");
                }
                else
                {
                    toastText.append("was not added!");
                }
            }
            else
            {
                toastText.append(" already exists!");
            }
            Toast.makeText(getApplication(), toastText.toString(),
                    Toast.LENGTH_LONG).show();

            movieManagementSystem.close();
        }
    }

    private void updateTableLayouts()
    {
        movieManagementSystem.open();
        List<MovieCategory> categoryList =
                movieManagementSystem.getAllCategories();
        movieManagementSystem.close();

        // Clear all entries.
        bodyTableLayout.removeAllViews();

        for (int categoryIndex = 0; categoryIndex < categoryList.size(); categoryIndex++)
        {
            MovieCategory currentCategory = categoryList.get(categoryIndex);
            Log.d(TAG, "Adding category: " + currentCategory);

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
        bodyTableLayout.postInvalidate();
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
