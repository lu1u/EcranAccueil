package com.lpi.ecrandaccueil;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DialogAPropos
{
	/***
	 * Affichage de la boite "A propos"
	 * @param activity
	 */
	public static void start(@NonNull final Activity activity)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.activity_apropos, null);

		DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.getDefault());
		String message = "Application Id: " + BuildConfig.APPLICATION_ID
				+ "\nBuild type: " + BuildConfig.BUILD_TYPE
				+ "\nVersion name: " + BuildConfig.VERSION_NAME
				+ "\nVersion code: " + BuildConfig.VERSION_CODE
				+ "\nDate: " + format.format( new Date(BuildConfig.TIMESTAMP))
				+ "\nDebug: " + BuildConfig.DEBUG;

		TextView tv = dialogView.findViewById(R.id.textViewDescription);
		tv.setText(message);
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}
}
