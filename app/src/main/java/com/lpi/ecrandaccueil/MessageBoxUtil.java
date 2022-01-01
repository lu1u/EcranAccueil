package com.lpi.ecrandaccueil;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessageBoxUtil
{
	public static final int BOUTON_OK = 1;
	public static final int BOUTON_CANCEL = 2;

	/***
	 * Affiche une MessageBox
	 * @param context
	 * @param titre
	 * @param texte
	 * @param boutons BOUTON_OK | BOUTON_CANCEL
	 * @param listener
	 * @param drawable
	 */
	public static void messageBox(@NonNull Context context, @NonNull String titre, @NonNull String texte, int boutons, final @Nullable Listener listener, final @Nullable Drawable drawable)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titre);
		builder.setMessage(texte);
		if (drawable != null)
			builder.setIcon(drawable);
		if ((boutons & BOUTON_OK) != 0)
			builder.setPositiveButton(R.string.bouton_OK, (dialog, id) ->
			{
				if (listener != null)
					listener.onOk();
			});

		if ((boutons & BOUTON_CANCEL) != 0)
			builder.setNegativeButton(R.string.bouton_annuler, (dialog, id) ->
			{
				if (listener != null)
					listener.onCancel();
			});

		builder.create().show();
	}

	public interface Listener
	{
		void onOk();
		void onCancel();
	}
}

