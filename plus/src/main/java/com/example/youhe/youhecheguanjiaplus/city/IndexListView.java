package com.example.youhe.youhecheguanjiaplus.city;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class IndexListView extends View {

	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	String[] indexStrArr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
			"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
			"Y", "Z","#" };
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;

	public IndexListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public IndexListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexListView(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// if (showBkg)
		// {
		// canvas.drawColor(Color.parseColor("#F0EEEF"));
		// }
//		canvas.drawColor(Color.parseColor("#F0EEEF"));

		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / indexStrArr.length;
		for (int i = 0; i < indexStrArr.length; i++) {
			paint.setColor(Color.parseColor("#FFFFFF"));
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			int px = UnitTransformUtil.dip2px(getContext(), 15);
			paint.setTextSize(px);
			// if (i == choose)
			// {
			// paint.setColor(Color.parseColor("点击后的颜色"));
			// paint.setFakeBoldText(true);
			// }
			float xPos = width / 2 - paint.measureText(indexStrArr[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(indexStrArr[i], xPos, yPos, paint);
			paint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int index = (int) (y / getHeight() * indexStrArr.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// showBkg = true;
			if (oldChoose != index && listener != null) {
				if (index > 0 && index < indexStrArr.length) {
					listener.onTouchingLetterChanged(indexStrArr[index]);
					choose = index;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != index && listener != null) {
				if (index > 0 && index < indexStrArr.length) {
					listener.onTouchingLetterChanged(indexStrArr[index]);
					choose = index;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		void onTouchingLetterChanged(String s);
	}

}
