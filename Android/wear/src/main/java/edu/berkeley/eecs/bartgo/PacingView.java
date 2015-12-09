package edu.berkeley.eecs.bartgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PacingView extends View{
    private static final String TAG = "PacingView";
    private static final double PI = 3.1416;

    private long mDepartureTimeInMillis, mArrivalTimeInMillis, mCurrentTimeInMillis;
    private String mMinutesTillDeparture, mMinutesTillArrival;
    private long[] mBARTDepartureTimes;
    private int mCriticalDepartureIndex;
    private RectF mGaugeBound = new RectF();
    private final Paint mGaugePaint = new Paint();
    private final Paint mBARTBarPaint = new Paint();
    private final Paint mPrimaryWhitePaint = new Paint();
    private final Paint mSecondaryWhitePaint = new Paint();

    public PacingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.v("PacingView", "constructor called");

        mGaugePaint.setAntiAlias(true);
        mGaugePaint.setColor(getResources().getColor(R.color.white_50));
        mGaugePaint.setStyle(Paint.Style.STROKE);

        mBARTBarPaint.setAntiAlias(true);
        mBARTBarPaint.setColor(getResources().getColor(R.color.white_50));

        mPrimaryWhitePaint.setAntiAlias(true);
        mPrimaryWhitePaint.setColor(getResources().getColor(R.color.white));
        mBARTBarPaint.setStyle(Paint.Style.FILL);
        mPrimaryWhitePaint.setTextAlign(Paint.Align.CENTER);

        mSecondaryWhitePaint.setAntiAlias(true);
        mSecondaryWhitePaint.setColor(getResources().getColor(R.color.white));
        mSecondaryWhitePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v("PacingView", "on measure");
        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = Math.max(minW, MeasureSpec.getSize(widthMeasureSpec));
        int minH = getSuggestedMinimumHeight() + getPaddingBottom() + getPaddingTop();
        int h = Math.max(MeasureSpec.getSize(heightMeasureSpec), minH);
        int size = Math.min(w, h);
        float u = size / 10;

        mGaugePaint.setStrokeWidth(u / 7);
        mBARTBarPaint.setStrokeWidth(u / 4);
        mPrimaryWhitePaint.setStrokeWidth(u / 4);
        mPrimaryWhitePaint.setTextSize((float) (2.9 * u));
        mSecondaryWhitePaint.setTextSize((float) (0.8 * u));

        setMeasuredDimension(size, size);
        mGaugeBound.set(size / 10, size / 10, (float) (size * 0.9), (float) (size * 0.9));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "drawing UI");
        super.onDraw(canvas);

        /* draw background color*/
        double offset = (mDepartureTimeInMillis - mArrivalTimeInMillis) / 60000;
        if (offset > 3) {
            canvas.drawColor(getResources().getColor(R.color.dull_green));
        } else if (offset > 0) {
            canvas.drawColor(getResources().getColor(R.color.mustard));
        } else {
            canvas.drawColor(getResources().getColor(R.color.sunset_orange));
        }
        float u = getWidth() / 10;

        /* draw gauge arc */
        canvas.drawArc(mGaugeBound, 180, 180, false, mGaugePaint);

        /* draw BART bar on the gauge */
        for (int i = 0; i < mBARTDepartureTimes.length; i++) {
            if (i == mCriticalDepartureIndex) continue;
            double diff = (mDepartureTimeInMillis - mBARTDepartureTimes[i]) / 60000;
            if (diff > 10 || diff < -10) continue;
            double beta = diff * PI / 20;
            double sin = java.lang.Math.sin(beta);
            double cos = java.lang.Math.cos(beta);
            float startX = (float) ((5 - 4 * sin) * u);
            float startY = (float) ((5 - 4 * cos) * u);
            float endX = (float) ((5 - 3 * sin) * u);
            float endY = (float) ((5 - 3 * cos) * u);
            canvas.drawLine(startX, startY, endX, endY, mBARTBarPaint);
        }
        canvas.drawLine(5 * u, u / 2, 5 * u, 2 * u, mPrimaryWhitePaint);

        /* draw next departure time */
        canvas.drawText(mMinutesTillDeparture, (float) (6.8 * u), (float) (7.4 * u), mPrimaryWhitePaint);
        canvas.drawText("BART", (float) (6.8 * u), (float) (8.2 * u), mSecondaryWhitePaint);

        /* draw estimated arrival dot on the gauge */
        double alpha = offset * PI / 20;
        float x = (float) ((5 - 4 * java.lang.Math.sin(alpha)) * u);
        float y = (float) ((5 - 4 * java.lang.Math.cos(alpha)) * u);
        if (offset > 10) {
            x = u;
            y = 5 * u;
        } else if (-10 > offset) {
            x = 9 * u;
            y = 5 * u;
        }
        canvas.drawCircle(x, y, u / 2, mPrimaryWhitePaint);

        /* draw estimated arrival time */
        canvas.drawText(mMinutesTillArrival, (float) (3.1 * u), (float) (7.4 * u), mPrimaryWhitePaint);
        canvas.drawText("YOU", (float) (3.2 * u), (float) (8.2 * u), mSecondaryWhitePaint);
    }

    /* --- The methods below are useful for Patrick --- */

    /* Set up the UI with a sequence of BART departures
     * DOSE NOT UPDATE UI */
    public void setDepartureTimes(long[] bartDepartureArray) {
        mBARTDepartureTimes = bartDepartureArray;
        mCriticalDepartureIndex = 0;
        mDepartureTimeInMillis = mBARTDepartureTimes[0];
    }

    /* Update the UI to show a newer estimate of the user's arrival time
     * newEstimateInMillis: user's new estimate arrival time in milliseconds since UNIX epoch
     * REFRESH THE ENTIRE UI */
    public void updateArrivalTime(long newEstimateInMillis) {
        mArrivalTimeInMillis = newEstimateInMillis;
        mCurrentTimeInMillis = new java.util.Date().getTime();

        /* get Minutes Till Arrival */
        int temp = (int) ((mArrivalTimeInMillis - mCurrentTimeInMillis) / 60000);
        mMinutesTillArrival = String.valueOf(temp);
        if (mMinutesTillArrival.length() == 1) {
            mMinutesTillArrival = "0" + mMinutesTillArrival;
        }

        /* get Minutes Till Departure */
        temp = (int) ((mDepartureTimeInMillis - mCurrentTimeInMillis) / 60000);
        mMinutesTillDeparture = String.valueOf(temp);
        if (mMinutesTillDeparture.length() == 1) {
            mMinutesTillDeparture = "0" + mMinutesTillDeparture;
        }

        this.invalidate();
        Log.d("PacingView", "UI invalidated");
    }

    /* On swipe from top to bottom -> update the UI to show the previous train */
    public boolean onSwipeDown() {
        if (mCriticalDepartureIndex > 0) {
            mCriticalDepartureIndex -= 1;
            mDepartureTimeInMillis = mBARTDepartureTimes[mCriticalDepartureIndex];
            updateArrivalTime(mArrivalTimeInMillis);
            return true;
        }
        return false;
    }

    /* On swipe from bottom to top -> update the UI to show the next train */
    public boolean onSwipeUp() {
        if (mCriticalDepartureIndex + 1 < mBARTDepartureTimes.length) {
            mCriticalDepartureIndex += 1;
            mDepartureTimeInMillis = mBARTDepartureTimes[mCriticalDepartureIndex];
            updateArrivalTime(mArrivalTimeInMillis);
            return true;
        }
        return false;
    }
}