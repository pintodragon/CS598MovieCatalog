package edu.sunyit.chryslj.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OverlayView extends SurfaceView
{
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Point previewSize;
    private int frameCount = 0;
    
    public OverlayView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }
    
    public void setPreviewSize(Point previewSize)
    {
        this.previewSize = previewSize;
        frameCount = 0;
    }

    public void setCamera(Camera camera)
    {
        this.camera = camera;
        camera.setPreviewCallback(new PreviewCallback()
        {
            // Called by camera hardware, with preview frame
            public void onPreviewFrame(byte[] frame, Camera c)
            {
            Canvas cOver = surfaceHolder.lockCanvas(null);
            try
            {
                    // Perform overlay rendering here
                // Here, draw an incrementing number onscreen
                Paint pt = new Paint();
                pt.setColor(Color.WHITE);
                pt.setTextSize(16);
                cOver.drawText(Integer.toString(frameCount++),
                               10, 10, pt);
            }
            catch(Exception e)
            {
                // Log/trap rendering errors
            }
            finally
            {
                surfaceHolder.unlockCanvasAndPost(cOver);
            }
            }
        });
    }
}
