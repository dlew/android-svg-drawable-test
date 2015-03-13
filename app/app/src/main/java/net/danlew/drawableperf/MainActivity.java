package net.danlew.drawableperf;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.telly.mrvector.MrVector;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final List<Integer> VECTOR_DRAWABLES = Arrays.asList(
        R.drawable.ic_3d_rotation_24px_vector,
        R.drawable.ic_accessibility_24px_vector,
        R.drawable.ic_account_balance_24px_vector,
        R.drawable.ic_account_balance_wallet_24px_vector,
        R.drawable.ic_account_box_24px_vector,
        R.drawable.ic_account_child_24px_vector,
        R.drawable.ic_account_circle_24px_vector,
        R.drawable.ic_add_shopping_cart_24px_vector,
        R.drawable.ic_alarm_24px_vector,
        R.drawable.ic_alarm_add_24px_vector,
        R.drawable.ic_alarm_off_24px_vector,
        R.drawable.ic_alarm_on_24px_vector
    );

    private static final List<Integer> MR_VECTOR_DRAWABLES = Arrays.asList(
        R.drawable.ic_3d_rotation_24px_mr,
        R.drawable.ic_accessibility_24px_mr,
        R.drawable.ic_account_balance_24px_mr,
        R.drawable.ic_account_balance_wallet_24px_mr,
        R.drawable.ic_account_box_24px_mr,
        R.drawable.ic_account_child_24px_mr,
        R.drawable.ic_account_circle_24px_mr,
        R.drawable.ic_add_shopping_cart_24px_mr,
        R.drawable.ic_alarm_24px_mr,
        R.drawable.ic_alarm_add_24px_mr,
        R.drawable.ic_alarm_off_24px_mr,
        R.drawable.ic_alarm_on_24px_mr
    );

    @InjectView(R.id.size_edit_text)
    EditText mSizeEditText;

    @InjectView(R.id.iterations_edit_text)
    EditText mIterationsEditText;

    @InjectView(R.id.results)
    TextView mResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
    }

    @OnClick(R.id.vector_drawable_test)
    public void testVectorDrawable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(this, "VectorDrawables are only supported in Lollipop+", Toast.LENGTH_LONG).show();
            return;
        }

        Observable<Drawable> drawableObservable =
            Observable.from(VECTOR_DRAWABLES)
                .map(new Func1<Integer, Drawable>() {
                    @Override
                    public Drawable call(Integer resId) {
                        return getResources().getDrawable(resId);
                    }
                });

        testDrawables(drawableObservable);
    }

    @OnClick(R.id.mr_vector_test)
    public void testMrVector() {
        Observable<Drawable> drawableObservable =
            Observable.from(MR_VECTOR_DRAWABLES)
                .map(new Func1<Integer, Drawable>() {
                    @Override
                    public Drawable call(Integer resId) {
                        return MrVector.inflate(getResources(), resId);
                    }
                });

        testDrawables(drawableObservable);
    }

    @OnClick(R.id.bitmap_drawable_test)
    public void testBitmap() {
        // TODO
    }

    private void testDrawables(Observable<Drawable> drawables) {
        int size = Integer.parseInt(mSizeEditText.getText().toString());
        int iterations = Integer.parseInt(mIterationsEditText.getText().toString());

        drawables.reduce(new DrawableInstrumentation(iterations, size, size),
            new Func2<DrawableInstrumentation, Drawable, DrawableInstrumentation>() {
                @Override
                public DrawableInstrumentation call(
                    DrawableInstrumentation drawableInstrumentation,
                    Drawable drawable) {
                    drawableInstrumentation.testDrawable(drawable);
                    return drawableInstrumentation;
                }
            })
            .subscribe(new Action1<DrawableInstrumentation>() {
                           @Override
                           public void call(DrawableInstrumentation drawableInstrumentation) {
                               long avg = drawableInstrumentation.getAverageNanosecondsPerRender();
                               String result = "Average render time per drawable: " + avg + " ns";
                               result += "\n\nThis works out to " + (avg / 100000) + " ms per 10 drawables rendered";
                               result += "\n\nOr... " + (avg / 10000) + " ms per 100 drawables rendered";
                               mResultsTextView.setText(result);
                           }
                       },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mResultsTextView.setText("Something went wrong during testing, check the logs.");
                        Log.e("DrawablePerf", "Something went wrong during testing", throwable);
                    }
                });
    }
}
