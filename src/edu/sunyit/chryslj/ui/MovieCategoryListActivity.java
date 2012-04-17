package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.MovieCategory;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieCategoryListActivity extends ListActivity implements
        AdapterView.OnItemClickListener
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

        // Register the list for context menu events.
        registerForContextMenu(getListView());
        getListView().setOnItemClickListener(this);

        movieManagementSystem = new MovieManagementSystem(
                getApplication());

        movieManagementSystem.open();
        categories = movieManagementSystem.getAllCategories();
        movieManagementSystem.close();

        categoryAdapter = new MovieCategoryAdapter(
                this, R.layout.category_list_item, categories);
        setListAdapter(categoryAdapter);
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
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_show_delete, menu);

        AdapterView.AdapterContextMenuInfo info = null;

        try
        {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            categoryAdapter.setSelectedIndex(info.position);
        }
        catch (ClassCastException e)
        {
            Log.e(TAG, "bad menuInfo", e);
            Toast.makeText(this, "Invalid selection.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        boolean itemHandled = false;

        MovieCategory selectedCategory = categoryAdapter.getSelectedCategory();

        switch (item.getItemId())
        {
            case R.id.category_menu_show:
                showSelectedCategory(selectedCategory);
                itemHandled = true;
                break;
            case R.id.category_menu_delete:
                showDeleteConfirm(selectedCategory);
                itemHandled = true;
                break;
            default:
                itemHandled = super.onContextItemSelected(item);
                break;
        }

        return itemHandled;
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position,
            long id)
    {
        categoryAdapter.setSelectedIndex(position);

        showSelectedCategory(categoryAdapter.getSelectedCategory());
    }

    private void showSelectedCategory(MovieCategory selectedCategory)
    {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.aquired_category_info),
                selectedCategory);
        intent.setClass(getApplication(), MovieCategoryInfoActivity.class);
        startActivity(intent);
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

    private void showDeleteConfirm(final MovieCategory selectedCategory)
    {
        if (selectedCategory != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    this);

            builder.setTitle("Remove " + selectedCategory.getTitle() + "?")
                    .setMessage(
                            "Are you sure you want to remove \"" +
                                    selectedCategory.getTitle() + "\"?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    MovieCategoryListActivity.this
                                            .removeCategory(selectedCategory);
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
            Toast.makeText(this, "No category selected.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Delete the movie that is currently being displayed on this view.
     */
    private void removeCategory(MovieCategory categoryToRemove)
    {
        movieManagementSystem.open();

        StringBuilder toastMessage = new StringBuilder();
        toastMessage.append(categoryToRemove.getTitle());

        if (movieManagementSystem.removeCategory(categoryToRemove))
        {
            categoryAdapter.removeSelectedCategory();

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
        else
        {
            toastMessage.append(" was not deleted!");
            setResult(RESULT_CANCELED);
        }

        Toast.makeText(getApplication(), toastMessage.toString(),
                Toast.LENGTH_LONG).show();

        movieManagementSystem.close();
    }
}
