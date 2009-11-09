package com.clickpopmedia.android.pet.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.clickpopmedia.android.pet.R;
import com.clickpopmedia.android.pet.model.Pet;
import com.clickpopmedia.android.pet.model.Pet.Toy;

/**
 * Displays a Pet.
 *
 */
public class PetView extends FrameLayout {
	
	private static final String LOG_TAG = "GPP PetView";
	
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
				
		mCharacterFeaturesView = (ImageView) findViewById(R.id.characterFeaturesView);
		
		mSceneryView = (ImageView) findViewById(R.id.sceneryView);
	}

	private void add(final ArrayList<Drawable> drawables, final int drawableId) {
		drawables.add(getResources().getDrawable(drawableId));
	}
	
	public void showPet(final Pet pet) {

		//Putting all layers in one LayerDrawable seemed to ignore color filters 
		//on the drawables, so we use a separate view for the colored body.
		mCharacterColorView.setColorFilter(pet.getColor(), Mode.SRC_ATOP);

		final ArrayList<Drawable> features = new ArrayList<Drawable>();
		boolean showScenery = true;
		switch( pet.getSize() ) {
			case 0:
				updateBabyDrawables(pet, features);				
				break;
			
			case 1:
				mCharacterColorView.setImageResource(R.drawable.character_child);
				
				//XXX Low quality placeholder with jaggies in the mouth area.
				add(features, R.drawable.character_child_features);
				break;
			
			case 2:
				mCharacterColorView.setImageResource(R.drawable.character_teen);
				
				//XXX Low quality placeholder with jaggies in the mouth area.
				add(features, R.drawable.character_teen_features);
				break;
			
			default:			

				if ( pet.isGamer() ) {
					mCharacterColorView.setImageDrawable(null);
					
					//XXX Black bars in landscape mode because character not separate from scenery.
					add(features, R.drawable.pet_size_3_gamer);	
					showScenery = false;
				} else if ( pet.isPlaya() ) {
					mCharacterColorView.setImageDrawable(null);

					//XXX Black bars in landscape mode because character not separate from scenery.
					add(features, R.drawable.pet_size_3_playa);						
					showScenery = false;
				} else {
					mCharacterColorView.setImageResource(R.drawable.character_adult);
					
					//XXX Low quality placeholder with jaggies in the mouth area.
					add(features, R.drawable.character_adult_features);					
				}
				break;
		}
		
		mCharacterFeaturesView.setImageDrawable(new LayerDrawable(features.toArray(new Drawable[] {})));
		
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

	private void updateBabyDrawables(Pet pet, ArrayList<Drawable> features) {
		
		mCharacterColorView.setColorFilter(pet.getColor(), Mode.MULTIPLY);
				
		if ( null != pet.getRecentFood() ) {
			mCharacterColorView.setImageResource(R.drawable.character_baby_eat01_body01);
			add(features, R.drawable.character_baby_eat01_face01);
			return;
		}
		
		final Toy recentToy = pet.getRecentToy();
		if ( null != recentToy ) {
			switch( recentToy ) {
				case BOOK:
					mCharacterColorView.setImageResource(R.drawable.character_baby_reading01_body01);
					add(features, R.drawable.character_baby_reading01_item01);
					add(features, R.drawable.character_baby_reading01_face01);
					return;
					
				case INSTRUMENT:
					mCharacterColorView.setImageResource(R.drawable.character_baby_music01_body01);
					add(features, R.drawable.character_baby_music01_item01);
					add(features, R.drawable.character_baby_music01_face01);
					return;
					
				case WEIGHTS:
					mCharacterColorView.setImageResource(R.drawable.character_baby_lifting01_body01);
					add(features, R.drawable.character_baby_lifting01_item01);
					add(features, R.drawable.character_baby_lifting01_face01);
					return;
					
				case GAME_CONTROLLER:
					mCharacterColorView.setImageResource(R.drawable.character_baby_gaming01_body01);
					add(features, R.drawable.character_baby_gaming01_item01);
					add(features, R.drawable.character_baby_gaming01_face01);
					return;
			}
		}
		
		mCharacterColorView.setImageResource(R.drawable.character_baby_normal01_body01);
		add(features, R.drawable.character_baby_normal01_face01);
	}
	
	/*
	private void setBackgroundDrawable(final int id) {
		setBackgroundDrawable(getResources().getDrawable(id));
	}
	*/

}
