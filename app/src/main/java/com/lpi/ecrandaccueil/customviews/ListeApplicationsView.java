package com.lpi.ecrandaccueil.customviews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.lpi.ecrandaccueil.MessageBoxUtil;
import com.lpi.ecrandaccueil.Preferences;
import com.lpi.ecrandaccueil.R;
import com.lpi.ecrandaccueil.applications.ApplicationInstallee;
import com.lpi.ecrandaccueil.applications.ApplicationList;
import com.lpi.ecrandaccueil.sound.SoundManager;

/**
 * TODO: document your custom view class.
 */
public class ListeApplicationsView extends View
{
	private static final String TAG = "TVLauncher:ListeApplication";
	private Drawable _indicateurGauche;
	private Drawable _indicateurDroite;
	float _largeurCase = 200;
	float _hauteurCase = 150;
	float _margeIcone = 10;
	float _margeX = 10;
	float _tailleTexte = 10;
	int _couleurTexte;
	int _delaiAnimation = 100;

	private int _selectionnee = 0;
	//private String _message = "Ecran d'accueil";

	ApplicationList _applications;

	// Tailles, marges, mesures...
	private int _paddingLeft;
	private int _paddingTop;
	private int _paddingRight;
	private int _contentWidth;
	private int _contentHeight;
	private int _nbParRangee = 4;
	private ApplicationInstallee.AttributsGraphiques _attributs;
	ValueAnimator _animator, _animatorLancement;

	private float _decalage = 0;
	private float _fractionAnimation, _fractionAnimationLancement;
	private float _valeurCible;
	private float _valeurDepart;

	public ListeApplicationListener _listener;
	private boolean _animationLancementEnCours = false;

	public interface ListeApplicationListener
	{
		void onOpenMenu();
		void initialisationTerminee();
	}

	public void setListener(ListeApplicationListener listener)
	{
		_listener = listener;
	}

	public ListeApplicationsView(Context context)
	{
		super(context);
		init(null, 0);
		initApplications();
	}

