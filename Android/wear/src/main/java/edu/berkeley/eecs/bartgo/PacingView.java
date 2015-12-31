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

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "drawing UI");
        super.onDraw(canvas);

        /* draw background color*/
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
        if (mDidMiss) {
            canvas.drawText("Train Missed", 5 * u, (float) (7.4 * u), mMissTrainPaint);
        } else {
            canvas.drawText(mMinutesTillDeparture, (float) (6.8 * u), (float) (7.4 * u), mPrimaryWhitePaint);
            canvas.drawText("BART", (float) (6.8 * u), (float) (8.2 * u), mSecondaryWhitePaint);
        }

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
        if (!mDidMiss) {
            canvas.drawText(mMinutesTillArrival, (float) (3.1 * u), (float) (7.4 * u), mPrimaryWhitePaint);
            canvas.drawText("YOU", (float) (3.2 * u), (float) (8.2 * u), mSecondaryWhitePaint);
        }
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
     * Return: whether the UI background color would change at this update
     * newEstimateInMillis: user's new estimate arrival time in milliseconds since UNIX epoch
     * REFRESH THE ENTIRE UI */
    public boolean updateArrivalTime(long newEstimateInMillis) {
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
        if (temp < 0) {
            mDidMiss = true;
        } else {
            mDidMiss = false;
            mMinutesTillDeparture = String.valueOf(temp);
            if (mMinutesTillDeparture.length() == 1) {
                mMinutesTillDeparture = "0" + mMinutesTillDeparture;
            }
        }

        /* determine if the UI color would change */
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

    /* On swipe from top to bottom -> update the UI to show the previous train */
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
}