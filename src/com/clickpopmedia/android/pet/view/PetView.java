package com.clickpopmedia.android.pet.view;

import com.clickpopmedia.android.pet.R;
import com.clickpopmedia.android.pet.model.Pet;
import com.clickpopmedia.android.pet.model.Pet.Scenery;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Displays a Pet.
 *
 */
public class PetView extends FrameLayout {
	
	private static final String LOG_TAG = "GPP PetView";

	//private final static int characterColor = Color.parseColor ("#FFCD0121"); 
	private final static int characterColor = Color.parseColor ("#FFFF0000"); 
	//private final static int characterColor = Color.parseColor ("#FFd11532"); 
	
	private ImageView mCharacterColorView;
	
	private ImageView mCharacterFeaturesView;
	
	private ImageView mSceneryView;
	
	public PetView(Context context) {
		super(context);
	}

	public PetView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PetView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mCharacterColorView = (ImageView) findViewById(R.id.characterColorView);
		
		//ColorFilter cf = PorterDuffColorFilter.create(characterColor, Mode.SRC_ATOP); 
		//mCharacterView.setColorFilter(cf); 
		
		mCharacterColorView.setColorFilter(characterColor, Mode.SRC_ATOP);
		
		mCharacterFeaturesView = (ImageView) findViewById(R.id.characterFeaturesView);
		
		mSceneryView = (ImageView) findViewById(R.id.sceneryView);
	}

	public void showPet(Pet pet) {

		boolean showScenery = true;
		switch( pet.getSize() ) {
			case 0:
				mCharacterColorView.setImageResource(R.drawable.character_baby);
				mCharacterFeaturesView.setImageResource(R.drawable.character_baby_features);
				break;
			
			case 1:
				mCharacterColorView.setImageResource(R.drawable.character_child);
				mCharacterFeaturesView.setImageResource(R.drawable.character_child_features);
				break;
			
			case 2:
				mCharacterColorView.setImageResource(R.drawable.character_teen);
				mCharacterFeaturesView.setImageResource(R.drawable.character_teen_features);
				break;
			
			default:			

				if ( pet.isGamer() ) {
					mCharacterColorView.setImageDrawable(null);
					mCharacterFeaturesView.setImageResource(R.drawable.pet_size_3_gamer);	
					showScenery = false;
				} else if ( pet.isPlaya() ) {
					mCharacterColorView.setImageDrawable(null);
					mCharacterFeaturesView.setImageResource(R.drawable.pet_size_3_playa);						
					showScenery = false;
				} else {
					mCharacterColorView.setImageResource(R.drawable.character_adult);
					mCharacterFeaturesView.setImageResource(R.drawable.character_adult_features);					
				}
				break;
		}
		
		if ( showScenery ) {
			switch( pet.getScenery() ) {
				case FOREST:
					mSceneryView.setImageResource(R.drawable.scenery_forest);
					break;
				
				case CAVERN:
					mSceneryView.setImageResource(R.drawable.scenery_cavern);
					break;
				
				case CITY:
					mSceneryView.setImageResource(R.drawable.scenery_city);
					break;
				
				case HILLS:
					mSceneryView.setImageResource(R.drawable.scenery_hills);
					break;
				
				case MOUNTAIN:
					mSceneryView.setImageResource(R.drawable.scenery_mountain);
					break;
				
				default:
					Log.e(LOG_TAG, "Unknown scenery: " + pet.getScenery());
				
			}
		} else {
			mSceneryView.setImageDrawable(null);
		}
		
	}
	
	/*
	private void setBackgroundDrawable(final int id) {
		setBackgroundDrawable(getResources().getDrawable(id));
	}
	*/

}