	public ListeApplicationsView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
		initApplications();
	}

	public ListeApplicationsView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
		initApplications();
	}

	public static Drawable loadDrawable(final @NonNull TypedArray a, final int id)
	{
		if (a.hasValue(id))
		{
			Drawable r = a.getDrawable(id);
			r.setCallback(null);
			return r;
		}

		return null;
	}

	private void initApplications()
	{
		new Thread(() ->
		{
			try
			{
				Preferences.getInstance(getContext()); // Pour initialiser le singleton
				ApplicationList.ListeListener listener = () ->
				{
					Log.d(TAG, "onListeChanged " + _applications.getNb());
					if (_selectionnee >= _applications.getNb())
						_selectionnee = _applications.getNb() - 1;
					invalidate();
					if (_listener != null)
						_listener.initialisationTerminee();
				};

				_applications = new ApplicationList(listener);
				if (isInEditMode())
				{
					// Ajouter artificiellement quelques applications bidon est mode Design
					Drawable r = ResourcesCompat.getDrawable( getContext().getResources(), R.drawable.icone, getContext().getTheme());
					if ( r!=null)
					{
						r.setCallback(null);
						r.setTint(Color.WHITE);
						for (int i = 0; i < 10; i++)
							_applications.ajoute(new ApplicationInstallee("Application " + i, "package." + i, r, i));
					}
				}
				else
					_applications.litApplications(getContext(), false);

				calculeTailles();
				invalidate();

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}).start();

	}

	private void init(AttributeSet attrs, int defStyle)
	{
		try
		{

			// Load attributes
			final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ListeApplicationsView, defStyle, 0);
			// Attributs
			final Drawable fondCase = loadDrawable(a, R.styleable.ListeApplicationsView_ListeDrawableFondCase);
			final Drawable fondCaseSelectionnee = loadDrawable(a, R.styleable.ListeApplicationsView_ListeDrawableFondCaseSelectionnee);
			_indicateurGauche = loadDrawable(a, R.styleable.ListeApplicationsView_ListeIndicateurGauche);
			_indicateurDroite = loadDrawable(a, R.styleable.ListeApplicationsView_ListeIndicateurDroite);
			_nbParRangee = a.getInt(R.styleable.ListeApplicationsView_ListeNbParLigne, _nbParRangee);
			_delaiAnimation = a.getInt(R.styleable.ListeApplicationsView_ListeDelaiAnimation, _delaiAnimation);
			float ratioSelection = a.getFloat(R.styleable.ListeApplicationsView_ListeRatioSelection, 1.1f);
			_margeIcone = a.getDimension(R.styleable.ListeApplicationsView_ListeMargeIcone, _margeIcone);
			_margeX = a.getDimension(R.styleable.ListeApplicationsView_ListeMargeX, _margeX);
			_tailleTexte = a.getDimension(R.styleable.ListeApplicationsView_ListeTexteTaille, _tailleTexte);
			_couleurTexte = a.getColor(R.styleable.ListeApplicationsView_ListeTexteCouleur, Color.WHITE);

			_attributs = new ApplicationInstallee.AttributsGraphiques();
			_attributs.couleurTexte = Color.WHITE;
			_attributs.fond = fondCase;
			_attributs.fondSelectionne = fondCaseSelectionnee;
			_attributs.couleurTexte = _couleurTexte;
			_attributs.margeIcone = (int) _margeIcone;
			_attributs.hauteurTexteMax = _margeIcone;
			_attributs.ratioSelection = ratioSelection;

			// Set up a default TextPaint object
			final TextPaint textPaint = new TextPaint();
			textPaint.setColor(_couleurTexte);
			textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
			textPaint.setTextAlign(Paint.Align.LEFT);
			textPaint.setTextSize(_tailleTexte);

			calculeTailles();

			setOnLongClickListener(view ->
			{
				if (_listener != null)
					_listener.onOpenMenu();
				return true;
			});
			a.recycle();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		calculeTailles();
	}

	private void calculeTailles()
	{
		_paddingLeft = getPaddingLeft();
		_paddingTop = getPaddingTop();
		_paddingRight = getPaddingRight();
		final int paddingBottom = getPaddingBottom();

		_contentWidth = getWidth() - _paddingLeft - _paddingRight;
		_contentHeight = getHeight() - _paddingTop - paddingBottom;

		_largeurCase = (_contentWidth / (float) _nbParRangee) - _margeX;
		_hauteurCase = _largeurCase;
	}

	/***
	 * Affiche le contenu de la fenetre
	 * @param canvas
	 */
	@Override protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		int nbApplis = _applications.getNb();
		final float decalageADessiner = _valeurDepart + (_valeurCible - _valeurDepart) * _fractionAnimation;
		float x = _paddingLeft + decalageADessiner;
		float y = _paddingTop + (_contentHeight - _hauteurCase) / 2;
		boolean depasseAGauche = (decalageADessiner < 0);
		boolean depasseADroite = false;
		_attributs.focus = hasFocus();

		for (int i = 0; i < nbApplis; i++)
		{
			if (x >= _contentWidth)
			{
				depasseADroite = true;
				break;
			}

			if (x + _largeurCase > _paddingLeft)
				_applications.affiche(canvas, i, x + _margeIcone, y + _margeIcone, _largeurCase - (_margeIcone * 2), _hauteurCase - (_margeIcone * 2),
						i == _selectionnee, _attributs);
			x += _largeurCase + _margeX;
		}

		if (depasseAGauche)
			// Afficher l'indicateur "depassement a gauche
			drawDrawable(canvas, getLeft(), y, _paddingLeft, _hauteurCase, _indicateurGauche);

		if (depasseADroite)
			// Afficher l'indicateur "depassement a droite
			drawDrawable(canvas, getRight() - _paddingRight, y, _paddingRight, _hauteurCase, _indicateurDroite);

		afficheAnimationLancement(canvas);
	}

	/***
	 * Affiche l'animation de lancement d'une application si elle est en cours
	 * @param canvas
	 */
	private void afficheAnimationLancement(final Canvas canvas)
	{
		if (!_animationLancementEnCours)
			return;

		float centreX = getXApplication(_selectionnee) + _largeurCase / 2.0f;
		float centreY = getHeight() / 2.0f;
		float taille = _hauteurCase + (getHeight() - _hauteurCase) * _fractionAnimationLancement;

		Drawable d = _applications.get(_selectionnee).getIcone();
		if ( d !=null)
		{
			d.setAlpha((int) (255.0f * (1.0f - _fractionAnimationLancement)));
			drawDrawable(canvas, centreX - taille / 2.0f, centreY - taille / 2.0f, taille, taille, _applications.get(_selectionnee).getIcone());
			d.setAlpha(255);
		}
	}

	private void lanceAnimationLancement()
	{
		if (_animationLancementEnCours)
			return;

		_animationLancementEnCours = true;
		_animatorLancement = ValueAnimator.ofFloat(0, 1);
		_animatorLancement.setInterpolator(new AccelerateInterpolator());
		_animatorLancement.setDuration(_delaiAnimation);
		_animatorLancement.addUpdateListener(animation ->
		{
			try
			{
				_fractionAnimationLancement = (float) animation.getAnimatedValue();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			invalidate();
		});

		_animatorLancement.addListener(new Animator.AnimatorListener()
		{
			@Override public void onAnimationStart(final Animator animator)
			{

			}

			@Override public void onAnimationEnd(final Animator animator)
			{
				_animationLancementEnCours = false;
				_animatorLancement = null;
				invalidate();
			}

			@Override public void onAnimationCancel(final Animator animator)
			{

			}

			@Override public void onAnimationRepeat(final Animator animator)
			{

			}
		});
		_animatorLancement.start();
	}

	public static void drawDrawable(@NonNull final Canvas canvas, final float x, final float y, final float l, final float h, @Nullable final Drawable drawable)
	{
		if (drawable == null)
			return;

		drawable.setBounds((int) x, (int) y, (int) (x + l), (int) (y + h));
		drawable.draw(canvas);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		//if (_listener != null)
		//	_listener.onKey(event);
//
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				onRightDPad();
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				onLeftDPad();
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				if (shortPress)
					onCenterDPad();
				break;

			case KeyEvent.KEYCODE_VOLUME_DOWN:
				SoundManager.getInstance(getContext()).diminueVolume();
				invalidate();
				break;

			case KeyEvent.KEYCODE_VOLUME_UP:
				SoundManager.getInstance(getContext()).augmenteVolume();
				invalidate();
				break;

			default:
				if (event.getRepeatCount() == 0)
					shortPress = true;
				return onKeyDown(keyCode, event);
		}

		shortPress = false;
		return true;
	}

	private boolean shortPress = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_CENTER:
			{
				event.startTracking();
				if (event.getRepeatCount() == 0)
					shortPress = true;
				return true;
			}

			case KeyEvent.KEYCODE_VOLUME_DOWN:
				SoundManager.getInstance(getContext()).diminueVolume();
				invalidate();
				break;

			case KeyEvent.KEYCODE_VOLUME_UP:
				SoundManager.getInstance(getContext()).augmenteVolume();
				invalidate();
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			shortPress = false;
			if (_listener != null)
				_listener.onOpenMenu();
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	/***
	 * Calcule la coordonnee X d'une application donnee, en fonction du decalage actuel
	 * @param noAppli
	 * @return
	 */
	private float getXApplication(final int noAppli)
	{
		return _paddingLeft + (_largeurCase + _margeX) * noAppli;
	}

	/***
	 * Retourne l'indice de l'application a la coordonnee X
	 * @param x
	 * @return indice ou -1
	 */
	private int getApplication(final float x)
	{
		return (int) ((x - _paddingLeft) / (_largeurCase + _margeX));
	}

	public void onLeftDPad()
	{
		if (_animator == null)
		{
			if (_selectionnee > 0)
			{
				SoundManager.getInstance(null).playGauche();
				_selectionnee--;
				//Preferences.getInstance(getContext()).setInt(Preferences.PREF_SELECTION, _selectionnee);
				// Glisser a droite?
				float xApplication = getXApplication(_selectionnee) + _decalage;
				if (xApplication < getLeft())
					animeDecalage(_largeurCase + _margeX);
				invalidate();
			}
		}
	}

	public void onRightDPad()
	{
		if (_animator == null)
		{
			int nbApplis = _applications.getNb();
			if (_selectionnee < nbApplis - 1)
			{
				SoundManager.getInstance(null).playDroite();
				_selectionnee++;
				//Preferences.getInstance(getContext()).setInt(Preferences.PREF_SELECTION, _selectionnee);
				// Glisser à gauche?
				float xApplication = getXApplication(_selectionnee) + _decalage;
				if ((xApplication + _largeurCase) > getRight() - _paddingRight)
					animeDecalage(-(_largeurCase + _margeX));
				invalidate();
			}
		}
	}

	/***
	 * Anime le decalage des icones d'un cote ou de l'autre
	 * @param ecart
	 */
	private void animeDecalage(final float ecart)
	{
		if (_animator != null)
		{
			_valeurCible = _decalage + ecart;
			return; // Une animation est deja en cours
		}

		_valeurDepart = _valeurCible;
		_valeurCible = _decalage + ecart;
		_animator = ValueAnimator.ofFloat(0, 1);
		_animator.setInterpolator(new AccelerateDecelerateInterpolator());
		_animator.setDuration(_delaiAnimation);
		_animator.addUpdateListener(animation ->
		{
			try
			{
				_fractionAnimation = (float) animation.getAnimatedValue();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			invalidate();
		});

		_animator.addListener(new Animator.AnimatorListener()
		{
			@Override public void onAnimationStart(final Animator animator)
			{

			}

			@Override public void onAnimationEnd(final Animator animator)
			{
				_decalage = _valeurCible;
				_fractionAnimation = 1.0f;
				_animator = null;
				invalidate();
				Preferences.getInstance(getContext()).setFloat(Preferences.PREF_DECALAGE, _decalage);
			}

			@Override public void onAnimationCancel(final Animator animator)
			{

			}

			@Override public void onAnimationRepeat(final Animator animator)
			{

			}
		});
		_animator.start();
	}

	/***
	 * Touche centrale du DPad, lancer l'application selectionnee
	 */
	public void onCenterDPad()
	{
		if (_applications == null)
			return;

		// Demarrer l'animation
		lanceAnimationLancement();

		SoundManager.getInstance(null).playLance();
		_applications.demarre(_selectionnee, getContext());
	}

	public @Nullable ApplicationInstallee getSelectedApplication()
	{
		if (_applications == null)
			return null;
		if (_selectionnee < 0 || _selectionnee >= _applications.getNb())
			return null;
		return _applications.get(_selectionnee);
	}

	public void cacheApplication()
	{
		ApplicationInstallee app = _applications.get(_selectionnee);
		if (app == null)
			return;

		MessageBoxUtil.messageBox(getContext(), getContext().getString(R.string.messagebox_titre_cacher),
				getContext().getString(R.string.messagebox_cacher, app.getNom()), MessageBoxUtil.BOUTON_OK | MessageBoxUtil.BOUTON_CANCEL, new MessageBoxUtil.Listener()
				{
					@Override public void onOk()
					{
						app.setCachee(true);
						_applications.supprimeApplication(app);
						if (_selectionnee >= _applications.getNb())
							_selectionnee = _applications.getNb() - 1;
						invalidate();
						Toast.makeText(getContext(), getContext().getString(R.string.message_application_cachee, app.getNom()), Toast.LENGTH_SHORT).show();
					}

					@Override public void onCancel()
					{

					}
				}, app.getIcone());

	}

	/***
	 * Envoie l'application selectionnee au debut fin de la liste
	 */
	public void envoieAuDebut()
	{
		ApplicationInstallee app = _applications.get(_selectionnee);
		if (app == null)
			return;

		int maximum = Integer.MIN_VALUE + 1; // Important car on ajoutera 1 a la valeur
		for (ApplicationInstallee a : _applications.getApplications())
			if (a.getNbLancements() > maximum)
				maximum = a.getNbLancements();

		app.setNbLancements(maximum + 1);
		_applications.tri();
		invalidate();
		Toast.makeText(getContext(), getContext().getString(R.string.message_application_debut, app.getNom()), Toast.LENGTH_SHORT).show();
	}

	public void envoieALaFin()
	{
		ApplicationInstallee app = _applications.get(_selectionnee);
		if (app == null)
			return;

		int minimum = Integer.MAX_VALUE - 1;
		for (ApplicationInstallee a : _applications.getApplications())
			if (a.getNbLancements() < minimum)
				minimum = a.getNbLancements();

		app.setNbLancements(minimum - 1);
		_applications.tri();
		invalidate();
		Toast.makeText(getContext(), getContext().getString(R.string.message_application_fin, app.getNom()), Toast.LENGTH_SHORT).show();
	}

	public void reinitApplications()
	{
		_applications.litApplications(getContext(), true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		if (action == MotionEvent.ACTION_DOWN)
		{
			float x = event.getX() - _decalage;
			int indice = getApplication(x);
			if (indice != -1)
			{
				_selectionnee = indice;
				if (_listener != null)
					_listener.onOpenMenu();
				invalidate();
			}
		}
		return true;
	}

	/***
	 * Reponse a une option du menu contextuel
	 * @param itemId
	 */
	public void onMenu(final int itemId)
	{
		if (itemId == R.id.action_debut)
			envoieAuDebut();
		else if (itemId == R.id.action_fin)
			envoieALaFin();
		else if (itemId == R.id.action_cacher)
			cacheApplication();
	}
}

