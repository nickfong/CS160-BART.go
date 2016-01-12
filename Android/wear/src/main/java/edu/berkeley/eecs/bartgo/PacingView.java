/**
 * Copyright (C) 2015
 * Nicholas Fong, Daiwei Liu, Krystyn Neisess, Patrick Sun, Michael Xu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 */

package edu.berkeley.eecs.bartgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * A class handling the user-pacing UI and associated frontward-facing data updates.
 */
public class PacingView extends View{
    private static final String TAG = "PacingView";
    private static final double PI = 3.1416;

    private long mDepartureTimeInMillis, mArrivalTimeInMillis, mCurrentTimeInMillis;
    private String mMinutesTillDeparture, mMinutesTillArrival;
    private long[] mBARTDepartureTimes;
    private int mCriticalDepartureIndex;
    private boolean mDidMiss = false;
    private int mCurrColorState;
    private RectF mGaugeBound = new RectF();
    private final Paint mGaugePaint = new Paint();
    private final Paint mBARTBarPaint = new Paint();
    private final Paint mPrimaryWhitePaint = new Paint();
    private final Paint mSecondaryWhitePaint = new Paint();
    private final Paint mMissTrainPaint = new Paint();




    ////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Default PacingView constructor.  Generates a PacingView in the given Context,
     * with the specified attributes.  Paint colors are set.
     *
     * @param context       The Context within which to build the PacingView.
     * @param attrs         The specified attributes.
     */
    public PacingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.v("PacingView", "constructor called");

        mCurrColorState = -1;

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

