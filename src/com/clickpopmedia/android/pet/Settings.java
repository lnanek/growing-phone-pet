package com.clickpopmedia.android.pet;

import static com.clickpopmedia.android.pet.Constants.*;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

/**
 * Offer settings options.
 * 
 */
public class Settings extends PreferenceActivity {

	public static final String NEW_PET_ACTION = "com.clickpopmedia.android.pet.intent.NEW_PET_ACTION";

	private static final String LOG_TAG = "GPP Settings";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		Preference soundTriggers = (Preference) findPreference("newPet");
		soundTriggers.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if ( LOG ) Log.d(LOG_TAG, "sending NEW_PET_ACTION Intent.");
				Intent i = new Intent(NEW_PET_ACTION);
				sendBroadcast(i);				
				return false;
			}			
		});
	}
}
