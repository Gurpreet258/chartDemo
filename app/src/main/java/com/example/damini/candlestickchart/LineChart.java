package com.example.damini.candlestickchart;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LineChart extends Activity implements View.OnTouchListener,View.OnClickListener {
    ArrayList<Integer> alEntriesAll=new ArrayList<>();
    ArrayList<String> alLabelsAll=new ArrayList<>();
    ArrayList<String> alLabelsAllWithTime=new ArrayList<>();


    double beforeZoomValueX =0;
    double beforeZoomMinValueX =0;

    String[] domainMap;

    int clickCount = 0;

    List s1;
    List listGoal;
    String strGoal;

    private XYPlot plot;
    private PointF minXY;
    private PointF maxXY;

    private XYSeries series1;
    private XYSeries goalSeries;
    private XYSeries initSeries;

    private final boolean all=true;
    private final boolean average=false;

    private  boolean alValues=false;

    // Definition of the touch states
    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;

    PointF firstFinger;
    float distBetweenFingers;
    boolean stopThread = false;
    boolean first=true;
    private float dSpan;
    private boolean zoomin=false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_chart);

        initViews();
        initChart(average);
    }

    protected void initViews(){
        alEntriesAll.add(50);
        alEntriesAll.add(150);
        alEntriesAll.add(20);
        alEntriesAll.add(350);
        alEntriesAll.add(50);
        alEntriesAll.add(50);
        alEntriesAll.add(250);
        alEntriesAll.add(60);
        alEntriesAll.add(50);
        alEntriesAll.add(50);
        alEntriesAll.add(50);

        alLabelsAll.add("2/3/2015");
        alLabelsAll.add("2/4/2015");
        alLabelsAll.add("2/5/2015");
        alLabelsAll.add("2/6/2015");
        alLabelsAll.add("2/7/2015");
        alLabelsAll.add("2/7/2015");
        alLabelsAll.add("2/7/2015");
        alLabelsAll.add("2/7/2015");
        alLabelsAll.add("2/7/2015");
        alLabelsAll.add("2/7/2015");
        alLabelsAll.add("2/7/2015");

        plot = (XYPlot) findViewById(R.id.plot);
        plot.setOnTouchListener(this);
        plot.getGraphWidget().setTicksPerRangeLabel(2);
        //  plot.getGraphWidget().setTicksPerDomainLabel(2);

        plot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getDomainGridLinePaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getRangeSubGridLinePaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getDomainSubGridLinePaint().setColor(Color.TRANSPARENT);

        plot.getGraphWidget().getDomainTickLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeTickLabelPaint().setColor(Color.BLACK);

        plot.getGraphWidget().getDomainOriginTickLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeTickLabelPaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.TRANSPARENT);

        plot.getGraphWidget().getDomainSubGridLinePaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getRangeSubGridLinePaint().setColor(Color.TRANSPARENT);
        plot.setUserRangeOrigin(0);
        plot.setUserDomainOrigin(0);

        plot.getGraphWidget().setRangeTickLabelWidth(10);
        plot.setGridPadding(75, 50, 50, 50);
        plot.setRangeLabel("");
        //plot.setDomainStep(XYStepMode.SUBDIVIDE, 1);
        plot.setDomainLabel("");

        plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
    }
    protected void initChart(boolean all){
        ArrayList<Integer> alGoal=new ArrayList<>();
        ArrayList<Integer> alInit=new ArrayList<>();

            domainMap =  alLabelsAll.toArray(new String[alLabelsAll.size()]);
            s1 = alEntriesAll;
            if(strGoal!=null && !strGoal.equals("")) {
                for (int i = 0; i < alEntriesAll.size(); i++) {
                    alGoal.add(Integer.parseInt(strGoal));
                }
            }
            for (int i = 0; i < alEntriesAll.size(); i++) {
                alInit.add(0);
            }


        listGoal=alGoal;

        plot.setDomainValueFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                Log.d("field pos", field + "");

                if ((int) value >= domainMap.length)
                    return new StringBuffer(domainMap.length);
                else if ((int) value < 0)

                    return new StringBuffer(0);
                else
                    return new StringBuffer(domainMap[(int) value]);

            }

            @Override
            public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                return null;
            }

            @Override
            public Number parse(String string, ParsePosition position) {
                return null;
            }
        });

        series1 = new SimpleXYSeries(s1,
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
        goalSeries=new SimpleXYSeries(listGoal,
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
        initSeries=new SimpleXYSeries(alInit,
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");

        //set point label style
        PointLabelFormatter pointLabelFormatter=new PointLabelFormatter();
        pointLabelFormatter.getTextPaint().setColor(Color.parseColor("#000000"));

        PointLabelFormatter goalLabelFormatter=new PointLabelFormatter();
        goalLabelFormatter.getTextPaint().setColor(Color.TRANSPARENT);

        LineAndPointFormatter s1Format = new LineAndPointFormatter(Color.BLUE,Color.BLUE,null,pointLabelFormatter);
        LineAndPointFormatter goalFormat = new LineAndPointFormatter(Color.parseColor("#7F007F"),null,null,goalLabelFormatter);
        LineAndPointFormatter initFormat = new LineAndPointFormatter(Color.parseColor("#333333"),null,null,goalLabelFormatter);
        plot.addSeries(series1, s1Format);
        plot.addSeries(goalSeries, goalFormat);
        plot.addSeries(initSeries, initFormat);
        if(alLabelsAll.size()>0) {
            try {

                    plot.setRangeBoundaries(0, Collections.max(alEntriesAll) + plot.getRangeStepValue(), BoundaryMode.FIXED);

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        plot.redraw();
        plot.calculateMinMaxVals();

        minXY = new PointF(plot.getCalculatedMinX().floatValue(),
                plot.getCalculatedMinY().floatValue());
        maxXY = new PointF(plot.getCalculatedMaxX().floatValue(),
                plot.getCalculatedMaxY().floatValue());
        float mazValue = series1.getX(series1.size() - 1).floatValue();

        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL,1);

        zoom(0.615f);
        float domainSpan = maxXY.x - minXY.x;
        maxXY.x = mazValue;
        minXY.x = mazValue-domainSpan;

        plot.setDomainBoundaries(mazValue - domainSpan, mazValue,
                BoundaryMode.FIXED);

        plot.redraw();

    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // Start gesture
                firstFinger = new PointF(event.getX(), event.getY());
                mode = ONE_FINGER_DRAG;
                stopThread = true;
                if(clickCount==0)
                {
                    // WeightChartActivity.showlogs("value set "+maxXY.x+","+minXY.x);
                    beforeZoomValueX =maxXY.x;
                    beforeZoomMinValueX=minXY.x;
                }
                clickCount++;
                break;
            case MotionEvent.ACTION_UP:
                if(clickCount == 2)
                {
                    zoom(0.615f);
                    plot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    plot.redraw();
                    // clickCount = 0;
                    break;
                }
                else if(clickCount == 4)
                {
                    minXY.x=(float)beforeZoomMinValueX;
                    maxXY.x= (float)beforeZoomValueX;
                    plot.setDomainBoundaries(beforeZoomMinValueX, beforeZoomValueX,
                            BoundaryMode.FIXED);
                    plot.redraw();
                    //   plot.redraw();
                    clickCount = 0;
                    break;
                }
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
                    plot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    plot.redraw();


                } else if (mode == TWO_FINGERS_DRAG) {
                    clickCount=0;

                    float oldDist = distBetweenFingers;
                    distBetweenFingers = spacing(event);
                    zoom(oldDist / distBetweenFingers);
                    double distance=oldDist/distBetweenFingers;
                    plot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    plot.redraw();
                }
                break;
        }
        return true;
    }

    private void zoom(float scale) {

        float domainSpan = maxXY.x - minXY.x;
        Log.d("zoom","scale:"+scale);

        float domainMidPoint = maxXY.x - domainSpan / 2.0f;
        float offset = domainSpan * scale / 2.0f;
       /* if(first){
            dSpan=domainSpan;
            Log.d("date : ","default:"+dSpan+"");
            first=false;
        }
        if(dSpan>0.5){
            dSpan-=0.1;
        }
        if(domainSpan>(dSpan) || domainSpan==dSpan){
            Log.d("date","default date "+domainSpan);
            setDefaultDate();
        }
        else{
            Log.d("date","date with time "+domainSpan);
            setDateWithTime();
        }*/
        minXY.x = domainMidPoint - offset;
        maxXY.x = domainMidPoint + offset;

        minXY.x = Math.min(minXY.x, series1.getX(series1.size() - 1)
                .floatValue());
        maxXY.x = Math.max(maxXY.x, series1.getX(1).floatValue());
        Log.d("domain span : ",domainSpan+"");
        clampToDomainBounds(domainSpan);
    }

    private void scroll(float pan) {
        clickCount=0;
        if(first){
            first=false;
        }
        else {
            float domainSpan = maxXY.x - minXY.x;
            float step = domainSpan / plot.getWidth();
            float offset = pan * step;
            minXY.x = minXY.x + offset;
            maxXY.x = maxXY.x + offset;
            clampToDomainBounds(domainSpan);
        }
    }

    private void clampToDomainBounds(float domainSpan) {
        float leftBoundary = series1.getX(0).floatValue();
        float rightBoundary = series1.getX(series1.size() - 1).floatValue();
        // enforce left scroll boundary:
        if (minXY.x < leftBoundary) {
            minXY.x = leftBoundary;
            maxXY.x = leftBoundary + domainSpan;
        } else if (maxXY.x > series1.getX(series1.size() - 1).floatValue()) {
            maxXY.x = rightBoundary;
            minXY.x = rightBoundary - domainSpan;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.hypot(x, y);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
    }
    protected void setDefaultDate(){
        // plot.setDomainStepValue(10);
            domainMap = alLabelsAll.toArray(new String[alLabelsAll.size()]);

            plot.setDomainValueFormat(new NumberFormat() {
                @Override
                public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {

                    if ((int) value >= domainMap.length)
                        return new StringBuffer(domainMap.length);
                    else if ((int) value < 0)

                        return new StringBuffer(0);
                    else
                        return new StringBuffer(domainMap[(int) value]);

                }

                @Override
                public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                    return null;
                }

                @Override
                public Number parse(String string, ParsePosition position) {
                    return null;
                }
            });

    }
    protected void setDateWithTime(){

        if(alValues) {
            domainMap = alLabelsAllWithTime.toArray(new String[alLabelsAllWithTime.size()]);

            plot.setDomainValueFormat(new NumberFormat() {
                @Override
                public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {

                    if ((int) value >= domainMap.length)
                        return new StringBuffer(domainMap.length);
                    else if ((int) value < 0)

                        return new StringBuffer(0);
                    else
                        return new StringBuffer(domainMap[(int) value]);

                }

                @Override
                public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                    return null;
                }

                @Override
                public Number parse(String string, ParsePosition position) {
                    return null;
                }
            });
        }

    }


}
