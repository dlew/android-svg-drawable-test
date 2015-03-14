# android-svg-drawable-test

Testing out the performance of various SVG libs:

- Android's [`VectorDrawable`](https://developer.android.com/reference/android/graphics/drawable/VectorDrawable.html)
- [Mr. Vector's](https://github.com/telly/MrVector) `VectorDrawable`
- Bitmaps pre-generated from the SVGs.
- Bitmaps w/ cached rendering.

## Analysis

The initial rendering of a `VectorDrawable` is an order of magnitude slower than the rendering of a `BitmapDrawable`. However, once the `VectorDrawable` has been rendered once, its output is cached and reused to great effect and is actually faster than the `BitmapDrawable`.

How could that be? Digging into the code for `VectorDrawable` revealed the shortcut: it pre-renders the entire `Bitmap`, so subsequent draws are very fast. `BitmapDrawable` doesn't cache its output, so it has additional processing each pass. Creating a pseudo-caching `BitmapDrawable` resulted in similar cached performance as `VectorDrawable`.

## Conclusion

`VectorDrawable` is very slow to initially render, then very fast afterwards. However, this means that each time its cache is invalidated (bounds changes or other modifications) it will be have to do another slow pass.

`BitmapDrawable` is consistent and fast (but not quite as fast as `VectorDrawable` when reused).

That said - most of the time the `Views` using the `Drawables` themselves are cached. It's not like `draw()` is called if you're scrolling a `View` in a `ListView`. So that initial render really does count.

Which you use is up to you and your use case. Either is appropriate in most circumstances.

There is no discernible performance difference between the base implementation and Mr. Vector, so use either at will.