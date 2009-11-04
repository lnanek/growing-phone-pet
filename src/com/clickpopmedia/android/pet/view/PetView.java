package com.clickpopmedia.android.pet.view;

import com.clickpopmedia.android.pet.R;
import com.clickpopmedia.android.pet.model.Pet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Displays a Pet.
 *
 */
public class PetView extends View {

	public PetView(Context context) {
		super(context);
	}

	public PetView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PetView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void showPet(Pet pet) {
		
		final int size = pet.getSize();
		switch( size ) {
			case 0:
			setBackgroundDrawable(R.drawable.pet_size_0);
			break;
			
			case 1:
			setBackgroundDrawable(R.drawable.pet_size_1);
			break;
			
			case 2:
			setBackgroundDrawable(R.drawable.pet_size_2);
			break;
			
			default:			
			setBackgroundDrawable( 
				pet.isGamer() ? R.drawable.pet_size_3_gamer
					: pet.isPlaya() ? R.drawable.pet_size_3_playa
						: R.drawable.pet_size_3 );
			break;
			
		}
		
	}
	
	private void setBackgroundDrawable(final int id) {
		setBackgroundDrawable(getResources().getDrawable(id));
	}

}
