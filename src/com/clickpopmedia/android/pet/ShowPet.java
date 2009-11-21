package com.clickpopmedia.android.pet;

import static com.clickpopmedia.android.pet.Constants.*;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.clickpopmedia.android.pet.db.DbAdapter;
import com.clickpopmedia.android.pet.media.Effects;
import com.clickpopmedia.android.pet.model.Pet;
import com.clickpopmedia.android.pet.model.Pet.Food;
import com.clickpopmedia.android.pet.model.Pet.Toy;
import com.clickpopmedia.android.pet.view.PetView;

/**
 * Show pet and offer options to interact.
 * 
 */
public class ShowPet extends TabActivity {

	private static final String LOG_TAG = "GPP ShowPet";
	
	//Constants for referring to tabs on the screen.
	private static final String EXPAND_COLLAPSE_TAB = "openclose_tab";
	private static final String SETTINGS_TAB = "settings_tab";
	private static final String FOOD_TAB = "food_tab";
	private static final String FUN_TAB = "fun_tab";	
	
	private static final String WAKE_LOCK_TAG = ShowPet.class.getPackage().getName();
	
	private WakeLock mWakeLock;
	
	private Effects mEffects;
	
	private PetView mPetView;

	private Pet mPet;
	
	private String mPreviousTabTag = EXPAND_COLLAPSE_TAB;
	
	private LevelListDrawable mTabOpenCloseIndicator;
	
    private GestureDetector mGestureDetector;
    
	private boolean mTabsCollapsed = true;
	
	private TabHost mTabHost;

