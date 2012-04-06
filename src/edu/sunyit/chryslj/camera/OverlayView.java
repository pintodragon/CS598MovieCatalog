package edu.sunyit.chryslj.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class OverlayView extends SurfaceView
{
    private static final String TAG = OverlayView.class.getSimpleName();

    // Smaller guide. Algorithms tend to work better with a smaller barcode
    // image.
    public static final int X_OFFSET = 190;
    public static final int Y_OFFSET = 75;

    private Point previewSize = new Point(800, 480);

    public OverlayView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        Log.d(TAG, "Overlay Created!");
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        Log.d(TAG, "On Draw!");
        int recX1 = X_OFFSET;
        int recX2 = previewSize.x - X_OFFSET;
        int recY1 = Y_OFFSET;
        int recY2 = previewSize.y - Y_OFFSET;
        Log.d(TAG, "preview x: " + previewSize.x + " preview y: " +
                previewSize.y);
        Rect guide = new Rect(recX1, recY1, recX2, recY2);
        Paint paint = new Paint();
        paint.setAlpha(0);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(guide, paint);

        paint.setColor(Color.MAGENTA);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        canvas.drawLine(X_OFFSET, previewSize.y / 2, previewSize.x - X_OFFSET,
                previewSize.y / 2, paint);
    }

    /**
     * Called any time the surface changes on the camera.
     * 
     * @param previewSize
     */
    public void setPreviewSize(Point previewSize)
    {
        Log.d(TAG, "Preview Size set!");
        this.previewSize = previewSize;
    }
}
