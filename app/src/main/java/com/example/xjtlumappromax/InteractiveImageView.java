package com.example.xjtlumappromax;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Map;

public class InteractiveImageView extends AppCompatImageView {

    private Matrix matrix = new Matrix();
    private ScaleGestureDetector scaleDetector;
    private float startX, startY, lastX, lastY;
    private boolean singleFinger, mapMoved;
    private float gpsX, gpsY;

    public InteractiveImageView(Context context) {
        this(context, null);
    }

    public InteractiveImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InteractiveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        setScaleType(ScaleType.MATRIX);
        post(() -> {
            float viewWidth = getWidth();
            float viewHeight = getHeight();
            float imageWidth = getDrawable().getIntrinsicWidth();
            float imageHeight = getDrawable().getIntrinsicHeight();
            float scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);
            float dx = (viewWidth - imageWidth * scale) / 2;
            float dy = (viewHeight - imageHeight * scale) / 2;
            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
            setImageMatrix(matrix);
        });
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        init(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = lastX = event.getX();
                startY = lastY = event.getY();
                singleFinger = true;
                mapMoved = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                lastX = (event.getX(0) + event.getX(1)) / 2;
                lastY = (event.getY(0) + event.getY(1)) / 2;
                singleFinger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx, dy;
                if (event.getPointerCount() >= 2) {
                    float midX = (event.getX(0) + event.getX(1)) / 2;
                    float midY = (event.getY(0) + event.getY(1)) / 2;
                    dx = midX - lastX;
                    dy = midY - lastY;
                    lastX = midX;
                    lastY = midY;
                } else {
                    if (!singleFinger) {
                        break;
                    }
                    dx = event.getX() - lastX;
                    dy = event.getY() - lastY;
                    lastX = event.getX();
                    lastY = event.getY();
                }

                float[] matrixValues = new float[9];
                matrix.getValues(matrixValues);

                // Calculate the new potential position of the image
                float currentTranslateX = matrixValues[Matrix.MTRANS_X];
                float currentTranslateY = matrixValues[Matrix.MTRANS_Y];

                // Get dimensions of the image and view
                float viewWidth = getWidth();
                float viewHeight = getHeight();
                float scaledWidth = getDrawable().getIntrinsicWidth() * matrixValues[Matrix.MSCALE_X];
                float scaledHeight = getDrawable().getIntrinsicHeight() * matrixValues[Matrix.MSCALE_Y];

                // Calculate bounds for the center of the image
                float leftLimit = Math.min(0, (viewWidth - scaledWidth) / 2);
                float rightLimit = Math.max(viewWidth, (viewWidth + scaledWidth) / 2);
                float topLimit = Math.min(0, (viewHeight - scaledHeight) / 2);
                float bottomLimit = Math.max(viewHeight, (viewHeight + scaledHeight) / 2);

                // Ensure the center of the image stays within bounds
                float newTranslateX = currentTranslateX + dx;
                float newTranslateY = currentTranslateY + dy;

                if (newTranslateX > rightLimit - scaledWidth / 2) {
                    dx = rightLimit - scaledWidth / 2 - currentTranslateX;
                } else if (newTranslateX < leftLimit - scaledWidth / 2) {
                    dx = leftLimit - scaledWidth / 2 - currentTranslateX;
                }

                if (newTranslateY > bottomLimit - scaledHeight / 2) {
                    dy = bottomLimit - scaledHeight / 2 - currentTranslateY;
                } else if (newTranslateY < topLimit - scaledHeight / 2) {
                    dy = topLimit - scaledHeight / 2 - currentTranslateY;
                }

                matrix.postTranslate(dx, dy);
                setImageMatrix(matrix);
                if (!mapMoved && ((Math.abs(event.getX(0) - startX) + Math.abs(event.getY(0) - startY)) > 10)) {
                    mapMoved = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (singleFinger && !mapMoved) {
                    float[] imageCoords = getImageCords(event.getX(), event.getY());
                    Log.i("InteractiveImageView", "image coords:" +
                            imageCoords[0] + "f, " + imageCoords[1]+"f,");
                    String boundName = getBound(imageCoords[0], imageCoords[1]);
                    if (boundName != null && onBoundClickListener != null) {
                        onBoundClickListener.onBoundClick(boundName);
                    }
                }
        }
        return true;
    }

    private float[] getImageCords(float x, float y) {
        float[] coords = {x, y};
        Matrix inverse = new Matrix();
        getImageMatrix().invert(inverse);
        inverse.mapPoints(coords);
        coords[0] /= getDrawable().getIntrinsicWidth();
        coords[1] /= getDrawable().getIntrinsicHeight();
        return coords;
    }


    private Drawable gpsDrawable, markerDrawable;

    public void setGpsDraw(Drawable drawable) {
        gpsDrawable = drawable;
        invalidate();
    }

    public void setGpsPosition(float x, float y) {
        gpsX = x;
        gpsY = y;
        invalidate();
    }

    public void cancelGpsDraw() {
        gpsDrawable = null;
        invalidate();
    }

    public boolean isGpsDrawn() {
        return gpsDrawable != null;
    }

    public void setMarkerDraw(Drawable drawable) {
        markerDrawable = drawable;
        invalidate();
    }

    public void cancelMarkerDraw() {
        markerDrawable = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (markerDrawable != null && bounds != null) {
            for (String boundName : bounds.keySet()) {
                float[] bounds = this.bounds.get(boundName);
                float[] markerCords = {
                        (bounds[0] + bounds[2]) / 2 * getDrawable().getIntrinsicWidth(),
                        (bounds[1] + bounds[3]) / 2 * getDrawable().getIntrinsicHeight()
                };
                matrix.mapPoints(markerCords);
                int drawableWidth = markerDrawable.getIntrinsicWidth();
                int drawableHeight = markerDrawable.getIntrinsicHeight();
                markerDrawable.setBounds(
                        (int) (markerCords[0] - drawableWidth / 2),
                        (int) (markerCords[1] - drawableHeight / 2),
                        (int) (markerCords[0] + drawableWidth / 2),
                        (int) (markerCords[1] + drawableHeight / 2)
                );
                markerDrawable.draw(canvas);
            }
        }
        if (gpsDrawable != null) {
            float[] gpsCords = {gpsX * getDrawable().getIntrinsicWidth(), gpsY * getDrawable().getIntrinsicHeight()};
            matrix.mapPoints(gpsCords);
            int drawableWidth = gpsDrawable.getIntrinsicWidth();
            int drawableHeight = gpsDrawable.getIntrinsicHeight();
            gpsDrawable.setBounds(
                    (int) (gpsCords[0] - drawableWidth / 2),
                    (int) (gpsCords[1] - drawableHeight / 2),
                    (int) (gpsCords[0] + drawableWidth / 2),
                    (int) (gpsCords[1] + drawableHeight / 2)
            );
            gpsDrawable.draw(canvas);
        }
    }


    private static final float MIN_ZOOM = 0.4f;
    private static final float MAX_ZOOM = 2f;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactorChange = detector.getScaleFactor();
            float[] matrixValues = new float[9];
            matrix.getValues(matrixValues);
            float px = getWidth() / 2.0f;
            float py = getHeight() / 2.0f;
            matrix.postScale(scaleFactorChange, scaleFactorChange, px, py);
            setImageMatrix(matrix);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }

    private Map<String, float[]> bounds;

    public void setBounds(Map<String, float[]> bounds) {
        this.bounds = bounds;
    }

    public String getBound(float x, float y) {
        if (bounds == null) {
            return null;
        }
        for (String boundName : bounds.keySet()) {
            if (isInBound(x, y, boundName)) {
                Log.i("InteractiveImageView", "bound clicked - " + boundName);
                return boundName;
            }
        }
        return null;
    }

    private boolean isInBound(float x, float y, String boundName) {
        float[] bounds = this.bounds.get(boundName);
        return x > bounds[0] && x < bounds[2] && y > bounds[1] && y < bounds[3];
    }

    public interface OnBoundClickListener {
        void onBoundClick(String boundName);
    }

    private OnBoundClickListener onBoundClickListener;

    public void setOnBoundClickListener(OnBoundClickListener onBoundClickListener) {
        this.onBoundClickListener = onBoundClickListener;
    }

}