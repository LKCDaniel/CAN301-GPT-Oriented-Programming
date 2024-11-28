package com.example.xjtlumappromax;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Map;

public class InteractiveImageView extends AppCompatImageView {

    private Matrix matrix = new Matrix();
    private ScaleGestureDetector scaleDetector;
    private float lastX, lastY;
    private boolean allPointersUp;

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
            float dy = 0;//(viewHeight - imageHeight * scale) / 2;
            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
            setImageMatrix(matrix);
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                allPointersUp = true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                lastX = (event.getX(0) + event.getX(1)) / 2;
                lastY = (event.getY(0) + event.getY(1)) / 2;
                allPointersUp = false;
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
                break;
            case MotionEvent.ACTION_UP:
                if (allPointersUp) {
                    float[] imageCoords = getImageCords(event.getX(), event.getY());
                    Log.i("InteractiveImageView", "image coords - x: " +
                            imageCoords[0] + ", y: " + imageCoords[1]);
                    String building = getBuilding(imageCoords[0], imageCoords[1]);
                    if (building != null && onBuildingClickListener != null) {
                        onBuildingClickListener.onBuildingClick(building);
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
        return coords;
    }


    private static final float MIN_ZOOM = 0.4f;
    private static final float MAX_ZOOM = 2f;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactorChange = detector.getScaleFactor();
            float[] matrixValues = new float[9];
            matrix.getValues(matrixValues);
            float currentScale = matrixValues[Matrix.MSCALE_X];

            float newScale = currentScale * scaleFactorChange;
            if (newScale < MIN_ZOOM) {
                scaleFactorChange = MIN_ZOOM / currentScale;
            } else if (newScale > MAX_ZOOM) {
                scaleFactorChange = MAX_ZOOM / currentScale;
            }

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


    private Map<String, float[]> buildingBounds;

    public void setBuildingBounds(Map<String, float[]> buildingBounds) {
        this.buildingBounds = buildingBounds;
    }

    private String getBuilding(float x, float y) {
        if (buildingBounds == null) {
            return null;
        }
        for (String building : buildingBounds.keySet()) {
            if (isInBuilding(x, y, building)) {
                return building;
            }
        }
        return null;
    }

    private boolean isInBuilding(float x, float y, String building) {
        float[] bounds = buildingBounds.get(building);
        return x > bounds[0] && x < bounds[2] && y > bounds[1] && y < bounds[3];
    }

    public interface OnBuildingClickListener {
        void onBuildingClick(String building);
    }

    private OnBuildingClickListener onBuildingClickListener;

    public void setOnBuildingClickListener(OnBuildingClickListener onBuildingClickListener) {
        this.onBuildingClickListener = onBuildingClickListener;
    }

}