package com.mikechoch.prism;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.DecimalFormat;


/**
 * Created by mikechoch on 2/26/18.
 */

public class ZoomControlLinearLayout extends LinearLayout {

    /*
     * Globals
     */
    private Context context;

    private ScaleGestureDetector mScaleDetector;
    private float startDistanceChange;
    private float totalDistanceChange;
    private boolean isZooming = false;

    private ViewGroup parentView;
    private ImageView zoomImageView;
    private ToolbarPullDownLayout toolbarPullDownLayout;
    private ViewGroup[] parentsScrollViews;

    private boolean disabled = false;


    /*
     * Constructors
     */
    public ZoomControlLinearLayout(Context context) {
        super(context);
    }

    public ZoomControlLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomControlLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ZoomControlLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * Give context
     */
    public void addContext(Context context) {
        this.context = context;

        mScaleDetector = new ScaleGestureDetector(context, new MyPinchListener());
    }

    /**
     * Add image view to be zoomed
     */
    public void addImageView(ImageView zoomImageView) {
        this.zoomImageView = zoomImageView;
    }

    /**
     * Add all parent view children views for later usage
     */
    public void addToolbarPullDownLayout(ToolbarPullDownLayout toolbarPullDownLayout) {
        this.toolbarPullDownLayout = toolbarPullDownLayout;
    }

    /**
     * Add all parent view children scroll views for later usage
     */
    public void addScrollViews(ViewGroup[] scrollViews) {
        this.parentsScrollViews = scrollViews;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                performClick();
                isZooming = false;
                zoomImageView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150);
                totalDistanceChange = 0;
                toggleViewIntercepts(false);
                break;
        }

        if (event.getPointerCount() == 2 && !isZooming) {
            toggleViewIntercepts(true);
            isZooming = true;
            float firstTouchX = event.getX(0);
            float firstTouchY = event.getY(0);
            float secondTouchX = event.getX(1);
            float secondTouchY = event.getY(1);

            float pivotPointX;
            if (firstTouchX < secondTouchX) {
                pivotPointX = firstTouchX + Math.abs(firstTouchX - secondTouchX);
            } else {
                pivotPointX = firstTouchX - Math.abs(firstTouchX - secondTouchX);
            }

            float pivotPointY;
            if (firstTouchY < secondTouchY) {
                pivotPointY = firstTouchY + Math.abs(firstTouchY - secondTouchY);
            } else {
                pivotPointY = firstTouchY - Math.abs(firstTouchY - secondTouchY);
            }
            zoomImageView.setPivotX(pivotPointX);
            zoomImageView.setPivotY(pivotPointY);

            double distanceX = Math.pow(Math.abs(firstTouchX - secondTouchX), 2);
            double distanceY = Math.pow(Math.abs(firstTouchY - secondTouchY), 2);
            startDistanceChange = (float) Math.sqrt(distanceX + distanceY);
        }

        mScaleDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean performClick() {
        // Calls the super implementation, which generates an AccessibilityEvent
        // and calls the onClick() listener on the view, if any
        super.performClick();

        // Handle the action for the custom click here
        return true;
    }

    /**
     * MyPinchListener handling a pinch gesture for zooming ImageView
     */
    public class MyPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
//            System.out.println("PINCH! OUCH!");
            if (startDistanceChange > 0) {
                totalDistanceChange += (detector.getCurrentSpan() - detector.getPreviousSpan());
//            System.out.println(startDistanceChange);
//            System.out.println(totalDistanceChange);
                DecimalFormat df = new DecimalFormat("0.##");
                float imageScale = Float.parseFloat(df.format((double) ((startDistanceChange + totalDistanceChange) / startDistanceChange)));
//            System.out.println(imageScale);
                if (imageScale >= 1) {
                    zoomImageView.setScaleX(imageScale);
                    zoomImageView.setScaleY(imageScale);
                }
            }
            return true;
        }
    }

    /**
     * A boolean parameter is taken in to disable touch intercepting
     */
    public void toggleViewIntercepts(boolean disableIntercept) {
        if (toolbarPullDownLayout != null) {
            toolbarPullDownLayout.disable(disableIntercept);
        }

        if (parentsScrollViews != null) {
            for (ViewGroup scrollView : parentsScrollViews) {
                scrollView.requestDisallowInterceptTouchEvent(disableIntercept);
            }
        }
    }

    /**
     * Disables the touch events for the pull down toolbar
     */
    public void disable(boolean shouldDisable) {
        disabled = shouldDisable;
    }
}
