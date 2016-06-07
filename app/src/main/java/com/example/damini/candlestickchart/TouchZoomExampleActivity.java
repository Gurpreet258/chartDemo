package com.example.damini.candlestickchart;
/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/***********************************
 * @author David Buezas (david.buezas at gmail.com)
 * Feel free to copy, modify and use the source as it fits you.
 * 09/27/2012 nfellows - updated for 0.5.1 and made a few simplifications
 */
public class TouchZoomExampleActivity extends Activity implements OnTouchListener {
    private static final int SERIES_SIZE = 200;
    private XYPlot mySimpleXYPlot;
    private Button resetButton;
    private SimpleXYSeries[] series = null;
    private PointF minXY;
    private PointF maxXY;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_zoom_example);

        mySimpleXYPlot = (XYPlot) findViewById(R.id.plot);
        mySimpleXYPlot.setOnTouchListener(this);
        mySimpleXYPlot.getGraphWidget().setTicksPerRangeLabel(2);
        mySimpleXYPlot.getGraphWidget().setTicksPerDomainLabel(2);
        mySimpleXYPlot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        mySimpleXYPlot.getGraphWidget().setRangeValueFormat(
                new DecimalFormat("#####"));
        mySimpleXYPlot.getGraphWidget().setDomainValueFormat(
                new DecimalFormat("#####.##"));
        mySimpleXYPlot.getGraphWidget().setRangeTickLabelWidth(25);

        mySimpleXYPlot.setRangeLabel("");
        mySimpleXYPlot.setDomainLabel("");

        mySimpleXYPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        mySimpleXYPlot.getGraphWidget().setRangeTick(false);
        Paint paint=new Paint();
        paint.setColor(Color.TRANSPARENT);
        mySimpleXYPlot.setGridPadding(50,50,50,50);

        //mySimpleXYPlot.disableAllMarkup();
        series = new SimpleXYSeries[4];
        int scale = 1;
        ArrayList<Integer> alValues=new ArrayList();
        alValues.addAll(Arrays.asList(14000, 200, 300, 12200, 12000,12500, 200, 300, 12200, 5000,14000, 200, 300, 12200, 5000,14000, 200, 300, 12200, 5000));
        alValues.addAll(Arrays.asList(14000, 200, 300, 12200, 5000,14000, 200, 300, 12200, 5000,14000, 200, 300, 12200, 5000,14000, 200, 300, 12200, 5000));

        series[3]= new SimpleXYSeries(alValues,
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
        mySimpleXYPlot.setRangeBoundaries(0, Collections.max(alValues)+mySimpleXYPlot.getRangeStepValue(),BoundaryMode.FIXED);
        mySimpleXYPlot.addSeries(series[3],
                new LineAndPointFormatter(Color.rgb(50, 150, 0), Color.rgb(0, 100, 0),
                        null, new PointLabelFormatter(Color.BLACK)));


        mySimpleXYPlot.redraw();
        mySimpleXYPlot.calculateMinMaxVals();
        minXY = new PointF(mySimpleXYPlot.getCalculatedMinX().floatValue(),
                mySimpleXYPlot.getCalculatedMinY().floatValue());
        maxXY = new PointF(mySimpleXYPlot.getCalculatedMaxX().floatValue(),
                mySimpleXYPlot.getCalculatedMaxY().floatValue());
    }

    private void populateSeries(SimpleXYSeries series, int max) {
        Random r = new Random();
        for(int i = 0; i < SERIES_SIZE; i++) {
            series.addLast(i, r.nextInt(max));
        }
    }

    // Definition of the touch states
    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;

    PointF firstFinger;
    float distBetweenFingers;
    boolean stopThread = false;

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // Start gesture
                firstFinger = new PointF(event.getX(), event.getY());
                mode = ONE_FINGER_DRAG;
                stopThread = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // second finger
                distBetweenFingers = spacing(event);
                // the distance check is done to avoid false alarms
                if (distBetweenFingers > 5f) {
                    mode = TWO_FINGERS_DRAG;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ONE_FINGER_DRAG) {
                    PointF oldFirstFinger = firstFinger;
                    firstFinger = new PointF(event.getX(), event.getY());
                    scroll(oldFirstFinger.x - firstFinger.x);
                    mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    mySimpleXYPlot.redraw();

                } else if (mode == TWO_FINGERS_DRAG) {
                    float oldDist = distBetweenFingers;
                    distBetweenFingers = spacing(event);
                    zoom(oldDist / distBetweenFingers);
                    mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    mySimpleXYPlot.redraw();
                }
                break;
        }
        return true;
    }

    private void zoom(float scale) {
        Log.w("tag",String.valueOf(scale));
        float domainSpan = maxXY.x - minXY.x;
        float domainMidPoint = maxXY.x - domainSpan / 2.0f;
        float offset = domainSpan * scale / 2.0f;

        minXY.x = domainMidPoint - offset;
        maxXY.x = domainMidPoint + offset;

        minXY.x = Math.min(minXY.x, series[3].getX(series[3].size() - 3)
                .floatValue());
        maxXY.x = Math.max(maxXY.x, series[3].getX(1).floatValue());
        clampToDomainBounds(domainSpan);
    }

    private void scroll(float pan) {
        float domainSpan = maxXY.x - minXY.x;
        float step = domainSpan / mySimpleXYPlot.getWidth();
        float offset = pan * step;
        minXY.x = minXY.x + offset;
        maxXY.x = maxXY.x + offset;
        clampToDomainBounds(domainSpan);
    }

    private void clampToDomainBounds(float domainSpan) {
        float leftBoundary = series[3].getX(0).floatValue();
        float rightBoundary = series[3].getX(series[3].size() - 1).floatValue();
        // enforce left scroll boundary:
        if (minXY.x < leftBoundary) {
            minXY.x = leftBoundary;
            maxXY.x = leftBoundary + domainSpan;
        } else if (maxXY.x > series[3].getX(series[3].size() - 1).floatValue()) {
            maxXY.x = rightBoundary;
            minXY.x = rightBoundary - domainSpan;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.hypot(x, y);
    }
}
