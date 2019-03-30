package com.csci4020.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

interface Shape
{
	void draw(Canvas canvas, Paint paint);

	int getColor();

	void onDraw(MotionEvent event);
}
