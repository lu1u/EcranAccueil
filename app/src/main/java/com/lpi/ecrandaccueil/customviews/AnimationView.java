package com.lpi.ecrandaccueil.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.ecrandaccueil.R;

import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class AnimationView extends View
{
	public static final float MAX_V_H = 8.0f;
	// Attributs
	int NB;
	float TAILLE;
	float DISTANCE_MIN, DISTANCE_MAX;
	float VITESSE_MIN, VITESSE_MAX;
	private Random _r;

	private static class Flocon
	{
		public float distance;
		public float x, y;
		public float vx, vy;
		public float taille;
		public int type;
	}

	public @NonNull final Drawable[] _images = new Drawable[2];

	private Flocon[] _flocons;
	private long _derniereFrame;

	public AnimationView(Context context)
	{
		super(context);
		init(null, 0);
	}

	private void init(@Nullable AttributeSet attrs, int defStyle)
	{
		_r = new Random(System.currentTimeMillis());

		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AnimationView, defStyle, 0);
		NB = a.getInt(R.styleable.AnimationView_AnimNB, 100);
		TAILLE = a.getDimension(R.styleable.AnimationView_AnimTaille, 150);
		DISTANCE_MIN = a.getFloat(R.styleable.AnimationView_AnimDistanceMin, 1);
		DISTANCE_MAX = a.getFloat(R.styleable.AnimationView_AnimDistanceMax, 10);
		VITESSE_MIN = a.getFloat(R.styleable.AnimationView_AnimVitesseMin, 40);
		VITESSE_MAX = a.getFloat(R.styleable.AnimationView_AnimVitesseMax, 60);

		_images[0] = ListeApplicationsView.loadDrawable(a, R.styleable.AnimationView_AnimFlocon1);
		if (_images[0] != null) _images[0].setTint(Color.argb(64, 255, 255, 255));
		_images[1] = ListeApplicationsView.loadDrawable(a, R.styleable.AnimationView_AnimFlocon2);
		if (_images[1] != null) _images[1].setTint(Color.argb(64, 255, 255, 255));

		a.recycle();
	}

	public AnimationView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public AnimationView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (_flocons == null)
			return;

		final int hauteur = getHeight();
		final int largeur = getWidth();

		long maintenant = System.currentTimeMillis();
		final float deltaT = (float) (maintenant - _derniereFrame) / 1000.0f;

		for (Flocon f : _flocons)
		{
			// Bouge le flocon
			f.x += f.vx * deltaT;
			f.y += f.vy * deltaT;

			// flocon en bas de l'ecran?
			while (f.y > hauteur)
				f.y -= hauteur + f.taille;

			// Flocon qui deborde a droite ou a gauche?
			while (f.x > largeur)
				f.x -= largeur - f.taille;

			while (f.x + f.taille < 0)
				f.x += largeur;

			f.vx += floatRandom(_r, -MAX_V_H, MAX_V_H) * deltaT / f.distance;
			if (f.vx > MAX_V_H)
				f.vx = MAX_V_H;
			if (f.vx < -MAX_V_H)
				f.vx = -MAX_V_H;

			_images[f.type].setBounds((int) f.x, (int) f.y, (int) (f.x + f.taille), (int) (f.y + f.taille));
			_images[f.type].draw(canvas);
		}

		_derniereFrame = maintenant;
		prochaineFrame();
	}

	/***
	 * Provoque un nouvel affichage
	 */
	private void prochaineFrame()
	{
		// Provoque la prochaine frame
		new Thread(() ->
		{
			try
			{
				// Attend un certain temps
				Thread.sleep(40);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			invalidate();
		}).start();
	}

	/***
	 * Evenement: la fenetre change de taille
	 * @param w
	 * @param h
	 * @param oldw
	 * @param oldh
	 */
	@Override protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		creerFlocons(w, h);
	}

	/***
	 * Créer la liste des flocons
	 * @param largeur
	 * @param hauteur
	 */
	void creerFlocons(int largeur, int hauteur)
	{
		_r = new Random(_derniereFrame);
		_flocons = new Flocon[NB];
		for (int i = 0; i < NB; i++)
		{
			_flocons[i] = new Flocon();
			_flocons[i].distance = floatRandom(_r, DISTANCE_MIN, DISTANCE_MAX);
			_flocons[i].x = _r.nextInt(largeur);
			_flocons[i].y = _r.nextInt(hauteur);
			_flocons[i].vx = floatRandom(_r, -MAX_V_H, MAX_V_H) / _flocons[i].distance;
			_flocons[i].vy = floatRandom(_r, VITESSE_MIN, VITESSE_MAX) / _flocons[i].distance;
			_flocons[i].type = _r.nextInt(_images.length);
			_flocons[i].taille = TAILLE / _flocons[i].distance;
		}

		_derniereFrame = System.currentTimeMillis();
	}

	/***
	 * Calcule un nombre flottant aléatoire compris entre deux bornes
	 * @param r
	 * @param min
	 * @param max
	 * @return
	 */
	private float floatRandom(final @NonNull Random r, final float min, final float max)
	{
		return min + (r.nextFloat() * (max - min));
	}
}