	private BroadcastReceiver mNewPetReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if ( LOG ) Log.d(LOG_TAG, "Received new pet intent.");
			mPet = new Pet(mPet);
			mPetView.showPet(mPet);
			setTabsCollapsed(true);
		}
	};
	
    private DbAdapter mDb;
    	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        mDb = new DbAdapter(this).open();
        
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mGestureDetector = new GestureDetector(this, new FlingDetector(this));
		
		final Object savedPet = getLastNonConfigurationInstance();
		if ( null != savedPet && savedPet instanceof Pet ) {
			mPet = (Pet) savedPet;
		} else {
			mPet = mDb.load();
		}
		
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, WAKE_LOCK_TAG);
		
		mEffects = new Effects();
		
		setContentView(R.layout.show_pet);
		mPetView = (PetView) findViewById(R.id.petView);
		mPetView.showPet(mPet);
		
		//Setup tabs. Special in that they can collapse down to just the indicators.
		//TODO Consider implementing own tabs instead of hacking the built in stuff like this.
		mTabHost = getTabHost();
		final ViewGroup tabWidget = (ViewGroup) findViewById(android.R.id.tabs);
		final ViewGroup tabContentView = mTabHost.getTabContentView();
		LayoutInflater.from(this).inflate(R.layout.tab_contents, tabContentView, true);
		
		mTabHost.addTab(mTabHost.newTabSpec(FUN_TAB)
			.setIndicator(createTextOnlyIndicator(tabWidget, R.string.tab_indicator_fun))
			.setContent(R.id.funTabContent));
		mTabHost.addTab(mTabHost.newTabSpec(FOOD_TAB)
			.setIndicator(createTextOnlyIndicator(tabWidget, R.string.tab_indicator_food))
			.setContent(R.id.foodTabContent));
		mTabHost.addTab(mTabHost.newTabSpec(SETTINGS_TAB)
			.setIndicator(createTextOnlyIndicator(tabWidget, R.string.tab_indicator_settings))
		    .setContent(new Intent(this, Settings.class)));
		
		//This last tab has no content and just uses the indicator as a collapse/expand control.
		mTabOpenCloseIndicator = (LevelListDrawable) getResources().getDrawable(R.drawable.open_close_level_list);
		mTabHost.addTab(mTabHost.newTabSpec(EXPAND_COLLAPSE_TAB)
			.setIndicator(createImageOnlyIndicator(tabWidget, mTabOpenCloseIndicator))
			.setContent(R.id.emptyTabContent));
		
		mTabHost.setCurrentTabByTag(EXPAND_COLLAPSE_TAB);
		final View opencloseTabIndicator = tabWidget.getChildAt(3);
				
		//Toggle the tabs between expanded and collapsed when the control is clicked.
		opencloseTabIndicator.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
				if ( LOG ) Log.d(LOG_TAG, "OPENCLOSE TAG clicked.");
				toggleTabsCollapsed();
			}
		});

		//Close tabs if the indicator we use as the open/close control is focused.
		opencloseTabIndicator.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if ( !hasFocus ) return;
				
				if ( LOG ) Log.d(LOG_TAG, "OPENCLOSE TAG focused.");

				if ( !mTabsCollapsed ) {
					setTabsCollapsed(true);
				}
			}
		});		
		
		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				
				//Expand the tabs if the user picked one while they were collapsed.
				if ( EXPAND_COLLAPSE_TAB != tabId) {
					setTabsCollapsed(false);
				}
				
				//Reload the settings if we left the settings tab.
				if ( SETTINGS_TAB == mPreviousTabTag ) {
					loadSettings();
				}
				
				mPreviousTabTag = tabId;
			}			
		});
				
		//Setup buttons.
		addFoodClickListener(R.id.foodButtonMeat, Food.MEAT);
		addFoodClickListener(R.id.foodButtonSweets, Food.SWEETS);
		addFoodClickListener(R.id.foodButtonVegetable, Food.VEGETABLE);
		addFunClickListener(R.id.funButtonBook, Toy.BOOK);
		addFunClickListener(R.id.funButtonWeights, Toy.WEIGHTS);
		addFunClickListener(R.id.funButtonInstrument, Toy.INSTRUMENT);
		addFunClickListener(R.id.funButtonGameController, Toy.GAME_CONTROLLER);
		
	}
	
	/**
	 * Create a tab indicator that has only a centered image.
	 * 
	 */
    public View createImageOnlyIndicator(ViewGroup tabWidget, Drawable drawable) {

        View tabIndicator = LayoutInflater.from(this).inflate(
        	R.layout.tab_indicator_image,
            tabWidget,
            false); // no inflate params

        final ImageView iv = (ImageView) tabIndicator.findViewById(R.id.icon);
        iv.setImageDrawable(drawable);

        return tabIndicator;
    }	

    /**
     * Create a tab indicator that has only centered text.
     * 
     */
    public View createTextOnlyIndicator(ViewGroup tabWidget, int resid) {

        View tabIndicator = LayoutInflater.from(this).inflate(
        	R.layout.tab_indicator_text,
            tabWidget,
            false); // no inflate params

        final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
        tv.setText(resid);

        return tabIndicator;
    }    
    
	/**
	 * Collapse tabs as per {@link #setTabsCollapsed(boolean)}
	 * if they are currently open.
	 * Otherwise close them via the same method.
	 */
	private void toggleTabsCollapsed() {
				
		if ( mTabsCollapsed ) {
			setTabsCollapsed(false);
			mTabHost.setCurrentTabByTag(FUN_TAB);
		} else {
			setTabsCollapsed(true);
		}
	}
	
	/**
	 * Collapse or expand the tabbed content area of the screen.
	 * Collapsed tabs show only their indicators, not the current
	 * tab content and take less room on the screen.
	 * 
	 * @param collapsed boolean true to collapse tabs, false to expand
	 */
	private void setTabsCollapsed(boolean collapsed) {
		
		mTabsCollapsed = collapsed;
				
		if ( collapsed ) {
			mTabHost.setCurrentTabByTag(EXPAND_COLLAPSE_TAB);
			mTabOpenCloseIndicator.selectDrawable(0);
		} else {
			mTabOpenCloseIndicator.selectDrawable(1);
		}		
	}

	/**
	 * Add action for food buttons.
	 */
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

	/**
	 * Add action for toy buttons.
	 */
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
		if ( keepAwake && !mWakeLock.isHeld() ) {
			mWakeLock.acquire();		
		}
		
		mEffects.onActivityUpdated(getApplicationContext(), settings);
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if ( LOG ) Log.d(LOG_TAG, "onResume()");
		
		registerReceiver(mNewPetReceiver , new IntentFilter(Settings.NEW_PET_ACTION));
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if ( LOG ) Log.d(LOG_TAG, "onPause()");
		
		unregisterReceiver(mNewPetReceiver);
		mDb.save(mPet);
	}	
	
	@Override
	protected void onStop(){
		 super.onStop();
		 
		 if ( LOG ) Log.d(LOG_TAG, "onStop()");
		 
		 if ( mWakeLock.isHeld() ) {
			 mWakeLock.release();
		 }

	}	 

	@Override
	public Object onRetainNonConfigurationInstance() {
		//Preserve the model across configuration changes.
		return mPet;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEffects.release();
		mDb.close();
	}	

	/**
	 * Allow the menu and other keys to open and close the tabs.
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if ( KeyEvent.KEYCODE_VOLUME_UP == keyCode
			|| KeyEvent.KEYCODE_VOLUME_DOWN == keyCode ) {
			return false;
		}
		
		toggleTabsCollapsed();
		return true;	
	}

	/**
	 * Tapping the screen affects the pet.
	 * Swiping changes the scenery.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		//Check for a gesture.
		if ( mGestureDetector.onTouchEvent(event) )
			return true;	
		
		//Otherwise handle as a tap.
		if ( MotionEvent.ACTION_UP == event.getAction() ) {
			mPet.tap();
			mPetView.showPet(mPet);
			return true;	
		}
		
		return super.onTouchEvent(event);
	}

	//Change scenery on left and right flings.
	
	public void onFlingLeft() {
		mPet.previousScene();
		mPetView.showPet(mPet);
	}	
	
	public void onFlingRight() {
		mPet.nextScene();
		mPetView.showPet(mPet);
	}
	
}