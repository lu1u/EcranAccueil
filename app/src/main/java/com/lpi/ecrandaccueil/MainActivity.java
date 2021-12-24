package com.lpi.ecrandaccueil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import com.lpi.ecrandaccueil.applications.ApplicationInstallee;
import com.lpi.ecrandaccueil.customviews.ListeApplicationsView;
import com.lpi.ecrandaccueil.sound.SoundManager;

public class MainActivity extends AppCompatActivity
{
	private static final String TAG = "MainActivity";
	ListeApplicationsView _listeApplicationsView;
	ImageButton _bntSettings;
	private AnimationDrawable _animationDrawable;


	/***
	 * Creation de la vue
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setFullScreen(this);

		Preferences preferences = Preferences.getInstance(this); // Initialiser le singleton

		final ProgressBar _progressBar = findViewById(R.id.progressBar);
		_progressBar.setVisibility(View.VISIBLE);

		_bntSettings = findViewById(R.id.imageButtonSettings);
		_listeApplicationsView = findViewById(R.id.listeApplications);
		//_bntSettings.setVisibility(View.GONE);
		//_listeApplicationsView.setVisibility(View.GONE);
		registerForContextMenu(_listeApplicationsView);
		registerForContextMenu(_bntSettings);

		_bntSettings.setOnClickListener(view ->
				openContextMenu(_bntSettings));

		_listeApplicationsView.setListener(new ListeApplicationsView.ListeApplicationListener()
		{
			@Override public void onOpenMenu()
			{
				openContextMenu(_listeApplicationsView);
			}

			@Override public void initialisationTerminee()
			{
				_progressBar.setVisibility(View.GONE);
				_bntSettings.setVisibility(View.VISIBLE);
				_listeApplicationsView.setVisibility(View.VISIBLE);
				_listeApplicationsView.requestFocus();
			}
		});

		SoundManager.getInstance(this);
		boolean animations = preferences.getBoolean(Preferences.PREF_ANIMATIONS, true);
		if ( animations)
		{
			findViewById(R.id.animationView2).setVisibility(View.VISIBLE);
		}
		else
		{
			findViewById(R.id.animationView2).setVisibility(View.GONE);

		}
	}

	/***
	 * Evenement: l'activite passe en arriere plan
	 */
	@Override protected void onPause()
	{
		Log.d(TAG, "OnPause");
		try
		{
			super.onPause();
			if (_animationDrawable != null)
			{
				_animationDrawable.stop();
				_animationDrawable = null;
			}
		} catch (Exception e)
		{
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	/***
	 * Evenement: l'activite passe en avant plan
	 */
	@Override
	protected void onResume()
	{
		Log.d(TAG, "OnResume");
		try
		{

			super.onResume();
			//setFullScreen(this);

			// Relancer les animations
			View layoutView = this.findViewById(R.id.layout);
			if (layoutView != null)
			{
				_animationDrawable = (AnimationDrawable) layoutView.getBackground();
				if (_animationDrawable != null)
				{
					_animationDrawable.setEnterFadeDuration(10);
					_animationDrawable.setExitFadeDuration(5000);
					_animationDrawable.start();
				}
			}
		} catch (Exception e)
		{
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	public static void setFullScreen(@NonNull final Activity a)
	{
		try
		{
			if (Build.VERSION.SDK_INT < 19)
			{
				a.getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,
						WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
				                      );
			}
			else
			{
				View decorView = a.getWindow().getDecorView();

				int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
						| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
						| View.SYSTEM_UI_FLAG_IMMERSIVE;

				decorView.setSystemUiVisibility(uiOptions);

				a.getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,
						WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
				                      );
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onCreateContextMenu(final @NonNull ContextMenu menu, final @NonNull View v, final @NonNull ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v == _listeApplicationsView)
		{
			ApplicationInstallee a = _listeApplicationsView.getSelectedApplication();
			if (a != null)
			{
				getMenuInflater().inflate(R.menu.applications_menu, menu);
				MenuCompat.setGroupDividerEnabled(menu, true);
				menu.setHeaderTitle(a.getNom());
				menu.setHeaderIcon(a.getIcone());
			}
		}
		else
		{
			if (v == _bntSettings)
			{
				getMenuInflater().inflate(R.menu.menu_settings, menu);
				MenuCompat.setGroupDividerEnabled(menu, true);
				SoundManager sm = SoundManager.getInstance(this);
				MenuItem item = menu.findItem(R.id.menu_son);
				item.setChecked(sm.getVolume() > 0);
				item = menu.findItem(R.id.menu_animations);
				item.setChecked(Preferences.getInstance(this).getBoolean(Preferences.PREF_ANIMATIONS, true));
			}
		}
	}

	@Override public void onContextMenuClosed(@NonNull final Menu menu)
	{
		super.onContextMenuClosed(menu);
		_listeApplicationsView.requestFocus();
	}

	@Override public boolean onContextItemSelected(@NonNull final MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_son:
				onMenuSon();
				break;
			case R.id.menu_a_propos:
				onMenuAPropos();
				break;

			case R.id.menu_parametres:
				onMenuParametres();
				break;

			case R.id.menu_relire:
				_listeApplicationsView.reinitApplications();
				break;
			case R.id.menu_animations:
				montreOuCacheAnimations();
				break;

			default:
				_listeApplicationsView.onMenu(item.getItemId());
				_listeApplicationsView.requestFocus();
		}
		return true;
	}

	/***
	 * Montre ou cache les animations
	 */
	private void montreOuCacheAnimations()
	{
		Preferences preferences = Preferences.getInstance(this);
		boolean animations = preferences.getBoolean(Preferences.PREF_ANIMATIONS, true);
		animations = ! animations;
		if ( animations)
		{
			findViewById(R.id.animationView2).setVisibility(View.VISIBLE);
		}
		else
		{
			findViewById(R.id.animationView2).setVisibility(View.GONE);

		}
		preferences.setBoolean(Preferences.PREF_ANIMATIONS, animations);
	}

	private void onMenuParametres()
	{
		startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
	}

	private void onMenuAPropos()
	{
		DialogAPropos.start(this);
	}

	private void onMenuSon()
	{
		SoundManager sm = SoundManager.getInstance(this);
		sm.inverseSon();
	}

}