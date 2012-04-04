package edu.sunyit.chryslj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import edu.sunyit.chryslj.camera.BarcodeCameraActivity;

public class MainActivity extends Activity
{
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PICTURE_REQUEST = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Log.d(TAG, "Result returned to activity");
            if (requestCode == TAKE_PICTURE_REQUEST)
            {
                // imageData is the YCrCB data acquired from the preview.
                byte[] imageData = data
                        .getByteArrayExtra(getString(R.string.ycrcb_image_data));
                int width = data.getIntExtra(
                        getString(R.string.ycrcb_image_width), 0);
                int height = data.getIntExtra(
                        getString(R.string.ycrcb_image_height), 0);

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.setClass(this.getApplication(), TestBMPActivity.class);
                intent.putExtra(getString(R.string.ycrcb_image_data), imageData);
                intent.putExtra(getString(R.string.ycrcb_image_width), width);
                intent.putExtra(getString(R.string.ycrcb_image_height), height);
                startActivity(intent);
            }
        }
    }

    public void onClick(View view)
    {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        switch (view.getId())
        {
            case R.id.button1:
                intent.setClass(view.getContext(), MovieCatalogActivity.class);
                startActivity(intent);
                break;
            case R.id.button2:
                intent = new Intent();
                intent.setClass(view.getContext(), TestBMPActivity.class);
                startActivity(intent);
                break;
            case R.id.button3:
                intent = new Intent();
                intent.setClass(view.getContext(), BarcodeCameraActivity.class);
                startActivityForResult(intent, TAKE_PICTURE_REQUEST);
                break;
        }
    }

}
