package com.lpi.ecrandaccueil.sound;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.annotation.Nullable;

import com.lpi.ecrandaccueil.Preferences;
import com.lpi.ecrandaccueil.R;

/***
 * Pour simplifier l'usage de sons dans l'application
 */
public class SoundManager
{
	public static final int NIVEAUX_VOLUME = 4 ; // 0..3
	public static final int NIVEAUX_MAX= NIVEAUX_VOLUME-1 ;
	private static @Nullable SoundManager _instance;
	private int _volume ;
	//private Context _context;
	private MediaPlayer _mpDroite;
	private MediaPlayer _mpGauche;
	private MediaPlayer _mpLance;

	/***
	 * Obtenir l'instance (unique) de Preferences
	 * @param context
	 * @return
	 */
	public static synchronized SoundManager getInstance(@Nullable final Context context)
	{
		if (_instance == null)
			_instance = new SoundManager(context);

		return _instance;
	}

	private SoundManager(@Nullable final Context context)
	{
		_volume = Preferences.getInstance(context).getInt(Preferences.PREF_VOLUME, 3);
		new Thread(() ->
		{
			try
			{
				float v = (float)_volume / (float)(NIVEAUX_MAX);
				_mpDroite = MediaPlayer.create(context, R.raw.right);
				_mpDroite.setVolume(v, v);
				_mpGauche = MediaPlayer.create(context, R.raw.left);
				_mpGauche.setVolume(v, v);
				_mpLance = MediaPlayer.create(context, R.raw.ui_loading);
				_mpLance.setVolume(v, v);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}).start();

	}

	public void augmenteVolume()
	{
		if (_volume<3)
		{
			_volume++;
			Preferences.getInstance(null).setInt(Preferences.PREF_VOLUME, _volume);
			float v = (float)_volume / (float)(NIVEAUX_VOLUME-1);
			_mpDroite.setVolume(v, v);
			_mpGauche.setVolume(v, v);
			_mpLance.setVolume(v, v);

			playGauche();
		}
	}


	public void diminueVolume()
	{
		if (_volume>0)
		{
			_volume--;
			Preferences.getInstance(null).setInt(Preferences.PREF_VOLUME, _volume);
			float v = (float)_volume / (float)(NIVEAUX_VOLUME-1);
			_mpDroite.setVolume(v, v);
			_mpGauche.setVolume(v, v);
			_mpLance.setVolume(v, v);

			playDroite();
		}
	}

	public int getVolume()
	{
		return _volume;
	}

	public void playDroite()
	{
		if ( _volume>0)
		if (_mpDroite != null)
		{
			_mpDroite.seekTo(0);
			_mpDroite.start();
		}
	}

	public void playGauche()
	{
		if ( _volume>0)
		if (_mpGauche != null)
		{
			_mpGauche.seekTo(0);
			_mpGauche.start();
		}
	}

	public void playLance()
	{
		if ( _volume>0)
			if (_mpLance!=null)
			{
				_mpLance.seekTo(0);
				_mpLance.start();
			}
	}

	public void inverseSon()
	{
		if ( _volume > 0)
			_volume = 0;
		else
			_volume = NIVEAUX_MAX;
		Preferences.getInstance(null).setInt(Preferences.PREF_VOLUME, _volume);
	}
}
