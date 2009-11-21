package com.clickpopmedia.android.pet.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Text only tab indicator that changes style and color according to state.
 *
 */
public class CompactTabIndicator extends TextView {

	private static final int BLACK = Color.parseColor("#000");
	
	private static final int WHITE = Color.parseColor("#fff");
	
	public CompactTabIndicator(Context context) {
		super(context);
	}

	public CompactTabIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CompactTabIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		
		int[] states = getDrawableState();
		for( int state : states ) {
			switch( state ) {
				case android.R.attr.state_focused:
					setTypeface(getTypeface(), Typeface.BOLD);
					setTextColor(WHITE);
					break;
				default:
					setTypeface(getTypeface(), Typeface.NORMAL);
					setTextColor(WHITE);
					break;
			}
			
			
		}
	}
	


}
