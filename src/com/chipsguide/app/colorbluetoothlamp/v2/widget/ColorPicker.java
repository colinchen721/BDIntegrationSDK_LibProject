/*
 * Copyright 2013 Piotr Adamus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chipsguide.app.colorbluetoothlamp.v2.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PixelUtil;

public class ColorPicker extends View {

	/**
	 * Customizable display parameters (in percents)
	 */
	private final int paramOuterPadding = 3; // 弧形的外
	private final int paramInnerPadding = 7; // distance between value slider
												// wheel and inner color wheel
	private final int paramValueSliderWidth = 2; // width of the value slider

	private Paint colorWheelPaint;
	private Paint valueSliderPaint;

	private Paint colorViewPaint;

	private Paint colorPointerPaint;
	private RectF colorPointerCoords;

	private Paint valuePointerPaint;

	private RectF outerWheelRect;
	private RectF innerWheelRect;

	private Path colorViewPath;
	private Path valueSliderPath;

	private Bitmap colorWheelBitmap;

	private int valueSliderWidth;
	private int innerPadding;
	private int outerPadding;

	private int outerWheelRadius;
	private int innerWheelRadius;
	private int colorWheelRadius;
	private int padding;

	private Matrix gradientRotationMatrix;

	private Drawable mThumb, sun, moon;
	private int mThumbXPos, mThumbYPos;
	private float thumbRadius;

	/** Currently selected color */
	private float[] colorHSV = new float[] { 0f, 0f, 1f };

	public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ColorPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ColorPicker(Context context) {
		super(context);
		init();
	}

	private void init() {

		colorPointerPaint = new Paint();
		colorPointerPaint.setStyle(Style.FILL);
		colorPointerPaint.setARGB(200, 255, 255, 255);

		valuePointerPaint = new Paint();
		valuePointerPaint.setStyle(Style.STROKE);
		valuePointerPaint.setStrokeWidth(2f);

		colorWheelPaint = new Paint();
		colorWheelPaint.setAntiAlias(true);
		colorWheelPaint.setDither(true);

		valueSliderPaint = new Paint();
		valueSliderPaint.setAntiAlias(true);
		valueSliderPaint.setDither(true);

		colorViewPaint = new Paint();
		colorViewPaint.setAntiAlias(true);

		colorViewPath = new Path();
		valueSliderPath = new Path();

		outerWheelRect = new RectF();
		innerWheelRect = new RectF();

		colorPointerCoords = new RectF();

		mThumb = getResources().getDrawable(R.drawable.thumb);
		int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
		int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
		mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
				thumbHalfheight);

		sun = getResources().getDrawable(R.drawable.ic_bright);
		int sunHalfHeight = (int) sun.getIntrinsicHeight() / 2;
		int sunHalfWidth = (int) sun.getIntrinsicWidth() / 2;
		sun.setBounds(-sunHalfWidth, -sunHalfHeight, sunHalfWidth,
				sunHalfHeight);

		moon = getResources().getDrawable(R.drawable.ic_dark);
		int moonHalfHeight = (int) moon.getIntrinsicHeight() / 2;
		int moonHalfWidth = (int) moon.getIntrinsicWidth() / 2;
		moon.setBounds(-moonHalfWidth, -moonHalfHeight, moonHalfWidth,
				moonHalfHeight);

		padding = Math.max(thumbHalfWidth,
				Math.max(sunHalfWidth, moonHalfWidth));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int size = Math.min(widthSize, heightSize);
		setMeasuredDimension(size, size);
		thumbRadius = (outerWheelRadius - (outerWheelRadius - innerWheelRadius) / 2);
		mThumbXPos = (int) (thumbRadius * Math.cos(Math.toRadians(180)));
		mThumbYPos = (int) (thumbRadius * Math.sin(Math.toRadians(180)));
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;

		// drawing color wheel

		canvas.drawBitmap(colorWheelBitmap, centerX - colorWheelRadius, centerY
				- colorWheelRadius, null);

		// drawing color view

		// colorViewPaint.setColor(Color.HSVToColor(colorHSV));
		// canvas.drawPath(colorViewPath, colorViewPaint);

		// drawing value slider

		float[] hsv = new float[] { colorHSV[0], colorHSV[1], 1f };

		SweepGradient sweepGradient = new SweepGradient(centerX, centerY,
				new int[] { Color.BLACK, Color.HSVToColor(hsv), Color.WHITE },
				null);
		sweepGradient.setLocalMatrix(gradientRotationMatrix);
		valueSliderPaint.setShader(sweepGradient);

		canvas.drawPath(valueSliderPath, valueSliderPaint);

		// drawing color wheel pointer

		float hueAngle = (float) Math.toRadians(colorHSV[0]);
		int colorPointX = (int) (-Math.cos(hueAngle) * colorHSV[1] * colorWheelRadius)
				+ centerX;
		int colorPointY = (int) (-Math.sin(hueAngle) * colorHSV[1] * colorWheelRadius)
				+ centerY;

		float pointerRadius = 0.1f * colorWheelRadius;
		int pointerX = (int) (colorPointX - pointerRadius / 2);
		int pointerY = (int) (colorPointY - pointerRadius / 2);

		colorPointerCoords.set(pointerX, pointerY, pointerX + pointerRadius,
				pointerY + pointerRadius);
		canvas.drawOval(colorPointerCoords, colorPointerPaint);

		drawDrawable(canvas);

		Log.d("colorChnage", "<<<<colorChange");
		if (mColorChangelistener != null) {
			int color = Color.HSVToColor(colorHSV);
			int alpha = Color.alpha(color);
			int red = (color & 0xff0000) >> 16;
			int green = (color & 0x00ff00) >> 8;
			int blue = (color & 0x0000ff);
			mColorChangelistener.onColorChange(alpha, red, green, blue);
		}
	}

	private void drawDrawable(Canvas canvas) {
		int offset = PixelUtil.dp2px(25, getContext());
		canvas.save();
		canvas.translate(getWidth() / 2 + thumbRadius, getHeight() / 2 + offset);
		sun.draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.translate(getWidth() / 2 - thumbRadius, getHeight() / 2 + offset);
		moon.draw(canvas);
		canvas.restore();

		canvas.translate(getWidth() / 2 - mThumbXPos, getHeight() / 2
				- mThumbYPos);
		// canvas.drawCircle((float)mThumbXPos, (float)mThumbYPos, 30,
		// valuePointerArrowPaint);
		mThumb.draw(canvas);
	}

	private void updateThumbPosition() {
		double thumbAngle = (colorHSV[2]) * Math.PI;
		double tipAngleX = Math.cos(thumbAngle) * thumbRadius;
		double tipAngleY = Math.sin(thumbAngle) * thumbRadius;
		mThumbXPos = (int) tipAngleX;
		mThumbYPos = (int) tipAngleY;
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {

		int centerX = width / 2;
		int centerY = height / 2;

		innerPadding = (int) (paramInnerPadding * width / 100);
		outerPadding = (int) (paramOuterPadding * width / 100);
		valueSliderWidth = (int) (paramValueSliderWidth * width / 100);

		outerWheelRadius = width / 2 - outerPadding - padding;
		innerWheelRadius = outerWheelRadius - valueSliderWidth;
		colorWheelRadius = innerWheelRadius - innerPadding;

		outerWheelRect.set(centerX - outerWheelRadius, centerY
				- outerWheelRadius, centerX + outerWheelRadius, centerY
				+ outerWheelRadius);
		innerWheelRect.set(centerX - innerWheelRadius, centerY
				- innerWheelRadius, centerX + innerWheelRadius, centerY
				+ innerWheelRadius);

		colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2,
				colorWheelRadius * 2);

		gradientRotationMatrix = new Matrix();
		gradientRotationMatrix.preRotate(180, width / 2, height / 2);

		colorViewPath.arcTo(outerWheelRect, 270, -180);
		colorViewPath.arcTo(innerWheelRect, 90, 180);

		valueSliderPath.arcTo(outerWheelRect, 180, 180);
		valueSliderPath.arcTo(innerWheelRect, 0, -180);

	}

	private Bitmap createColorWheelBitmap(int width, int height) {

		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		int colorCount = 12;
		int colorAngleStep = 360 / 12;
		int colors[] = new int[colorCount + 1];
		float hsv[] = new float[] { 0f, 1f, 1f };
		for (int i = 0; i < colors.length; i++) {
			hsv[0] = (i * colorAngleStep + 180) % 360;
			colors[i] = Color.HSVToColor(hsv);
		}
		colors[colorCount] = colors[0];

		SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2,
				colors, null);
		RadialGradient radialGradient = new RadialGradient(width / 2,
				height / 2, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF,
				TileMode.CLAMP);
		ComposeShader composeShader = new ComposeShader(sweepGradient,
				radialGradient, PorterDuff.Mode.SRC_OVER);

		colorWheelPaint.setShader(composeShader);

		Canvas canvas = new Canvas(bitmap);
		canvas.drawCircle(width / 2, height / 2, colorWheelRadius,
				colorWheelPaint);

		return bitmap;

	}
	

	boolean downOnWheel = false;
	boolean downOnArc = false;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if(isTouchArc(x, y)){
				downOnArc = true;
				return true;
			}else if(isTouchColorWheel(x, y)){
				downOnWheel = true;
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:

			int cx = x - getWidth() / 2;
			int cy = y - getHeight() / 2;
			double d = Math.sqrt(cx * cx + cy * cy);

			if (downOnWheel && isTouchColorWheel(x, y)) {

				colorHSV[0] = (float) (Math.toDegrees(Math.atan2(cy, cx)) + 180f);
				colorHSV[1] = Math.max(0f,
						Math.min(1f, (float) (d / colorWheelRadius)));

				invalidate();
				return true;
			} else if (downOnArc && y <= getHeight() / 2) {
				cy = Math.abs(cy);
				colorHSV[2] = (float) Math.max(0,
						Math.min(1, 1 - Math.atan2(cy, cx) / Math.PI));
				updateThumbPosition();
				invalidate();
				return true;
			}

			break;
		case MotionEvent.ACTION_UP:
			downOnArc = false;
			downOnWheel = false;
			if (mColorChangelistener != null) {
				int color = Color.HSVToColor(colorHSV);
				int alpha = Color.alpha(color);
				int red = (color & 0xff0000) >> 16;
				int green = (color & 0x00ff00) >> 8;
				int blue = (color & 0x0000ff);
				mColorChangelistener.onColorChangeEnd(alpha, red, green, blue);
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private boolean isTouchArc(int x, int y) {
		double d = getTouchRadius(x, y);
		if (y <= getHeight() / 2 && d >= (innerWheelRadius - padding)
				&& d <= (outerWheelRadius + padding)) {
			return true;
		}
		return false;
	}
	
	private boolean isTouchColorWheel(int x , int y) {
		double d = getTouchRadius(x, y);
		if (d < colorWheelRadius) {
			return true;
		}
		return false;
	}

	private double getTouchRadius(int x, int y) {
		int cx = x - getWidth() / 2;
		int cy = y - getHeight() / 2;
		return Math.sqrt(cx * cx + cy * cy);
	}

	public void setColor(int color) {
		Color.colorToHSV(color, colorHSV);
	}

	public int getColor() {
		return Color.HSVToColor(colorHSV);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle state = new Bundle();
		state.putFloatArray("color", colorHSV);
		state.putParcelable("super", super.onSaveInstanceState());
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			colorHSV = bundle.getFloatArray("color");
			super.onRestoreInstanceState(bundle.getParcelable("super"));
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	private OnColorChangeListener mColorChangelistener;

	public void setOnColorChangeListener(OnColorChangeListener listener) {
		mColorChangelistener = listener;
	}

	public interface OnColorChangeListener {
		void onColorChange(int alpha, int red, int green, int blue);

		void onColorChangeEnd(int alpha, int red, int green, int blue);
	}

}
