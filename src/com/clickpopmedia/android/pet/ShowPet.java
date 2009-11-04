package com.clickpopmedia.android.pet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.clickpopmedia.android.pet.media.Effects;
import com.clickpopmedia.android.pet.model.Pet;
import com.clickpopmedia.android.pet.model.Pet.Toy;
import com.clickpopmedia.android.pet.view.PetView;

/**
 * Show the current pet and offer options to interact with it.
 * 
 */
public class ShowPet extends Activity {
	//TODO Persist the pet characteristics over multiple runs.

	private static final String LOG_TAG = "ShowPet";
	
	private static final boolean LOG = true;
	
	private static final String WAKE_LOCK_TAG = ShowPet.class.getPackage().getName();
	
    private static final int CHOOSE_TOY_DIALOG_ID = 1;
    
    private WakeLock mWakeLock;
    
    private Effects mEffects;
    
	private PetView mPetView;

	private Pet mPet;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		Object savedPet = getLastNonConfigurationInstance();
		if ( null != savedPet && savedPet instanceof Pet ) {
			mPet = (Pet) savedPet;
		} else {
			mPet = new Pet();
		}
		
    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, WAKE_LOCK_TAG);
    	
    	mEffects = new Effects();
		
		setContentView(R.layout.main);
		mPetView = (PetView) findViewById(R.id.petView);
		mPetView.showPet(mPet);
	}
	
    @Override
	protected void onStart() {
		super.onStart();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        boolean keepAwake = settings.getBoolean("keepAwake", true);
        if ( keepAwake ) {
        	mWakeLock.acquire();    	
        }
        
        mEffects.onActivityUpdated(getApplicationContext(), settings);
    }
    
    @Override
    protected void onStop(){
       super.onStop();
       
       if ( mWakeLock.isHeld() ) {
    	   mWakeLock.release();
       }

    }     

	//TODO Make the menu look more like the graphic design in the doc directory.
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

    @Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		switch (item.getItemId()) {
		case R.id.fun_menu:
			showDialog(CHOOSE_TOY_DIALOG_ID);
			return true;

		case R.id.food_menu:
			mPet.feed().run(mEffects);;
			mPetView.showPet(mPet);
			return true;

		case R.id.settings_menu:
    		startActivity(new Intent(this, Preferences.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
 
    @Override
	protected Dialog onCreateDialog(final int id) {
    			
    	switch ( id ) {
    		case CHOOSE_TOY_DIALOG_ID:
		    	final CharSequence[] items = {
		    		getString(R.string.book_toy),
		    		getString(R.string.harmonica_toy),
		    		getString(R.string.weights_toy),
		    		getString(R.string.controller_toy),
		    	};
		
		    	return new AlertDialog.Builder(this)
		    		.setTitle(R.string.choose_toy_dialog_title)
		    		.setItems(items, new DialogInterface.OnClickListener() {
			    	    public void onClick(DialogInterface dialog, int item) {
			    	    	final Toy toy;
			    	    	switch( item ) {
			    	    		case 0:
			    	    			toy = Pet.Toy.BOOK;
			    	    			break;
			    	    		case 1:
			    	    			toy = Pet.Toy.HARMONICA;
			    	    			break;   	    	
			    	    		case 2:
			    	    			toy = Pet.Toy.WEIGHTS;
			    	    			break;
			    	    		case 3:
			    	    			toy = Pet.Toy.CONTROLLER;
			    	    			break;
			    	    		default:
			    	    			if ( LOG ) Log.e(LOG_TAG, "Unhandled choice in toy choosing dialog. Ignoring.");
			    	    			return;
			    	    	}
			    	    	
			    			mPet.playWith(toy).run(mEffects);
			    			mPetView.showPet(mPet);
			    			return;
			    	    }
		    		})
		    		.create();
    	}
    	
    	return super.onCreateDialog(id);
		
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return mPet;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEffects.release();
	}	

    
    
}