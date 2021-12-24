package com.lpi.ecrandaccueil.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextPaint;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.ecrandaccueil.Preferences;

public class ApplicationInstallee
{
	private final String _packageName;
	private @Nullable Drawable _icone;
	private String _nom;
	private TextPaint _tPaint;
	private int _hauteurTexte;
	private int _nbLancements;

	public int getNbLancements()
	{
		return _nbLancements;
	}

	public void setNbLancements(int nbLancements)
	{
		_nbLancements = nbLancements;
		Preferences.getInstance(null).setInt(Preferences.PREF_NB_LANCEMENTS, _packageName, _nbLancements);
	}



	public @Nullable Drawable getIcone()
	{
		return _icone;
	}

	public static class AttributsGraphiques
	{
		public Drawable fond;
		public Drawable fondSelectionne;
		public int couleurTexte;
		public int margeIcone;
		public float hauteurTexteMax;
		public float ratioSelection;
		public boolean focus;
	}

	// Pour simulation en mode edit uniquement
	public ApplicationInstallee(@NonNull final String nom, @NonNull final String packagename, @NonNull final Drawable icone, int raccourci)
	{
		_packageName = packagename;
		_nom = nom;
		_nbLancements = 0;
		_icone = icone;
	}

	public ApplicationInstallee(@NonNull final PackageManager packageManager, @NonNull final ResolveInfo resolveInfo)
	{
		_packageName = resolveInfo.activityInfo.packageName;
		loadIcone(packageManager, resolveInfo);
		try
		{
			_nom = resolveInfo.loadLabel(packageManager).toString();
		} catch (Exception e)
		{
			_nom = _packageName;
		}

		Preferences prefs = Preferences.getInstance(null);
		_nbLancements = prefs.getInt(Preferences.PREF_NB_LANCEMENTS, _packageName, 0);
	}

	private void loadIcone(@NonNull final PackageManager packageManager, @NonNull final ResolveInfo resolveInfo)
	{
		new Thread(() ->
		{
			try
			{
				_icone = resolveInfo.loadIcon(packageManager);
			} catch (Exception e)
			{
				_icone = null;
			}
		}).start();
	}

	public String getNom()
	{
		return _nom;
	}

	public String getPackageName()
	{
		return _packageName;
	}

	/***
	 * Affiche l'application selon son etat
	 * @param canvas
	 * @param x
	 * @param y
	 * @param l
	 * @param h
	 * @param selectionnee
	 * @param attributs
	 */
	public void affiche(@NonNull final Canvas canvas, float x, float y, float l, float h, final boolean selectionnee, @NonNull final AttributsGraphiques attributs)
	{
		if (_tPaint == null)
		{
			calculeTPaint(l, attributs.hauteurTexteMax);
			_tPaint.setAntiAlias(true);
			_tPaint.setColor(attributs.couleurTexte);
			_tPaint.setShadowLayer(2,2,2, Color.BLACK);
		}

		float centreX = x + (l*0.5f);
		float centreY = y + (h*0.5f);
		if ( selectionnee )
		{
			l *= attributs.ratioSelection;
			h *= attributs.ratioSelection;
		}

		x = centreX - (l*0.5f);
		y = centreY - (h*0.5f);

		drawDrawable(canvas, x, y, l, h - _hauteurTexte, selectionnee ? attributs.fondSelectionne : attributs.fond, attributs.focus);
		if (_icone != null)
		{
			drawDrawable(canvas,
					(int) x + attributs.margeIcone, (int) y + attributs.margeIcone, (int) (l - attributs.margeIcone * 2), (int) (h - attributs.margeIcone * 2 - _hauteurTexte),
					_icone, attributs.focus);
		}

		afficheTexteCentre(canvas, _nom, x, y + attributs.hauteurTexteMax, l, h);
	}

	/***
	 * Affiche un Drawable aux coordonnees et a la taille donnee
	 * @param canvas
	 * @param x
	 * @param y
	 * @param l
	 * @param h
	 * @param drawable
	 */
	private void drawDrawable(@NonNull final Canvas canvas, final float x, final float y, final float l, final float h, @Nullable final Drawable drawable, boolean focus)
	{
		if (drawable == null)
			return;

		if ( focus)
			drawable.setColorFilter(null);
		else
			drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SCREEN);

		drawable.setBounds((int) x, (int) y, (int) (x + l), (int) (y + h));
		drawable.setCallback(null);
		drawable.draw(canvas);
	}
	public void setCachee(boolean cachee)
	{
		Preferences.getInstance(null).setInt(Preferences.PREF_CACHEE, _packageName, cachee ? 1 : 0);
	}

	private void afficheTexteCentre(final @NonNull Canvas canvas, final @NonNull String nom, final float x, final float y, final float l, final float h)
	{
		Rect rText = new Rect();
		_tPaint.getTextBounds(nom, 0, nom.length(), rText);
		canvas.drawText(nom, x + (l - rText.width()) / 2, y + h, _tPaint);
	}

	private void calculeTPaint(final float largeur, float hauteur)
	{
		_tPaint = new TextPaint();
		float texteMax = largeur;
		float texteMin = 1;
		float tailleTexte = texteMin + (texteMax - texteMin) / 2.0f;

		Rect rText = new Rect();

		// Recherche dichotomique de la taille
		while (texteMax > (texteMin + 1))
		{
			_tPaint.setTextSize(tailleTexte);
			_tPaint.getTextBounds(_nom, 0, _nom.length(), rText);

			if (rText.width() > largeur || rText.height() > hauteur)
				// Trop grand
				texteMax = tailleTexte;
			else
				// Trop petit
				texteMin = tailleTexte;

			tailleTexte = texteMin + (texteMax - texteMin) / 2.0f;
		}

		_hauteurTexte = rText.height();
	}

	/***
	 * Lance l'application
	 * @param context
	 */
	public void demarre(@NonNull final Context context)
	{
		// Incremente le nombre de lancements
		setNbLancements(_nbLancements + 1);
		try
		{
			// Lance l'application
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(_packageName);
			if (intent == null)
			{
				// Bring user to the market or let them choose an app?
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=" + _packageName));
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
