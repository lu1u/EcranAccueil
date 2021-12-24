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

		public static void messageBox(@NonNull Context a, @NonNull String titre, @NonNull String texte, int boutons, final @Nullable Listener listener, final @Nullable Drawable drawable)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(a);
			builder.setTitle(titre);
			builder.setMessage(texte);
			if ( drawable!=null)
			builder.setIcon(drawable);
			if ((boutons & BOUTON_OK) != 0)
				builder.setPositiveButton("OK", (dialog, id) ->
				{
					if (listener != null)
						listener.onOk();
				});

			if ((boutons & BOUTON_CANCEL) != 0)
				builder.setNegativeButton("Annuler", (dialog, id) ->
				{
					if (listener != null)
						listener.onCancel();
				});

			// Create the AlertDialog object and return it
			builder.create().show();
		}

		public interface Listener
		{
			void onOk();
			void onCancel();
		}
	}

