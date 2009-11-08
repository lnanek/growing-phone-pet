package com.clickpopmedia.android.pet;

import static com.clickpopmedia.android.pet.Constants.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

//Based on example at http://www.codeshogun.com/blog/2009/04/16/how-to-implement-swipe-action-in-android/ .
public class FlingDetector extends SimpleOnGestureListener {

	private static final String LOG_TAG = "GPP FlingDetector";
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	
	//private static final int SWIPE_MAX_OFF_PATH = 250;
	
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	
	protected ShowPet showPet;
	
	public FlingDetector(ShowPet score) {
		super();
		this.showPet = score;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) {

		//Swipe left.
		if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			if ( LOG ) Log.v(LOG_TAG, "Left fling detected.");
			
			showPet.onFlingLeft();
			return true;
		}

		//Swipe right.
		if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			if ( LOG ) Log.v(LOG_TAG, "Right fling detected.");
			
			showPet.onFlingRight();
			return true;
		}		

		if ( LOG ) Log.v(LOG_TAG, "Weak fling detected. Ignoring.");
		return false;
	}

}
