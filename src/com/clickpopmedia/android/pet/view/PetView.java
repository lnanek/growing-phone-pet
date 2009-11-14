package com.clickpopmedia.android.pet.view;

import static com.clickpopmedia.android.pet.Constants.*;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.AnimationDrawable;
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
	
	//Body needs separate view because LayerDrawable seems to ignore color filter on individual layers.
	private ImageView mBodyView;
	private ImageView mFeaturesView;
	private ImageView mSceneryBackgroundView;
	private ImageView mSceneryForegroundView;
	
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

		mBodyView = (ImageView) findViewById(R.id.bodyView);
		mFeaturesView = (ImageView) findViewById(R.id.featuresView);		
		mSceneryBackgroundView = (ImageView) findViewById(R.id.sceneryBackgroundView);
		mSceneryForegroundView = (ImageView) findViewById(R.id.sceneryForegroundView);
	}

	private void add(final ArrayList<Drawable> drawables, final int drawableId) {
		drawables.add(getResources().getDrawable(drawableId));
	}
	
	private void addAnimation(final ArrayList<Drawable> drawables, final int drawableId) {
		final Drawable drawable = getResources().getDrawable(drawableId);
		drawables.add(drawable);
		startAnimation(drawable);
	}
	
	private void setAnimation(final ImageView view, final int drawableId) {
		final Drawable drawable = getResources().getDrawable(drawableId);
		view.setImageDrawable(drawable);
		startAnimation(drawable);
	}

	private void startAnimation(final Drawable drawable) {
		if ( drawable instanceof AnimationDrawable ) {
			if ( LOG ) Log.d(LOG_TAG, "Starting animation.");

			//XXX start doesn't always work unless we set it to be run later like this..
			post(new Runnable() {

				@Override
				public void run() {
					((AnimationDrawable) drawable).start();
				}
				
			});
		}
	}
	
	public void showPet(final Pet pet) {

		//Clear out previous drawables and color settings.
		mBodyView.setImageDrawable(null);
		mBodyView.setColorFilter(pet.getColor(), Mode.SRC_ATOP);
		mFeaturesView.setImageDrawable(null);
		mSceneryBackgroundView.setImageDrawable(null);
		mSceneryForegroundView.setImageDrawable(null);

		//Set new ones.
		final ArrayList<Drawable> features = new ArrayList<Drawable>();
		boolean showScenery = true;
		switch( pet.getSize() ) {
			case 0:
				updateBabyDrawables(pet, features);				
				break;
			
			case 1:
				mBodyView.setImageResource(R.drawable.character_child);
				
				//XXX Low quality placeholder with jaggies in the mouth area.
				add(features, R.drawable.character_child_features);
				break;
			
			case 2:
				mBodyView.setImageResource(R.drawable.character_teen);
				
				//XXX Low quality placeholder with jaggies in the mouth area.
				add(features, R.drawable.character_teen_features);
				break;
			
			default:			

				if ( pet.isGamer() ) {
					
					//XXX Black bars in landscape mode because character not separate from scenery.
					add(features, R.drawable.pet_size_3_gamer);	
					showScenery = false;
				} else if ( pet.isPlaya() ) {

					//XXX Black bars in landscape mode because character not separate from scenery.
					add(features, R.drawable.pet_size_3_playa);						
					showScenery = false;
				} else {
					mBodyView.setImageResource(R.drawable.character_adult);
					
					//XXX Low quality placeholder with jaggies in the mouth area.
					add(features, R.drawable.character_adult_features);					
				}
				break;
		}

		if ( 0 != features.size() ) {
			mFeaturesView.setImageDrawable(new LayerDrawable(features.toArray(new Drawable[] {})));
		}
		
		if ( showScenery ) {
			switch( pet.getScenery() ) {
				case FOREST:
					mSceneryBackgroundView.setImageResource(R.drawable.scenery_forest_bg);
					break;
				
				case CAVERN:
					mSceneryBackgroundView.setImageResource(R.drawable.scenery_cavern_bg);
					break;
				
				case CITY:
					mSceneryBackgroundView.setImageResource(R.drawable.scenery_city_bg);
					break;
				
				case HILLS:
					mSceneryBackgroundView.setImageResource(R.drawable.scenery_hills_bg);
					break;
				
				case MOUNTAIN:
					mSceneryBackgroundView.setImageResource(R.drawable.scenery_mountain_bg);
					break;
				
				case OCEAN:
					mSceneryBackgroundView.setImageResource(R.drawable.scenery_ocean_bg);
					mSceneryForegroundView.setImageResource(R.drawable.scenery_ocean_fg);
					break;
				
				default:
					Log.e(LOG_TAG, "Unknown scenery: " + pet.getScenery());
			}
		}
	}

	private void updateBabyDrawables(Pet pet, ArrayList<Drawable> features) {
		
		mBodyView.setColorFilter(pet.getColor(), Mode.MULTIPLY);
				
		if ( null != pet.getRecentFood() ) {
			mBodyView.setImageResource(R.drawable.character_baby_eat01_body01);
			add(features, R.drawable.character_baby_eat01_face01);
			return;
		}
		
		final Toy recentToy = pet.getRecentToy();
		if ( null != recentToy ) {
			switch( recentToy ) {
				case BOOK:
					mBodyView.setImageResource(R.drawable.character_baby_reading01_body01);
					add(features, R.drawable.character_baby_reading01_face01);
					add(features, R.drawable.character_baby_reading01_item01);
					return;
					
				case INSTRUMENT:
					mBodyView.setImageResource(R.drawable.character_baby_music01_body01);
					add(features, R.drawable.character_baby_music01_face01);
					add(features, R.drawable.character_baby_music01_item01);
					return;
					
				case WEIGHTS:
					mBodyView.setImageResource(R.drawable.character_baby_lifting01_body01);
					add(features, R.drawable.character_baby_lifting01_face01);
					add(features, R.drawable.character_baby_lifting01_item01);
					return;
					
				case GAME_CONTROLLER:
					mBodyView.setImageResource(R.drawable.character_baby_gaming01_body01);
					add(features, R.drawable.character_baby_gaming01_face01);
					add(features, R.drawable.character_baby_gaming01_item01);
					return;
			}
		}
		
		switch( pet.getState() ) {
			case NORMAL:
				mBodyView.setImageResource(R.drawable.character_baby_normal01_body01);
				addAnimation(features, R.drawable.character_baby_normal_face_animation);
				return;
			case HAPPY:
				mBodyView.setImageResource(R.drawable.character_baby_laugh01_body01);
				addAnimation(features, R.drawable.character_baby_laugh_face_animation);
				return;
			case CRYING:
				setAnimation(mBodyView, R.drawable.character_baby_cry_body_animation);
				addAnimation(features, R.drawable.character_baby_cry_face_animation);
				return;
		}
	}
	
	/*
	private void setBackgroundDrawable(final int id) {
		setBackgroundDrawable(getResources().getDrawable(id));
	}
	*/

}
