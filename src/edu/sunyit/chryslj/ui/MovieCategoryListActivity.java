package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.MovieCategory;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieCategoryListActivity extends ListActivity implements
        OnItemClickListener
{
    private static final String TAG = MovieCategoryListActivity.class
            .getSimpleName();
    private MovieManagementSystem movieManagementSystem;

    private List<MovieCategory> categories = null;
    private MovieCategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        movieManagementSystem = new MovieManagementSystem(
                getApplication());

        movieManagementSystem.open();
        categories = movieManagementSystem.getAllCategories();
        movieManagementSystem.close();

        categoryAdapter = new MovieCategoryAdapter(
                this, R.layout.category_list_item, categories);
        setListAdapter(categoryAdapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position,
            long id)
    {
        TextView categoryTitle =
                (TextView) view.findViewById(R.id.category_title);

        if (categoryTitle != null)
        {
            movieManagementSystem.open();
            MovieCategory movieCategory =
                    movieManagementSystem.getCategory(categoryTitle.getText()
                            .toString());
            movieManagementSystem.close();

            Intent intent = new Intent();
            intent.putExtra(getString(R.string.aquired_category_info),
                    movieCategory);
            intent.setClass(getApplication(), MovieCategoryInfoActivity.class);
            startActivity(intent);
        }
    }

    public void onButtonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.category_add:
                addNewCategory();
            default:
                break;
        }
    }

    private void addNewCategory()
    {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.category_add_dialog, null);
        final EditText titleBox =
                (EditText) layout.findViewById(R.id.category_add_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);
        builder.setTitle("New Category Title:")
                .setView(layout)
                .setCancelable(true)
                .setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MovieCategoryListActivity.this.addNewCategory(titleBox
                                .getText().toString());
                    }
                })
                .setNegativeButton("Cancel",
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

    protected void addNewCategory(String categoryTitle)
    {
        if (!"".equals(categoryTitle))
        {
            Log.d(TAG, "Adding: " + categoryTitle);

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
                    categoryAdapter.add(newCategory);
                    categoryAdapter.notifyDataSetChanged();
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
}
