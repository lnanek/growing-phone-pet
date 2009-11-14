package com.clickpopmedia.android.pet.model;

import static com.clickpopmedia.android.pet.Constants.*;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;

/**
 * Pet that grows characteristics based on interaction.
 * 
 */
public class Pet {

	public enum State {
		NORMAL, HAPPY, CRYING
	}
	
	public enum Toy {
		BOOK, INSTRUMENT, WEIGHTS, GAME_CONTROLLER,
	}
	
	public enum Food {
		MEAT, SWEETS, VEGETABLE,
	}
	
	public enum Scenery {
		CAVERN, CITY, FOREST, HILLS, MOUNTAIN, OCEAN
	}
	
	public static final int[] COLORS = new int[] {
		Color.parseColor("#ffcd0122"),//Red.
		Color.parseColor("#ff1fa7e2"),//Blue.
		Color.parseColor("#ff04ac52"),//Green.
		Color.parseColor("#fff84d0c"),//Orange.
		Color.parseColor("#fffdf115"),//Yellow.
	};
	
	private static final String LOG_TAG = "GPP Pet";
	
	private static final long TOUCH_MEMORY_MS = 500;

	private int mColorIndex;
	
	private Toy mRecentToy;
	
	private Food mRecentFood;

	//How many times the pet has played with each toy.
	
	private int mToyCountBook;

	private int mToyCountInstrument;
	
	private int mToyCountWeights;
	
	private int mToyCountGameController;
	
	//How many times the pet has eaten each food.
	
	private int mFoodCountMeat;

	private int mFoodCountSweets;
	
	private int mFoodCountVegetable;
	
	private Scenery mScenery = Scenery.FOREST;
	
	private State mState = State.NORMAL;
	
	private List<Long> mRecentTouchTimes = new LinkedList<Long>();
	
	public Pet() {
		this(null);
	}
	
	public Pet(Pet previous) {
		if ( null == previous ) {
			//Just randomly pick a color.
			mColorIndex = new Random().nextInt(COLORS.length);
		} else {
			//Use next color to ensure user knows pet changed when getting a new one.
			mColorIndex = ( previous.mColorIndex + 1 ) % COLORS.length;
		}
	}

	public Response feed(final Food food) {
		
		mRecentFood = food;
		mRecentToy = null;
		
		switch ( food ) {
			case MEAT:
				mFoodCountMeat++;
				break;

			case SWEETS:
				mFoodCountSweets++;
				break;

			case VEGETABLE:
				mFoodCountVegetable++;
				break;
		}
		
		return mToyCountBook > 0 ? new PoliteEating() : new NoisyEating();
	}

	/**
	 * Play with the pet using a toy.
	 * 
	 * @param toy Toy to use
	 * @return true if the Pet accepted playing with the Toy
	 */
	public Response playWith(final Toy toy) {

		if (toy == mRecentToy) {
			return new Tantrum();
		}

		mRecentFood = null;
		mRecentToy = toy;

		switch (toy) {
			case BOOK:
				mToyCountBook++;
				break;

			case GAME_CONTROLLER:
				mToyCountGameController++;
				break;

			case WEIGHTS:
				mToyCountWeights++;
				break;
				
			case INSTRUMENT:
				mToyCountInstrument++;
				break;
		}

		return new Playing(toy, mToyCountInstrument);
	}
	
	public void nextScene() {
		Scenery[] sceneryValues = Scenery.values();
		int nextSceneryIndex = ( mScenery.ordinal() + 1 ) % sceneryValues.length;
		mScenery = sceneryValues[nextSceneryIndex];
	}
	
	public void previousScene() {
		Scenery[] sceneryValues = Scenery.values();
		int previousSceneryIndex = mScenery.ordinal() - 1;
		if ( -1 == previousSceneryIndex ) {
			previousSceneryIndex = sceneryValues.length - 1;
		}
		mScenery = sceneryValues[previousSceneryIndex];
	}	

	/**
	 * Get the current size of the Pet.
	 * 
	 * @return size 0 or higher
	 */
	public int getSize() {
		return (mFoodCountMeat + mFoodCountSweets + mFoodCountVegetable) / 2;
	}
	
	public Scenery getScenery() {
		return mScenery;
	}

	public Toy getRecentToy() {
		return mRecentToy;
	}

	public Food getRecentFood() {
		return mRecentFood;
	}

	public int getColor() {
		return COLORS[mColorIndex];
	}

	public boolean isPlaya() {
		return mToyCountWeights > 0 && mToyCountWeights >= mToyCountGameController;
	}

	public boolean isGamer() {
		return mToyCountGameController > 0 && mToyCountGameController > mToyCountWeights;
	}

	/**
	 * Pet forgets recent toy/food with a tap,
	 * is happy with a tap otherwise,
	 * and gets upset with too frequent tapping.
	 * 
	 */
	public void tap() {
		//TODO use crying for shaked instead.
			
		//Forget taps that aren't recent.
		final long now = SystemClock.uptimeMillis();
		final long removeTime = now - TOUCH_MEMORY_MS;
		for( final ListIterator<Long> i = mRecentTouchTimes.listIterator(); i.hasNext(); ) {
			final long previousTouchTime = i.next();		
			if ( previousTouchTime < removeTime ) {
				i.remove();
			}
		}

		//Reset last food/toy if we just did that instead of counting as tap.
		if ( null != mRecentFood || null != mRecentToy ) {
			mRecentToy = null;
			mRecentFood = null;			
		
		//Count as a tap.
		} else {
			mRecentTouchTimes.add(now);
		}
		
		//Decide response.
		if ( LOG ) Log.d(LOG_TAG, "Recent touches are: " + mRecentTouchTimes);		
		switch( mRecentTouchTimes.size() ) {
			case 0:
				if ( LOG ) Log.d(LOG_TAG, "Pet state set to normal.");
				mState = State.NORMAL;
			case 1:
				if ( LOG ) Log.d(LOG_TAG, "Pet state set to happy.");
				mState = State.HAPPY;
				break;
			default:
				if ( LOG ) Log.d(LOG_TAG, "Pet state set to crying.");
				mState = State.CRYING;
		}
	}
	
	public State getState() {
		return mState;
	}

}
