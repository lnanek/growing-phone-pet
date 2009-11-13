package com.clickpopmedia.android.pet.model;

import com.clickpopmedia.android.pet.media.Effects;
import com.clickpopmedia.android.pet.model.Pet.Toy;

public class Playing extends Response {

	final Toy mToy;
	
	final int mSkill;
	
	Playing(Toy toy, int skill) {
		super();
		this.mToy = toy;
		this.mSkill = skill;
	}

	@Override
	public void run(Effects effects) {
		
		
		if ( Toy.INSTRUMENT == mToy ) {
			switch(mSkill) {
				case 1:
					effects.msuicBasic();
					return;
				case 2:
					effects.msuicNormal();
					return;
				default:
					effects.msuicSkilled();
					return;
			}
		}
		
		effects.play();
	}

}
