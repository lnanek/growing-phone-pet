package com.clickpopmedia.android.pet.media;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.widget.Toast;

import com.clickpopmedia.android.pet.R;

/**
 * Trigger sounds, vibrations, and pop up messages.
 * 
 */
public class Effects {
	
	protected static final int VIBE_PULSE_LENGTH = 150;
	
	protected static final long[] TANTRUM_VIBE = new long[] {0, VIBE_PULSE_LENGTH, 
		3 * VIBE_PULSE_LENGTH, 2 * VIBE_PULSE_LENGTH, 3 * VIBE_PULSE_LENGTH, VIBE_PULSE_LENGTH, 
		2 * VIBE_PULSE_LENGTH, VIBE_PULSE_LENGTH};
	
	protected SoundPool soundPool;
	
	protected int politeEatingSoundID;
	protected int noisyEatingSoundID;

	protected Toast playToast;
	protected Toast foodToast;
	protected Toast tantrumToast;
	
	protected Context context;
	
	protected Vibrator vibrator;
	
	public Effects() {
	}	
	
	protected Toast makeToast(CharSequence message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		//toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		return toast;
	}
	
	/**
	 * Process updated context and settings. 
	 * Preserve previous SoundPool if possible.
	 * 
	 */
	public void onActivityUpdated(Context context, SharedPreferences settings) {
		
		this.context = context;
		
		playToast = makeToast(context.getText(R.string.play_message));
		foodToast = makeToast(context.getText(R.string.food_message));	
		tantrumToast = makeToast(context.getText(R.string.tantrum_message));	

		boolean playSound = settings.getBoolean("playSound", true);
		if ( playSound) {
			if ( null == soundPool ) {
				soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
				politeEatingSoundID = soundPool.load(context, R.raw.quick_crunch, 1);
				noisyEatingSoundID = soundPool.load(context, R.raw.noisy_eating, 1);
			}
		} else {
			release();
		}
		
		boolean vibrate = settings.getBoolean("vibrate", true);
		if ( vibrate) {
			if ( null == vibrator ) {
				vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			}
		} else {
			vibrator = null;
		}
		
	}
	
	public void release() {
		if ( null != soundPool ) {
			soundPool.release();
			soundPool = null;
		}
	}	
	
	protected void play(Integer soundID, Toast message, long[] vibePattern) {

		if ( null != soundPool && null != soundID ) {
		    //Priority 0 so don't interrupt anything higher.
			int streamID = soundPool.play(soundID, 1f, 1f, 0, 0, 1);
	
			//Once playing, don't let low priority sounds interrupt us.
			//if ( streamID > 0 ) {
			//	soundPool.setPriority(streamID, 1);
			//}	    
		}
		
		if ( null != message ) {
			message.show();
		}
		
		if ( null != vibrator && null != vibePattern ) {
			vibrator.vibrate(vibePattern, -1);
		}
		
	}
	
	public void noisyEat() {
		play(noisyEatingSoundID, foodToast, null);
	}
	
	public void politeEat() {
		play(politeEatingSoundID, foodToast, null);
	}
	
	public void play() {
		play(null, playToast, null);
	}
	
	public void tantrum() {
		play(null, tantrumToast, TANTRUM_VIBE);
	}
	
}
