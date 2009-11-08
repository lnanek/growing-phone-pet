package com.clickpopmedia.android.pet.model;

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

	private int mSize;

	private Toy mLastToy;

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
		
		mSize++;
		return mToyCountBook > 0 ? new PoliteEating() : new NoisyEating();
	}

	/**
	 * Play with the pet using a toy.
	 * 
	 * @param toy Toy to use
	 * @return true if the Pet accepted playing with the Toy
	 */
	public Response playWith(final Toy toy) {

		if (toy == mLastToy) {
			return new Tantrum();
		}

		mLastToy = toy;

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
		return mSize;
	}
	
	public Scenery getScenery() {
		return mScenery;
	}

	public boolean isPlaya() {
		return mToyCountWeights > 0 && mToyCountWeights >= mToyCountGameController;
	}

	public boolean isGamer() {
		return mToyCountGameController > 0 && mToyCountGameController > mToyCountWeights;
	}

}
