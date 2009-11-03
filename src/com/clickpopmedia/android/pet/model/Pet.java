package com.clickpopmedia.android.pet.model;

/**
 * Pet that grows characteristics based on interaction.
 *
 */
public class Pet {
	
	public static final int BOOK_TOY_ID = 0;
	public static final int HARMONICA_TOY_ID = 1;
	public static final int WEIGHTS_TOY_ID = 2;
	public static final int CONTROLLER_TOY_ID = 3;
	
	private int size = 1;
	
	private boolean isGamer = false;
	
	private boolean isPlaya = false;
	
	public void feed() {
		size++;
	}
	
	public void playWith(int toyId) {
		switch( toyId ) {
			case BOOK_TOY_ID :
			case CONTROLLER_TOY_ID :
				isGamer = true;
				isPlaya = false;
			break;
	
			case WEIGHTS_TOY_ID :
			case HARMONICA_TOY_ID :
				isGamer = false;
				isPlaya = true;			
			break;
		}
	}
	
	public int getSize() {
		return size;
	}
	
	public boolean isPlaya() {
		return isPlaya;
	}
	
	public boolean isGamer() {
		return isGamer;
	}

}
