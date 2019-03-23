package com.zmp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class ScanRectView extends View {

        private RectF rectF;

        private RectF mRectF;

        private Paint mWhitePaint;

        private Paint mGreenPaint;

        private Path mPath;

        private Paint mGreenPaint2;

        private float mLineTop;

        private boolean isAdd;

        public ScanRectView(Context context) {
                this(context, null);
        }

        public ScanRectView(Context context, @Nullable AttributeSet attrs) {
                this(context, attrs, 0);
        }

        public ScanRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
                init();
        }

        private void init() {
                mWhitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mWhitePaint.setStrokeWidth(2);
                mWhitePaint.setColor(Color.WHITE);
                mWhitePaint.setStyle(Paint.Style.STROKE);

                mGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mGreenPaint.setStrokeWidth(4);
                mGreenPaint.setColor(Color.GREEN);
                mGreenPaint.setStyle(Paint.Style.STROKE);

                mGreenPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                mGreenPaint2.setStrokeWidth(4);
                mGreenPaint2.setStyle(Paint.Style.STROKE);
                mRectF = new RectF();
                mPath = new Path();

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                int measuredWidth = getMeasuredWidth();
                int measuredHeight = getMeasuredHeight();
                float minR = Math.min(measuredWidth, measuredHeight) * 0.8F / 2;
                mRectF.left = measuredWidth / 2 - minR;
                mRectF.top = measuredHeight / 2 - minR;
                mRectF.right = measuredWidth / 2 + minR;
                mRectF.bottom = measuredHeight / 2 + minR;
                int[] oc2 = new int[]{0x0000FF00, 0xFF00FF00};
                LinearGradient linearGradient = new LinearGradient(mRectF.left, 0, measuredWidth / 2, 0,
                                                                   oc2, null, Shader.TileMode.MIRROR);
                mGreenPaint2.setShader(linearGradient);


                postDelayed(runnable, 100);
        }

        @Override
        protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(mRectF, mWhitePaint);
                drawRectAngle(canvas);
                canvas.drawLine(mRectF.left + 30, mLineTop, mRectF.right - 30, mLineTop, mGreenPaint2);
        }

        Runnable runnable = new Runnable() {
                @Override
                public void run() {
                        if (isAdd) {
                                mLineTop += 10;
                                if (mLineTop > mRectF.bottom - 30) {
                                        mLineTop = mRectF.bottom - 30;
                                        isAdd = false;
                                }
                        }
                        else {
                                mLineTop -= 10;
                                if (mLineTop < mRectF.top + 30) {
                                        mLineTop = mRectF.top + 30;
                                        isAdd = true;
                                }
                        }
                        invalidate();
                        postDelayed(this,100);
                }
        };

        private void drawRectAngle(Canvas canvas) {
                mPath.moveTo(mRectF.left, mRectF.top + 20);
                mPath.lineTo(mRectF.left, mRectF.top);
                mPath.lineTo(mRectF.left + 20, mRectF.top);
                canvas.drawPath(mPath, mGreenPaint);
                mPath.reset();
                mPath.moveTo(mRectF.right - 20, mRectF.top);
                mPath.lineTo(mRectF.right, mRectF.top);
                mPath.lineTo(mRectF.right, mRectF.top + 20);
                canvas.drawPath(mPath, mGreenPaint);
                mPath.reset();
                mPath.moveTo(mRectF.right, mRectF.bottom - 20);
                mPath.lineTo(mRectF.right, mRectF.bottom);
                mPath.lineTo(mRectF.right - 20, mRectF.bottom);
                canvas.drawPath(mPath, mGreenPaint);
                mPath.reset();
                mPath.moveTo(mRectF.left, mRectF.bottom - 20);
                mPath.lineTo(mRectF.left, mRectF.bottom);
                mPath.lineTo(mRectF.left + 20, mRectF.bottom);
                canvas.drawPath(mPath, mGreenPaint);
                mPath.reset();
        }

        @Override
        protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                removeCallbacks(runnable);
        }
}
