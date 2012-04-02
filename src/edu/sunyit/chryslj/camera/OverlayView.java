package edu.sunyit.chryslj.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// TODO Make this class not use the preview call back. Instead it just draws.
public class OverlayView extends SurfaceView
{
    private static final String TAG = OverlayView.class.getName();

    private SurfaceHolder surfaceHolder;
    private Point previewSize;

    public OverlayView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    public void setPreviewSize(Point previewSize)
    {
        this.previewSize = previewSize;
    }

    public void setCamera(Camera camera)
    {
        camera.setPreviewCallback(new PreviewCallback()
        {
            // Called by camera hardware, with preview frame
            @Override
            public void onPreviewFrame(byte[] frame, Camera c)
            {
                Canvas canvasOverlay = surfaceHolder.lockCanvas(null);
                try
                {
                    int recX1 = 70;
                    int recX2 = previewSize.x - recX1;
                    int recY1 = 40;
                    int recY2 = previewSize.y - recY1;
                    Log.d(TAG, "preview x: " + previewSize.x + " preview y: " +
                            previewSize.y);
                    Rect guide = new Rect(recX1, recY1, recX2, recY2);
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    paint.setStrokeWidth(2);
                    paint.setStyle(Paint.Style.STROKE);
                    canvasOverlay.drawRect(guide, paint);

                    paint.setColor(Color.MAGENTA);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(4);
                    canvasOverlay.drawLine(70, previewSize.y / 2,
                            previewSize.x - 70, previewSize.y / 2, paint);
                }
                catch (Exception e)
                {
                    // Log/trap rendering errors
                }
                finally
                {
                    surfaceHolder.unlockCanvasAndPost(canvasOverlay);
                }
            }
        });
    }
}
