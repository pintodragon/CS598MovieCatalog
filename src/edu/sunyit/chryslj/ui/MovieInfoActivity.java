package edu.sunyit.chryslj.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import edu.sunyit.chryslj.R;

public class MovieInfoActivity extends Activity implements
        SeekBar.OnSeekBarChangeListener
{
    private static final String TAG = MovieInfoActivity.class.getSimpleName();

    private SeekBar ratingSeekBar;
    private TextView ratingProgressText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_info);

        ratingProgressText =
                (TextView) findViewById(R.id.movie_info_rating_progress);

        ratingSeekBar = (SeekBar) findViewById(R.id.movie_info_seek_bar);
        ratingSeekBar.setOnSeekBarChangeListener(this);

        Log.d(TAG, "TextView: " + ratingProgressText.getId() + " SeekBar: " +
                ratingSeekBar.getId());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch)
    {
        ratingProgressText.setText("" + progress);
    }

    @Override
    public void onResume()
    {
        Intent intent = getIntent();
        if (intent != null)
        {
            // TODO fill in with stuff
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub

    }
}
