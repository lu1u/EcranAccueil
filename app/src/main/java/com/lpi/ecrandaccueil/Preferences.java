package com.lpi.ecrandaccueil;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

public class Preferences
{
	public static final String PREF_VOLUME = "Volume";
	public static final String PREF_ANIMATIONS = "Animations";
	private static final String PREFERENCES = Preferences.class.getName();
	public static final String PREF_DECALAGE = "Decalage";
	public static final String PREF_NB_LANCEMENTS = "NbLancements ";
	public static final String PREF_CACHEE = "Cachee ";
	public static final String SEPARATEUR = " ";

	private static Preferences _instance;
	@NonNull final SharedPreferences settings;
	@NonNull final SharedPreferences.Editor editor;

	/***
	 * Obtenir l'instance (unique) de Preferences
	 * @param context
	 * @return
	 */
	public static synchronized Preferences getInstance(@Nullable final Context context)
	{
		if (_instance == null)
			if ( context!=null)
			_instance = new Preferences(context);

		return _instance;
	}

	private Preferences(final @NonNull Context context)
	{
		settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		editor = settings.edit();

	}

	public int getInt(@NonNull final String nom, int defaut)
	{
		return settings.getInt(nom, defaut);
	}
	public int getInt(@NonNull final String nom, @NonNull final String packageName, int defaut)
	{
		return getInt( nom + SEPARATEUR + packageName, defaut);
	}

	public void setBoolean(@NonNull final String nom, boolean val)
	{
		editor.putBoolean(nom, val);
		editor.apply();
	}

	public void setBoolean(@NonNull final String nom, @NonNull final String packageName, boolean val)
	{
		setBoolean( nom + SEPARATEUR + packageName, val);
	}

	public boolean getBoolean(@NonNull final String nom, boolean defaut)
	{
		return settings.getBoolean(nom, defaut);
	}
	public boolean getBoolean(@NonNull final String nom, @NonNull final String packageName, boolean defaut)
	{
		return getBoolean( nom + SEPARATEUR + packageName, defaut);
	}

	public void setInt(@NonNull final String nom, int val)
	{
		editor.putInt(nom, val);
		editor.apply();
	}

	public void setInt(@NonNull final String nom, @NonNull final String packageName, int val)
	{
		setInt( nom + SEPARATEUR + packageName, val);
	}
	public float getFloat(@NonNull final String nom, float defaut)
	{
		return settings.getFloat(nom, defaut);
	}
	public float getFloat(@NonNull final String nom, @NonNull final String packageName, float defaut)
	{
		return getFloat(nom + SEPARATEUR + packageName, defaut);
	}

	public void setFloat(@NonNull final String nom, float val)
	{
		editor.putFloat(nom, val);
		editor.apply();
	}

	public void setFloat(@NonNull final String nom, @NonNull final String packageName, float val)
	{
		setFloat( nom + SEPARATEUR + packageName, val);
	}

	public void setChar(@NonNull final String nom, @NonNull final String packageName, final char val)
	{
		setChar(nom+ SEPARATEUR + packageName, val);
	}

	public void setChar(@NonNull final String nom, char val)
	{
		editor.putString(nom, "" + val);
		editor.apply();
	}

	public char getChar(@NonNull final String nom, char defaut)
	{
		return settings.getString( nom, defaut + " ").charAt(0);
	}
	public char getChar(@NonNull final String nom, @NonNull final String packageName, char defaut)
	{
		return getChar(nom + SEPARATEUR + packageName, defaut);
	}
	public String getString(@NonNull final String nom, final String defaut)
	{
		return settings.getString( nom, defaut );
	}
	public String getString(@NonNull final String nom, @NonNull final String packageName, String defaut)
	{
		return getString(nom + SEPARATEUR + packageName, defaut);
	}

	public void setString(@NonNull final String nom, @NonNull final String packageName, final String val)
	{
		setString(nom+ SEPARATEUR + packageName, val);
	}

	public void setString(@NonNull final String nom, String val)
	{
		editor.putString(nom, val);
		editor.apply();
	}

}
