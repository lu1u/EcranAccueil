package com.lpi.ecrandaccueil.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.ecrandaccueil.R;

/**
 * Controle d'edition d'un nombre entier a l'aide du DPad: gauche et droite
 */
public class EditNumberDPad extends View
{
	public interface EditNumberDPadListener
	{
		/***
		 * Appelé a chaque fois que la valeur est changee
		 * @param valeur
		 */
		void onValueChanged(int valeur);

	}

	private int _valeur, _min, _max;
	private @Nullable Drawable _droite, _gauche;
	private boolean _bDroitePresse = false;
	private boolean _bGauchePresse = false;
	private int _couleur, _couleurSelectionnee;
	private TextPaint _textPaint;
	private @Nullable EditNumberDPadListener _listener;

	public EditNumberDPad(Context context)
	{
		super(context);
		init(null, 0);
	}

	private void init(AttributeSet attrs, int defStyle)
	{
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EditNumberDPad, defStyle, 0);
		_min = a.getInt(R.styleable.EditNumberDPad_ENDPMin, 0) ;
		_max = a.getInt(R.styleable.EditNumberDPad_ENDPMax, 0) ;
		_droite = loadDrawable(a, R.styleable.EditNumberDPad_ENDPDroite);
		_gauche = loadDrawable(a, R.styleable.EditNumberDPad_ENDPGauche);
		_couleur = a.getColor(R.styleable.EditNumberDPad_ENDPCouleur, Color.BLACK);
		_couleurSelectionnee = a.getColor(R.styleable.EditNumberDPad_ENDPCouleurSelectionnee, Color.RED);
		final float tSize = a.getDimension(R.styleable.EditNumberDPad_ENDPtailleTexte, 22);

		if  (_max <=_min)
			_max = _min +1;

		_valeur = a.getInt(R.styleable.EditNumberDPad_ENDPValeur, _min);
		if (_valeur< _min)
			_valeur = _min;
		if  (_valeur > _max)
			_valeur = _max;

		a.recycle();

		// Set up a default TextPaint object
		_textPaint = new TextPaint();
		_textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		_textPaint.setTextAlign(Paint.Align.LEFT);
		_textPaint.setTextSize(tSize);
	}

	/***
	 * Charge un Drawable
	 * @param a
	 * @param id
	 * @return
	 */
	public static @Nullable Drawable loadDrawable(final @NonNull TypedArray a, final int id)
	{
		if (a.hasValue(id))
		{
			Drawable r = a.getDrawable(id);
			r.setCallback(null);
			return r;
		}
		return null;
	}

	public EditNumberDPad(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public EditNumberDPad(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	/***
	 * Affichage
	 * @param canvas
	 */
	@Override protected void onDraw(@NonNull Canvas canvas)
	{
		super.onDraw(canvas);

		int paddingTop = getPaddingTop();
		int paddingRight = getPaddingRight();
		int paddingBottom = getPaddingBottom();

		int contentHeight = getHeight() - paddingTop - paddingBottom;

		afficheTexteDroite(canvas, "" + _valeur, getWidth() - paddingRight - contentHeight * 2, paddingTop + contentHeight / 2.0f, _textPaint);

		drawDrawable(canvas, getWidth() - paddingRight - contentHeight, paddingTop, contentHeight, contentHeight, _droite, _bDroitePresse);
		drawDrawable(canvas, getWidth() - paddingRight - contentHeight * 2.0f, paddingTop, contentHeight, contentHeight, _gauche, _bGauchePresse);
	}

	/***
	 * Dessine un Drawable, avec un style de couleur dependant de l'etat
	 * @param canvas
	 * @param x
	 * @param y
	 * @param l
	 * @param h
	 * @param drawable
	 * @param selectionne
	 */
	public void drawDrawable(@NonNull final Canvas canvas, final float x, final float y, final float l, final float h, @Nullable final Drawable drawable, boolean selectionne)
	{
		if (drawable == null)
			return;

		if ( hasFocus())
			drawable.setTint( selectionne ? _couleurSelectionnee : _couleur);
		else
			drawable.setTint(Color.GRAY);

		drawable.setBounds((int) x, (int) y, (int) (x + l), (int) (y + h));
		drawable.draw(canvas);
	}

	/***
	 * Affiche un texte aligné a droite
	 * @param canvas
	 * @param nom
	 * @param x
	 * @param y
	 * @param paint
	 */
	private void afficheTexteDroite(final @NonNull Canvas canvas, final @NonNull String nom, final float x, final float y, @NonNull TextPaint paint)
	{
		Rect rText = new Rect();
		paint.getTextBounds(nom, 0, nom.length(), rText);
		canvas.drawText(nom, x - rText.width(), y + rText.height() / 2.0f, paint);
	}

	@Override public boolean onKeyDown(final int keyCode, final KeyEvent event)
	{
		switch( keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_LEFT:
				_bGauchePresse = true;
				if (_valeur>_min)
				{
					_valeur--;
					if (_listener!=null)
						_listener.onValueChanged(_valeur);
				}
				invalidate();
				break;

			case KeyEvent.KEYCODE_DPAD_RIGHT:
				_bDroitePresse = true;
				if (_valeur<_max)
				{
					_valeur++;
					if (_listener!=null)
						_listener.onValueChanged(_valeur);
				}
				invalidate();
				break;
			default:
				return super.onKeyDown(keyCode, event);
		}
		return true;
	}


	@Override public boolean onKeyUp(final int keyCode, final KeyEvent event)
	{
		switch( keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_LEFT:
				_bGauchePresse = false;
				invalidate();
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				_bDroitePresse = false;
				invalidate();
				break;
			default:
				return super.onKeyUp(keyCode, event);
		}
		return true;
	}


	public void setValeur(int val)
	{
		if ( val <= _max && val > _min)
		{
			_valeur = val;
			invalidate();
		}
	}


	public void setMin(int min)
	{
		_min = min;
		if ( _valeur < _min )
		_valeur = _min;

		if ( _max <= _min)
		_max = _min +1;
			invalidate();
	}


	public void setMax(int max)
	{
		_max = max;
		if ( _valeur > _max )
			_valeur = _max;

		if ( _min >= _max)
			_min = _max - 1;
		invalidate();
	}

	public void setListener(@Nullable EditNumberDPadListener listener)
	{
		_listener = listener;
	}
}