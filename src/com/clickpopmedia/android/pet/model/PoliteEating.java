package com.clickpopmedia.android.pet.model;

import com.clickpopmedia.android.pet.media.Effects;

public class PoliteEating extends Response {

	@Override
	public void run(Effects effects) {
		effects.politeEat();
	}

}
