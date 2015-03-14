/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.danlew.drawableperf;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * A version of BitmapDrawable that caches its output.
 *
 * DO NOT USE this for production! It doesn't really work at all!
 * It's just testing how much faster a BitmapDrawable would be if we
 * didn't have to do the complicated draw() routine each pass.
 */
public class FastBitmapDrawable extends BitmapDrawable {

    Bitmap mCachedBitmap;
    private boolean mCached = false;

    public FastBitmapDrawable(Resources res, BitmapDrawable source) {
        super(res, source.getBitmap());
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

        mCached = false;
    }

    @Override
    public Drawable mutate() {
        // Lazy mutate!
        mCached = false;

        return super.mutate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mCached) {
            mCachedBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas cachedCanvas = new Canvas(mCachedBitmap);
            super.draw(cachedCanvas);
            mCached = true;
        }

        canvas.drawBitmap(mCachedBitmap, 0, 0, getPaint());
    }
}
