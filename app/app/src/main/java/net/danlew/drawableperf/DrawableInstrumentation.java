package net.danlew.drawableperf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import rx.functions.Func0;

/**
 * Tests how long it takes drawables to render.
 */
public class DrawableInstrumentation {

    private final int mNumTestsPerDrawable;

    private final Canvas mCanvas;

    private final boolean mReuseDrawables;

    private long mTotalTime = 0;
    private int mNumRenders = 0;

    public DrawableInstrumentation(int numTestsPerDrawable, int size, boolean reuseDrawables) {
        mNumTestsPerDrawable = numTestsPerDrawable;

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);

        mReuseDrawables = reuseDrawables;
    }

    public void testDrawable(Func0<Drawable> drawableGenerator) {
        Drawable drawable = null;

        if (mReuseDrawables) {
            drawable = drawableGenerator.call();
            drawable.setBounds(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
        }

        long start = System.nanoTime();
        for (int a = 0; a < mNumTestsPerDrawable; a++) {
            if (!mReuseDrawables) {
                drawable = drawableGenerator.call();
                drawable.setBounds(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
            }

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
