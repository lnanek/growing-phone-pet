package com.clickpopmedia.android.pet.model;

import java.util.Random;

import android.graphics.Color;

/**
 * Pet that grows characteristics based on interaction.
 * 
 */
public class Pet {

	public enum Toy {
		BOOK, INSTRUMENT, WEIGHTS, GAME_CONTROLLER,
	}
	
	public enum Food {
		MEAT, SWEETS, VEGETABLE,
	}
	
	public enum Scenery {
		CAVERN, CITY, FOREST, HILLS, MOUNTAIN
	}
	
	public static final int[] COLORS = new int[] {
		Color.parseColor("#ffcd0122"),//Red.
		Color.parseColor("#ff1fa7e2"),//Blue.
		Color.parseColor("#ff04ac52"),//Green.
		Color.parseColor("#fff84d0c"),//Orange.
		Color.parseColor("#fffdf115"),//Yellow.
	};

	//Just randomly pick a color for now.
	private int mColor = COLORS[new Random().nextInt(COLORS.length)];
	
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

		return new Playing();
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
		return mColor;
	}

	public boolean isPlaya() {
		return mToyCountWeights > 0 && mToyCountWeights >= mToyCountGameController;
	}

	public boolean isGamer() {
		return mToyCountGameController > 0 && mToyCountGameController > mToyCountWeights;
	}

}
