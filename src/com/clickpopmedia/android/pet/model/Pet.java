package com.clickpopmedia.android.pet.model;

/**
 * Pet that grows characteristics based on interaction.
 * 
 */
public class Pet {

	public enum Toy {
		BOOK, HARMONICA, WEIGHTS, CONTROLLER,
	}

	private int education;
	
	private int strength;
	
	private int gamingExperience;
	
	private int musicAbility;

	private int size;

	private Toy lastToy;

	public Response feed() {
		size++;
		return education > 0 ? new PoliteEating() : new NoisyEating();
	}

	/**
	 * Play with the pet using a toy.
	 * 
	 * @param toy Toy to use
	 * @return true if the Pet accepted playing with the Toy
	 */
	public Response playWith(final Toy toy) {

		if (toy == lastToy) {
			return new Tantrum();
		}

		lastToy = toy;

		switch (toy) {
			case BOOK:
				education++;
				break;

			case CONTROLLER:
				gamingExperience++;
				break;

			case WEIGHTS:
				strength++;
				break;
				
			case HARMONICA:
				musicAbility++;
				break;
		}

		return new Playing();
	}

	/**
	 * Get the current size of the Pet.
	 * 
	 * @return size 0 or higher
	 */
	public int getSize() {
		return size;
	}

	public boolean isPlaya() {
		return strength > 0 && strength >= gamingExperience;
	}

	public boolean isGamer() {
		return gamingExperience > 0 && gamingExperience > strength;
	}

}
