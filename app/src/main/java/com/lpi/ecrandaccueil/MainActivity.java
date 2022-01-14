package com.lpi.ecrandaccueil;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuCompat;

import com.lpi.ecrandaccueil.applications.ApplicationInstallee;
import com.lpi.ecrandaccueil.customviews.ListeApplicationsView;
import com.lpi.ecrandaccueil.database.MediaDatabase;
import com.lpi.ecrandaccueil.medias.MediaAdapter;
import com.lpi.ecrandaccueil.medias.mediaelements.ElementListeMedias;
import com.lpi.ecrandaccueil.sound.SoundManager;
import com.lpi.ecrandaccueil.utils.BackgroundTaskWithSpinner;
import com.lpi.ecrandaccueil.utils.FileUtils;
import com.lpi.ecrandaccueil.utils.MessageBoxUtil;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener
{
	private static final String TAG = "MainActivity";
	private static final int PERMISSION_REQUEST_CODE = 2;
	public static final float RATIO_NON_FOCUS = 0.01f;
	public static final float RATIO_FOCUS = 0.90f;
	private ListeApplicationsView _listeApplicationsView;
	private ImageButton _bntSettings;
	@Nullable private AnimationDrawable _animationDrawable;
	private ListView _lvMedias;
	private MediaAdapter _adapter;
	private TextView _tvMedias;

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

		Preferences.getInstance(this); // Initialiser le singleton
		SoundManager.getInstance(this);

		if (checkPermissions(true))
			initApplication();
	}

	/***
	 * Initialisation de l'application, une partie en tache de fond
	 */
	private void initApplication()
	{
		// Activer les animations quand un controle change de taille
		//((ViewGroup) findViewById(R.id.layoutMain)).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

		// Recuperer les controles
		_bntSettings = findViewById(R.id.imageButtonSettings);
		_listeApplicationsView = findViewById(R.id.listeApplications);
		_lvMedias = findViewById(R.id.lvFichiers);
		_tvMedias = findViewById(R.id.tvMedias);

		// Cacher les controles le temps de l'initialisation
		_lvMedias.setVisibility(View.GONE);
		_tvMedias.setVisibility(View.GONE);
		_listeApplicationsView.setVisibility(View.GONE);

		// Surveiller le changement de focus
		_lvMedias.setOnFocusChangeListener(this);

		// Menu contextuel
		_bntSettings.setOnClickListener(view -> openContextMenu(_bntSettings));
		_listeApplicationsView.setListener(() -> openContextMenu(_listeApplicationsView));

		_lvMedias.setOnItemClickListener((adapterView, view, position, id) ->
		{
			ElementListeMedias f = _adapter.getItem(position);
			if (f != null)
			{
				ouvreElement(f);
			}
		});

		Preferences preferences = Preferences.getInstance(this);
		boolean animations = preferences.getBoolean(Preferences.PREF_ANIMATIONS, true);
		findViewById(R.id.animationView2).setVisibility(animations ? View.VISIBLE : View.GONE);

		BackgroundTaskWithSpinner.execute(this, R.layout.background_working, new BackgroundTaskWithSpinner.TaskListener()
		{
			@Override public void execute()
			{

				_listeApplicationsView.initApplications();
				_adapter = MediaAdapter.createAdapter(MainActivity.this, null);
			}

			@Override public void onFinished()
			{
				_lvMedias.setAdapter(_adapter);
				_tvMedias.setText(getString(R.string.medias, _adapter.getTitre(MainActivity.this)));
				ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) _lvMedias.getLayoutParams();
				lp.height = (int) (MainActivity.this.getWindow().getDecorView().getHeight() * RATIO_NON_FOCUS);
				_lvMedias.setLayoutParams(lp);

				// Montrer les controles maintenant que l'application est initialisee
				_lvMedias.setVisibility(View.VISIBLE);
				_tvMedias.setVisibility(View.VISIBLE);
				_listeApplicationsView.setVisibility(View.VISIBLE);

				_listeApplicationsView.requestFocus();

				registerForContextMenu(_listeApplicationsView);
				registerForContextMenu(_bntSettings);
			}
		});

	}

	/***
	 * Ouvre un element dans la liste des medias
	 * @param f
	 */
	private void ouvreElement(final @NonNull ElementListeMedias f)
	{
		if (f.isDirectory())
		{
			_adapter = MediaAdapter.createAdapter(MainActivity.this, f);
			_lvMedias.setAdapter(_adapter);
			_tvMedias.setText(getString(R.string.medias, _adapter.getTitre(MainActivity.this)));
		}
		else
		{
			MediaDatabase.getInstance(this).ajoute(f.getPath(), 0, 0);
			_adapter.notifyDataSetChanged();
			FileUtils.ouvreFichier(this, f.getFile());
		}
	}

	static final @NonNull String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};

	/***
	 * Reception du resultat de la demande de permissions
	 * @param requestCode
	 * @param resultCode
	 * @param intent
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d(TAG, "onActivityResult, requestcode: " + requestCode + ",resultcode: " + resultCode);
		if (intent != null)
		{
			String action = intent.getAction();
			if ("org.videolan.vlc.player.result".equals(action))
			{
				Uri data = intent.getData();
				if (data != null)
				{
					String path = data.getPath();
					Log.d(TAG, "Path: " + path);
				}
				Log.d(TAG, "action: " + action);
				Log.d(TAG, "Extra position: " + intent.getExtras().getInt("extra_position"));
				Log.d(TAG, "Extra duration: " + intent.getExtras().getInt("extra_duration"));
			}
		}
	}

	/***
	 * Verifie que les permissions necessaires sont bien allouées, les demande si besoin
	 * @param request
	 * @return
	 */
	private boolean checkPermissions(boolean request)
	{
		boolean toutAccorde = true;
		for (String permission : PERMISSIONS)
		{
			if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
			{
				toutAccorde = false;
				break;
			}
		}

		if (!toutAccorde)
		{
			if (request)
				ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
			return false;
		}

		return true;
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (checkPermissions(false))
		{
			initApplication();
		}
		else
		{
			// Les permissions n'ont pas ete accordées
			MessageBoxUtil.messageBox(MainActivity.this, "L'application ne peut pas fonctionner correctement si vous ne lui accordez pas les permissions nécéssaires.\nVoulez-vous accorder les permission?",
					"Permissions non accordées", MessageBoxUtil.BOUTON_OK | MessageBoxUtil.BOUTON_CANCEL, new MessageBoxUtil.Listener()
					{
						@Override public void onOk()
						{
							checkPermissions(true);
						}

						@Override public void onCancel()
						{
							finish();
							moveTaskToBack(true);
						}
					}, null);
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

			// Relancer l'animation
			View layoutView = this.findViewById(R.id.layoutMain);
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
//			if (Build.VERSION.SDK_INT < 19)
//			{
//				a.getWindow().setFlags(
//						WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,
//						WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
//				                      );
//			}
//			else
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
			//case R.id.menu_animations:
			//	montreOuCacheAnimations();
			//	break;
			case R.id.action_proprietes:
				proprietesApplication();
				break;

			case R.id.menu_affichage:
				proprietesAffichage();
				break;

			default:
				_listeApplicationsView.onMenu(item.getItemId());
				_listeApplicationsView.requestFocus();
		}
		return true;
	}

	private void proprietesAffichage()
	{
		DialogPreferencesAffichage.start(this, new DialogPreferencesAffichage.DialogPreferenceAffichageListener()
		{
			@Override public void onAnimationChanged()
			{
				Preferences preferences = Preferences.getInstance(MainActivity.this);
				boolean animations = preferences.getBoolean(Preferences.PREF_ANIMATIONS, true);
				if (animations)
					findViewById(R.id.animationView2).setVisibility(View.VISIBLE);
				else
					findViewById(R.id.animationView2).setVisibility(View.GONE);
			}

			@Override public void onNbParRangeeChanged()
			{
				_listeApplicationsView.reinitAttributs();
			}

			@Override public void onDialogClosed()
			{
				_listeApplicationsView.invalidate();
			}
		});
	}

	private void proprietesApplication()
	{
		ApplicationInstallee app = _listeApplicationsView.getSelectedApplication();
		if (app == null)
			return;

		ProprietesApplication.start(this, app);
	}

	/***
	 * Option de menu: Parametres systeme
	 */
	private void onMenuParametres()
	{
		startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
	}

	/***
	 * Option de menu: A propos
	 */
	private void onMenuAPropos()
	{
		DialogAPropos.start(this);
	}

	/***
	 * Option de menu: son
	 */
	private void onMenuSon()
	{
		SoundManager sm = SoundManager.getInstance(this);
		sm.inverseVolume();
	}

	/***
	 * Changement de focus, ajuster la taille des fenetres
	 * @param view
	 * @param b
	 */
	@Override public void onFocusChange(final View view, final boolean b)
	{
		ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) _lvMedias.getLayoutParams();
		if (_lvMedias.hasFocus())
		{
			lp.height = (int) (this.getWindow().getDecorView().getHeight() * RATIO_FOCUS);
		}
		else
		{
			lp.height = (int) (this.getWindow().getDecorView().getHeight() * RATIO_NON_FOCUS);
		}
		_lvMedias.setLayoutParams(lp);
		_lvMedias.forceLayout();
		_lvMedias.setAdapter(_adapter);
		_lvMedias.invalidate();
	}
}