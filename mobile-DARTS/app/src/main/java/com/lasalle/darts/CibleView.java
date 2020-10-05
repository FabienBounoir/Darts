package com.lasalle.darts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CibleView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = "CibleView"; //!< le TAG de la classe pour les logs
    Context context;
    SurfaceHolder surfaceHolder;
    DrawingThread thread;
    Paint paint;
    Volee volee;

	public CibleView(Context context)
    {
        super(context);
        this.context = context;
        Log.d(TAG,"CibleView()");
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        //setMinimumWidth(400);
        //setMinimumHeight(400);
        surfaceHolder.setFixedSize(720, 720);
        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        //surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        volee = new Volee();
    }

    public void setVolee(Volee volee)
    {
        this.volee = volee;
    }

    public static Drawable getDrawable(Context context, String name)
    {
        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return context.getResources().getDrawable(resourceId);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
	    if(canvas == null)
	        return;
        // Dessiner le fond
        //canvas.drawColor(Color.GREEN);
        //canvas.drawColor(Color.WHITE);
        //canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);

        // Dessiner la cible
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int cibleWidth = canvasWidth;// / 2;
        int cibleHeight = canvasHeight;// / 2;
        int xOffset = cibleWidth - cibleWidth/2;
        int yOffset = cibleHeight - cibleHeight/2;

        Rect cibleRect = new Rect();
        cibleRect.set(0, 0, cibleWidth, cibleHeight);
        Bitmap cible = BitmapFactory.decodeResource(getResources(), R.drawable.cible);
        //cibleRect.offsetTo(xOffset, yOffset);
        canvas.drawBitmap(cible, null, cibleRect, paint);

        // Dessiner les impacts
        if(volee.getNbFlechettes() > 0)
        {
            for(int i = 0; i < volee.getNbFlechettes(); ++i)
            {
                String imageImpact = "impact_";
                if(volee.getTypePoint(i) != -1 && volee.getNumeroCible(i) != -1)
                {
                    imageImpact += (Integer.toString(volee.getTypePoint(i)) + "_");
                    imageImpact += Integer.toString(volee.getNumeroCible(i));
                    Bitmap impact = BitmapFactory.decodeResource(getResources(), context.getResources().getIdentifier(imageImpact, "drawable", context.getPackageName()));
                    canvas.drawBitmap(impact, null, cibleRect, paint);
                }
            }
        }
    }

    private Bitmap scale(Bitmap bitmap, int maxWidth, int maxHeight)
    {
        // Determine the constrained dimension, which determines both dimensions.
        int width;
        int height;
        float widthRatio = (float)bitmap.getWidth() / maxWidth;
        float heightRatio = (float)bitmap.getHeight() / maxHeight;
        // Width constrained.
        if (widthRatio >= heightRatio)
        {
            width = maxWidth;
            height = (int)(((float)width / bitmap.getWidth()) * bitmap.getHeight());
        }
        // Height constrained.
        else
        {
            height = maxHeight;
            width = (int)(((float)height / bitmap.getHeight()) * bitmap.getWidth());
        }
        Bitmap scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        float ratioX = (float)width / bitmap.getWidth();
        float ratioY = (float)height / bitmap.getHeight();
        float middleX = width / 2.0f;
        float middleY = height / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        //
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG,"surfaceCreated()");
        thread = new DrawingThread();
        thread.keepDrawing = true;
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder pHolder)
    {
        Log.d(TAG,"surfaceDestroyed()");
        thread.keepDrawing = false;
        boolean retry = true;
        while (retry)
        {
            try
            {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e) {}
        }
    }

    private class DrawingThread extends Thread
    {
        boolean keepDrawing = true;

        @Override
        public void run()
        {
            Canvas canvas;
            while (keepDrawing)
            {
                canvas = null;

                try
                {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder)
                    {
                        onDraw(canvas);
                    }
                }
                finally
                {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }

                // Pour dessiner à 50 fps
                try
                {
                    Thread.sleep(20);
                }
                catch (InterruptedException e) {}
            }
        }
    }
}
