package edu.sunyit.chryslj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import edu.sunyit.chryslj.ui.MovieCategoryListActivity;
import edu.sunyit.chryslj.ui.MovieListActivity;

public class MainActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onClick(View view)
    {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        switch (view.getId())
        {
            case R.id.main_movie_list:
                intent.setClass(view.getContext(), MovieListActivity.class);
                startActivity(intent);
                break;
            case R.id.main_category_list:
                intent.setClass(view.getContext(),
                        MovieCategoryListActivity.class);
                startActivity(intent);
                break;
        }
    }

}
