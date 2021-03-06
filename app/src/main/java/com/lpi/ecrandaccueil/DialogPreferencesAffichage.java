package com.lpi.ecrandaccueil;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.lpi.ecrandaccueil.customviews.EditNumberDPad;
import com.lpi.ecrandaccueil.customviews.ListeApplicationsView;

class DialogPreferencesAffichage
{
	public interface DialogPreferenceAffichageListener
	{
		void onAnimationChanged();
		void onNbParRangeeChanged();
		void onDialogClosed();
	}
	public static void start(@NonNull final Activity activity, @NonNull final DialogPreferenceAffichageListener listener)
	{

		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.proprietes_affichage, null);
		dialogView.setBackgroundColor(Color.TRANSPARENT);

		final Preferences preferences = Preferences.getInstance(activity);

		CheckBox cbAnimations = dialogView.findViewById(R.id.cbAnimations);
		cbAnimations.setChecked(preferences.getBoolean(Preferences.PREF_ANIMATIONS, true));
		cbAnimations.setOnCheckedChangeListener((compoundButton, b) ->
		{
			preferences.setBoolean(Preferences.PREF_ANIMATIONS, b);
			listener.onAnimationChanged();
		});

		EditNumberDPad edNbParRangee = dialogView.findViewById(R.id.editNumberDPad);
		edNbParRangee.setMin(ListeApplicationsView.NB_MIN_ICONES);
		edNbParRangee.setMax(ListeApplicationsView.NB_MAX_ICONES);
		edNbParRangee.setValeur(preferences.getInt(Preferences.PREF_NB_ICONES_PAR_RANGEE, 4));
		edNbParRangee.setListener(valeur ->
		{
			preferences.setInt(Preferences.PREF_NB_ICONES_PAR_RANGEE, valeur);
			listener.onNbParRangeeChanged();
		});

		dialogBuilder.setOnCancelListener(dialogInterface -> listener.onDialogClosed());

		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}
}
