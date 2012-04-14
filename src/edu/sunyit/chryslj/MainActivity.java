package edu.sunyit.chryslj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import edu.sunyit.chryslj.ui.MovieListActivity;

public class MainActivity extends Activity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // TODO Remove this line.
        Log.e(TAG, "making a filter");
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
        }
    }

}
