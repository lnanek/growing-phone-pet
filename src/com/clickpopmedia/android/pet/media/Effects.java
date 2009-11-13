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
	
	protected SoundPool mPool;
	
	protected int mPoliteEatingSound;
	protected int mNoisyEatingSound;
	protected int mMusicBasicSound;
	protected int mMusicNormalSound;
	protected int mMusicSkilledSound;

	protected Toast mLastToast;
	
	protected Context mContext;
	
	protected Vibrator mVibrator;
	
	public Effects() {
	}	
	
	protected void showToast(int messageID) {
		//Prevent large stacks of toast during frequent actions.
		if ( null != mLastToast ) {
			mLastToast.cancel();
		}
		
		mLastToast = Toast.makeText(mContext, messageID, Toast.LENGTH_SHORT);
		//toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		mLastToast.show();
	}
	
	/**
	 * Process updated context and settings. 
	 * Preserve previous SoundPool if possible.
	 * 
	 */
	public void onActivityUpdated(Context context, SharedPreferences settings) {
		
		this.mContext = context;

		boolean playSound = settings.getBoolean("playSound", true);
		if ( playSound) {
			if ( null == mPool ) {
				mPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
				mPoliteEatingSound = mPool.load(context, R.raw.quick_crunch, 1);
				mNoisyEatingSound = mPool.load(context, R.raw.noisy_eating, 1);
				mMusicBasicSound = mPool.load(context, R.raw.music_basic, 1);
				mMusicNormalSound = mPool.load(context, R.raw.music_normal, 1);
				mMusicSkilledSound = mPool.load(context, R.raw.music_skilled, 1);
			}
		} else {
			release();
		}
		
		boolean vibrate = settings.getBoolean("vibrate", true);
		if ( vibrate) {
			if ( null == mVibrator ) {
				mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			}
		} else {
			mVibrator = null;
		}
		
	}
	
	public void release() {
		if ( null != mPool ) {
			mPool.release();
			mPool = null;
		}
	}	
	
	protected void play(Integer soundID, Integer messageID, long[] vibePattern) {

		if ( null != mPool && null != soundID ) {
		    //Priority 0 so don't interrupt anything higher.
			int streamID = mPool.play(soundID, 1f, 1f, 0, 0, 1);
	
			//Once playing, don't let low priority sounds interrupt us.
			//if ( streamID > 0 ) {
			//	soundPool.setPriority(streamID, 1);
			//}	    
		}
		
		if ( null != messageID ) {
			showToast(messageID);
		}
		
		if ( null != mVibrator && null != vibePattern ) {
			mVibrator.vibrate(vibePattern, -1);
		}
		
	}
	
	public void noisyEat() {
		play(mNoisyEatingSound, R.string.food_message_noisy, null);
	}
	
	public void politeEat() {
		play(mPoliteEatingSound, R.string.food_message_polite, null);
	}
	
	public void play() {
		play(null, R.string.play_message, null);
	}
	
	public void tantrum() {
		play(null, R.string.tantrum_message, TANTRUM_VIBE);
	}
	
	public void msuicBasic() {
		play(mMusicBasicSound, R.string.play_music_basic_message, null);
	}
	
	public void msuicNormal() {
		play(mMusicNormalSound, R.string.play_music_normal_message, null);
	}
	
	public void msuicSkilled() {
		play(mMusicSkilledSound, R.string.play_music_skilled_message, null);
	}
	
}
