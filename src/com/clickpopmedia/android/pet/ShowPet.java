package com.clickpopmedia.android.pet;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LevelListDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.clickpopmedia.android.pet.media.Effects;
import com.clickpopmedia.android.pet.model.Pet;
import com.clickpopmedia.android.pet.model.Pet.Food;
import com.clickpopmedia.android.pet.model.Pet.Toy;
import com.clickpopmedia.android.pet.view.PetView;

/**
 * Show the current pet and offer options to interact with it.
 * 
 */
public class ShowPet extends TabActivity {
	//TODO Persist the pet characteristics over multiple runs.

	private static final String OPENCLOSE_TAB_TAG = "openclose_tab";

	private static final String SETTINGS_TAB_TAG = "settings_tab";

	private static final String FOOD_TAB_TAG = "food_tab";

	private static final String FUN_TAB_TAG = "fun_tab";
	
	//private static final String HIDDEN_TAB_TAG = "hidden_tab";

	private static final String LOG_TAG = "ShowPet";
	
	private static final boolean LOG = true;
	
	private static final String WAKE_LOCK_TAG = ShowPet.class.getPackage().getName();
	
	//private static final int CHOOSE_TOY_DIALOG_ID = 1;
	
	private WakeLock mWakeLock;
	
	private Effects mEffects;
	
	private PetView mPetView;

	private Pet mPet;
	
	private String mPreviousTabTag = OPENCLOSE_TAB_TAG;
	
	private LevelListDrawable mOpenCloseDrawable;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		final Object savedPet = getLastNonConfigurationInstance();
		if ( null != savedPet && savedPet instanceof Pet ) {
			mPet = (Pet) savedPet;
		} else {
			mPet = new Pet();
		}
		
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, WAKE_LOCK_TAG);
		
		mEffects = new Effects();
		
		setContentView(R.layout.main);
		mPetView = (PetView) findViewById(R.id.petView);
		mPetView.showPet(mPet);
		
		//Set up tabs. They are somewhat weird in that they can collapse down to just the indicators.
		//TODO Consider implementing own tabs instead of hacking the built in stuff like this.
		final TabHost tabHost = getTabHost();
		final ViewGroup tabContentView = tabHost.getTabContentView();
		LayoutInflater.from(this).inflate(R.layout.tabs, tabContentView, true);
		
		tabHost.addTab(tabHost.newTabSpec(FUN_TAB_TAG)
			.setIndicator(getText(R.string.tab_indicator_fun))
			.setContent(R.id.funTabContent));
		tabHost.addTab(tabHost.newTabSpec(FOOD_TAB_TAG)
			.setIndicator(getText(R.string.tab_indicator_food))
			.setContent(R.id.foodTabContent));
		tabHost.addTab(tabHost.newTabSpec(SETTINGS_TAB_TAG)
			.setIndicator(getText(R.string.tab_indicator_settings))
			//.setContent(R.id.emptyTabContent));		
		    .setContent(new Intent(this, Preferences.class)));
		
		/*
		tabHost.addTab(tabHost.newTabSpec(HIDDEN_TAB_TAG)
			.setIndicator("")
			.setContent(R.id.emptyTabContent));	
		final int hiddenTabIndex = 3;
		*/
		/*
		TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		View hiddenTabIndicator = tabWidget.getChildAt(hiddenTabIndex);
		hiddenTabIndicator.setVisibility(View.INVISIBLE);
		*/
				
		mOpenCloseDrawable = (LevelListDrawable) getResources().getDrawable(R.drawable.open_close_level_list);
		tabHost.addTab(tabHost.newTabSpec(OPENCLOSE_TAB_TAG)
			.setIndicator("", mOpenCloseDrawable)
			.setContent(R.id.emptyTabContent));
		
		tabHost.setCurrentTabByTag(OPENCLOSE_TAB_TAG);
		final TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		final View opencloseTabIndicator = tabWidget.getChildAt(3);
				
		opencloseTabIndicator.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
				if ( LOG ) Log.d(LOG_TAG, "OPENCLOSE TAG clicked.");

				if ( View.VISIBLE == tabContentView.getVisibility() ) {
					setTabsCollapsed(true);
				} else {
					setTabsCollapsed(false);
					tabHost.setCurrentTabByTag(FUN_TAB_TAG);
				}
			}
		});


		opencloseTabIndicator.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if ( !hasFocus ) return;
				
				if ( LOG ) Log.d(LOG_TAG, "OPENCLOSE TAG focused.");

				if ( View.VISIBLE == tabContentView.getVisibility() ) {
					setTabsCollapsed(true);
				}
			}
		});		
		
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				
				//Expand the tabs if the user picked one while they were collapsed.
				if ( OPENCLOSE_TAB_TAG != tabId) {
					setTabsCollapsed(false);
				}
				
				//Reload the settings if we left the settings tab.
				if ( SETTINGS_TAB_TAG == mPreviousTabTag ) {
					loadSettings();
				}
				
				mPreviousTabTag = tabId;
			}			
		});
		
		addFoodClickListener(R.id.foodButtonMeat, Food.MEAT);
		addFoodClickListener(R.id.foodButtonSweets, Food.SWEETS);
		addFoodClickListener(R.id.foodButtonVegetable, Food.VEGETABLE);
		addFunClickListener(R.id.funButtonBook, Toy.BOOK);
		addFunClickListener(R.id.funButtonWeights, Toy.WEIGHTS);
		addFunClickListener(R.id.funButtonInstrument, Toy.INSTRUMENT);
		addFunClickListener(R.id.funButtonGameController, Toy.GAME_CONTROLLER);
		
	}
	
	private void setTabsCollapsed(boolean collapsed) {
		
		TabHost tabHost = getTabHost();
		View tabContent = tabHost.getTabContentView();
		
		if ( collapsed ) {
			tabContent.setVisibility(View.GONE);
			tabHost.setCurrentTabByTag(OPENCLOSE_TAB_TAG);
			mOpenCloseDrawable.selectDrawable(0);
		} else {
			tabContent.setVisibility(View.VISIBLE);
			mOpenCloseDrawable.selectDrawable(1);
		}		
	}
	
	private void addFoodClickListener(final int buttonId, final Food food) {
		findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPet.feed(food).run(mEffects);;
				mPetView.showPet(mPet);			
				setTabsCollapsed(true);
			}
		});		
	}

	private void addFunClickListener(final int buttonId, final Toy toy) {
		findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPet.playWith(toy).run(mEffects);;
				mPetView.showPet(mPet);		
				setTabsCollapsed(true);
			}
		});		
	}	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if ( LOG ) Log.d(LOG_TAG, "onStart()");

		loadSettings();
	}
	
	private void loadSettings() {

		if ( LOG ) Log.d(LOG_TAG, "loadSettings()");
		
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
		 
		 if ( LOG ) Log.d(LOG_TAG, "onStop()");
		 
		 if ( mWakeLock.isHeld() ) {
			 mWakeLock.release();
		 }

	}	 

	/*
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
			mPet.feed(Food.MEAT).run(mEffects);;
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
									toy = Pet.Toy.INSTRUMENT;
									break;	 			
								case 2:
									toy = Pet.Toy.WEIGHTS;
									break;
								case 3:
									toy = Pet.Toy.GAME_CONTROLLER;
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
	*/

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