package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.MovieCategory;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieCategoryListActivity extends ListActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
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
    protected void onResume()
    {
        super.onResume();

        MovieCategoryListActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Updating the adapter from UI Thread");
                categoryAdapter.notifyDataSetChanged();
            }
        });
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
            startActivityForResult(intent, R.id.CATEGORY_INFO);
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
                case R.id.CATEGORY_INFO:
                    MovieCategory categoryDeleted =
                            (MovieCategory) data
                                    .getSerializableExtra(getString(R.string.deleted_category_info));

                    if (categoryDeleted != null)
                    {
                        updateAdapter(categoryDeleted);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void updateAdapter(MovieCategory categoryDeleted)
    {
        // For some reason the list isn't updating when I delete an object.
        // Forcing an update by re adding everything to the adapter. Might be a
        // bug with the remove method of the adapter.

        // movieManagementSystem.open();
        // categories = movieManagementSystem.getAllCategories();
        // movieManagementSystem.close();
        //
        // categoryAdapter.clear();
        //
        // for (int index = 0; index < categories.size(); index++)
        // {
        // categoryAdapter.add(categories.get(index));
        // }
        //
        // categoryAdapter.notifyDataSetChanged();
        Log.d(TAG, "Updating the adapter: " + categoryDeleted.getId());

        categoryAdapter.remove(categoryDeleted.getId());

        MovieCategoryListActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Updating the adapter from UI Thread");
                categoryAdapter.notifyDataSetChanged();
            }
        });
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

                long insertId = movieManagementSystem.addCategory(newCategory);
                if (insertId != -1)
                {
                    toastText.append("added successfully!");
                    newCategory.setId((int) insertId);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id)
    {
        // TODO Auto-generated method stub
        Log.d(TAG, "Display menu here");
        Toast.makeText(this, "Display Menu Here", Toast.LENGTH_SHORT);
        return false;
    }
}
