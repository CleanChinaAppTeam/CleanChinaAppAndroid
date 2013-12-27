package com.cleanchina.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.cleanchina.R;

/**
 * list列表页用来做快速切换的工具条，以section做区分
 * 
 * @author Jun.Deng
 * 
 */
public class AlphabetBar extends View {

	private String[] mSections;

	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private int m_nItemHeight = 25;
	private int mCurIdx;

	public AlphabetBar(Context context) {
		super(context);
		setBackgroundColor(0x44FFFFFF);
	}

	public AlphabetBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundColor(0x44FFFFFF);
	}

	public AlphabetBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setBackgroundColor(0x44FFFFFF);
	}

	public void setListView(ListView _list) {
		list = _list;
	}

	public void setSectionIndexter(SectionIndexer sectionIndexter) {
		this.sectionIndexter = sectionIndexter;
		this.mSections = (String[]) sectionIndexter.getSections();
		requestLayout();
	}

	public int getCurIndex() {
		return mCurIdx;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mSections != null && mSections.length > 0) {
			m_nItemHeight = (bottom - top - 10) / mSections.length;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		int idx = i / m_nItemHeight;
		if (idx >= mSections.length) {
			idx = mSections.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		mCurIdx = idx;

		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(idx);
			if (position == -1) {
				return true;
			}
			list.setSelection(position);

			if (mOnSelectedListener != null) {
				mOnSelectedListener.onSelected(position);
			}

			setBackgroundColor(0xFFCCCCCC);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (mOnSelectedListener != null) {
				mOnSelectedListener.onUnselected();
			}
			setBackgroundColor(0x00FFFFFF);
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mSections == null) {
			return;
		}

		Paint paint = new Paint();
		paint.setColor(getContext().getResources().getColor(R.color.text_deep_gray));
		paint.setAntiAlias(true);

		if (m_nItemHeight > 25) {
			paint.setTextSize(20);
		} else if (m_nItemHeight - 5 < 5) {
			paint.setTextSize(5);
		} else {
			paint.setTextSize(m_nItemHeight - 5);
		}
		paint.setTextAlign(Paint.Align.CENTER);

		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < mSections.length; i++) {
			String text = String.valueOf(mSections[i]);
			if (text.length() > 2) {
				text = text.substring(0, 1);
			}
			canvas.drawText(text, widthCenter, m_nItemHeight
					+ (i * m_nItemHeight), paint);
		}
		super.onDraw(canvas);
	}

	public interface OnSelectedListener {
		public void onSelected(int pos);

		public void onUnselected();
	}

	private OnSelectedListener mOnSelectedListener;

	public void setOnSelectedListener(OnSelectedListener l) {
		mOnSelectedListener = l;
	}
}