package com.example.inputmethod;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

/**
 * a layout in response to soft input method
 * <ol>
 * <li>you should set InputMethodResponseScrollView as the root view of a Activity</li>
 * <li>the Activity should set <b>android:windowSoftInputMode="adjustResize"</b> in AndroidManifest.xml</li>
 * <ol>
 */
public class InputMethodResponseScrollView extends ScrollView {
	private Rect r = new Rect();
	private Map<View, View> map = new HashMap<View, View>();
	private boolean shouldRepositionScrollY;

	public InputMethodResponseScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public InputMethodResponseScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public InputMethodResponseScrollView(Context context) {
		super(context);
	}
	
	private final OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) repositionScrollY();
		}
	};
	
	public void registerAllChildEditTextFocusChangeListener() {
		registerAllChildEditTextFocusChangeListener(this);
	}
	
	private void registerAllChildEditTextFocusChangeListener(View v) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				registerAllChildEditTextFocusChangeListener(vg.getChildAt(i));
			}
		} else if (v instanceof EditText) {
			((EditText) v).setOnFocusChangeListener(onFocusChangeListener);
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		registerAllChildEditTextFocusChangeListener();
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		getGlobalVisibleRect(r);
		int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
		shouldRepositionScrollY = r.bottom < screenHeight;
		/*
		 * bug: sometimes(when you touch a EditText, if the EditText's bottom 
		 * is flush with the soft input method's top) the onScrollChanged may 
		 * not call back, so we explicitly call repositionScrollY()
		 */
		repositionScrollY();
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (shouldRepositionScrollY) {
			shouldRepositionScrollY = false;
			repositionScrollY();
		}
	}
	
	private void repositionScrollY() {
		View focusedChild = findFocus();
		if (focusedChild instanceof EditText) {
			final View v = map.get(focusedChild);
			if (v != null) {
				int bottom = getBottomOfView(v);
				if (bottom - getScrollY() > r.height()) {
					int scrollToY = bottom - r.height();
					smoothScrollTo(0, scrollToY);
				}
			} else {
				View nextFocusView = focusedChild.focusSearch(View.FOCUS_DOWN);
				if (nextFocusView instanceof EditText) {
					int bottom = getBottomOfView(nextFocusView);
					if (bottom - getScrollY() > r.height()) {
						int scrollToY = bottom - r.height();
						smoothScrollTo(0, scrollToY);
					}
				}
			}
		}
	}
	
	private int getBottomOfView(View childView) {
		return getTopOfView(childView) + childView.getHeight();
	}
	
	private int getTopOfView(View childView) {
		int top = childView.getTop();
		ViewGroup parent = (ViewGroup) childView.getParent();
		if (parent != this) top += getTopOfView(parent);
		return top;
	}
	
	public void mapNextView(View target, View next) {
		map.put(target, next);
	}
	
	public void clearNextViewMap() {
		map.clear();
	}
}