        mMissTrainPaint.setAntiAlias(true);
        mMissTrainPaint.setColor(getResources().getColor(R.color.white));
        mMissTrainPaint.setTextAlign(Paint.Align.CENTER);
    }




    ////////////////////////////////////////////////////////////////////////////////
    // OVERRIDDEN METHODS (GENERAL)
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Sets stroke widths, text sizes, and view dimension for the custom View.
     *
     * @param widthMeasureSpec      Horizontal space requirement as dictated by parent.
     * @param heightMeasureSpec     Vertical space requirement as dictated by parent.
     */
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
        mMissTrainPaint.setTextSize((float) (1.1 * u));

        setMeasuredDimension(size, size);
        mGaugeBound.set(size / 10, size / 10, (float) (size * 0.9), (float) (size * 0.9));
    }

    /**
     * Draws the custom View on the specified Canvas.
     *
     * @param canvas        The Canvas to draw upon.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "drawing UI");
        super.onDraw(canvas);

        // Draw background color
        double offset = (mDepartureTimeInMillis - mArrivalTimeInMillis) / 60000;
        if (offset > 3) {
            canvas.drawColor(getResources().getColor(R.color.dull_green));
            mCurrColorState = 0;
        } else if (offset > 0) {
            canvas.drawColor(getResources().getColor(R.color.mustard));
            mCurrColorState = 1;
        } else {
            canvas.drawColor(getResources().getColor(R.color.sunset_orange));
            mCurrColorState = 2;
        }
        float u = getWidth() / 10;

        // Draw gauge arc
        canvas.drawArc(mGaugeBound, 180, 180, false, mGaugePaint);

        // Draw BART bar on the gauge
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

        // Draw next departure time
        if (mDidMiss) {
            canvas.drawText("Train Missed", 5 * u, (float) (7.4 * u), mMissTrainPaint);
        } else {
            canvas.drawText(mMinutesTillDeparture, (float) (6.8 * u), (float) (7.4 * u), mPrimaryWhitePaint);
            canvas.drawText("BART", (float) (6.8 * u), (float) (8.2 * u), mSecondaryWhitePaint);
        }

        // Draw estimated arrival dot on the gauge
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

        // Draw estimated arrival time
        if (!mDidMiss) {
            canvas.drawText(mMinutesTillArrival, (float) (3.1 * u), (float) (7.4 * u), mPrimaryWhitePaint);
            canvas.drawText("YOU", (float) (3.2 * u), (float) (8.2 * u), mSecondaryWhitePaint);
        }
    }




    ////////////////////////////////////////////////////////////////////////////////
    // ON-SWIPE METHODS
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Updates UI to displays the previous train, if available.  Indicates swipe detection
     * success/failure, and whether the data update merits an update in background color.
     *
     * @return          Success/background color update code.
     */
    public int onSwipeDown() {
        if (mCriticalDepartureIndex > 0) {
            mCriticalDepartureIndex -= 1;
            mDepartureTimeInMillis = mBARTDepartureTimes[mCriticalDepartureIndex];
            if (updateArrivalTime(mArrivalTimeInMillis)) {
                return 2; // succeed AND color changed
            } else {
                return 1; // succeed AND color didn't change
            }
        }
        return 0; // didn't succeed
    }

    /**
     * Updates UI to displays the next train, if available.  Indicates swipe detection
     * success/failure, and whether the data update merits an update in background color.
     *
     * @return          Success/background color update code.
     */
    /* On swipe from bottom to top -> update the UI to show the next train */
    public int onSwipeUp() {
        if (mCriticalDepartureIndex + 1 < mBARTDepartureTimes.length) {
            mCriticalDepartureIndex += 1;
            mDepartureTimeInMillis = mBARTDepartureTimes[mCriticalDepartureIndex];
            if (updateArrivalTime(mArrivalTimeInMillis)) {
                return 2; // succeed AND color changed
            } else {
                return 1; // succeed AND color didn't change
            }
        }
        return 0; // didn't succeed
    }




    ////////////////////////////////////////////////////////////////////////////////
    // DATA UPDATE METHODS
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets global long[], mBARTDepartureTimes, to the given long[].  Note that this
     * does NOT update the UI.
     *
     * @param bartDepartureArray    The data with which to update mBARTDepartureTimes.
     */
    public void setDepartureTimes(long[] bartDepartureArray) {
        mBARTDepartureTimes = bartDepartureArray;
        mCriticalDepartureIndex = 0;
        mDepartureTimeInMillis = mBARTDepartureTimes[0];
    }

    /**
     * Updates the UI to display the user's newest ETA estimate.  (Note that this update
     * refreshes the ENTIRE UI.  Returns whether the background color would change
     * during this update.
     *
     * @param newEstimateInMillis       New user ETA estimate, in millis since the UNIX epoch.
     * @return                          A boolean indicating whether background color changes.
     */
    public boolean updateArrivalTime(long newEstimateInMillis) {
        mArrivalTimeInMillis = newEstimateInMillis;
        mCurrentTimeInMillis = new java.util.Date().getTime();

        // Get Minutes Till Arrival
        int temp = (int) ((mArrivalTimeInMillis - mCurrentTimeInMillis) / 60000);
        mMinutesTillArrival = String.valueOf(temp);
        if (mMinutesTillArrival.length() == 1) {
            mMinutesTillArrival = "0" + mMinutesTillArrival;
        }

        // Get Minutes Till Departure
        temp = (int) ((mDepartureTimeInMillis - mCurrentTimeInMillis) / 60000);
        if (temp < 0) {
            mDidMiss = true;
        } else {
            mDidMiss = false;
            mMinutesTillDeparture = String.valueOf(temp);
            if (mMinutesTillDeparture.length() == 1) {
                mMinutesTillDeparture = "0" + mMinutesTillDeparture;
            }
        }

        // Determine if the UI color would change
        long difference = mDepartureTimeInMillis - mArrivalTimeInMillis;
        int newColorState;
        if (difference > 180000) {
            newColorState = 0;
        } else if (difference > 0) {
            newColorState = 1;
        } else {
            newColorState = 2;
        }
        Log.d(TAG, "current color state: " + mCurrColorState);
        Log.d(TAG, "new color state: " + newColorState);

        this.invalidate();
        Log.d("PacingView", "UI invalidated");
        return (newColorState != mCurrColorState);
    }
}