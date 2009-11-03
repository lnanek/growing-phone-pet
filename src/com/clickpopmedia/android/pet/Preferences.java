package com.clickpopmedia.android.pet;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Offer settings options.
 *
 */
public class Preferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
