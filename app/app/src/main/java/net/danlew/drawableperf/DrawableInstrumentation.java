package net.danlew.drawableperf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * Tests how long it takes drawables to render.
 */
public class DrawableInstrumentation {

    private final int mNumTestsPerDrawable;

    private final Canvas mCanvas;

    private long mTotalTime = 0;
    private int mNumRenders = 0;

    public DrawableInstrumentation(int numTestsPerDrawable, int width, int height) {
        mNumTestsPerDrawable = numTestsPerDrawable;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
    }

    public void testDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, mCanvas.getWidth(), mCanvas.getHeight());

        long start = System.nanoTime();
        for (int a = 0; a < mNumTestsPerDrawable; a++) {
            drawable.draw(mCanvas);
        }
        long end = System.nanoTime();

        mTotalTime += end - start;
        mNumRenders += mNumTestsPerDrawable;
    }

    public long getAverageNanosecondsPerRender() {
        return mTotalTime / mNumRenders;
    }
}
