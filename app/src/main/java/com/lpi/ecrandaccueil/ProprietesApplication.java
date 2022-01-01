package com.lpi.ecrandaccueil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.ecrandaccueil.applications.ApplicationInstallee;

public class ProprietesApplication
{
	public static void start(@NonNull final Activity activity, @Nullable final ApplicationInstallee application)
	{
		if ( application == null)
			return;

		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.proprietes_application, null);
		dialogView.setBackgroundColor(Color.TRANSPARENT);

		ImageView iIcone = dialogView.findViewById(R.id.imageIconeAppli);
		TextView tvNomAppli = dialogView.findViewById(R.id.tvNomAppli);
		TextView tvPackageName = dialogView.findViewById(R.id.tvPackageName);
		TextView tvNbLancement = dialogView.findViewById(R.id.tvNbLancements);
		TextView tvVersion = dialogView.findViewById(R.id.tvVersion);
		Button bOuvreSysteme = dialogView.findViewById(R.id.buttonProprietes);

		iIcone.setImageDrawable(application.getIcone());
		tvNomAppli.setText(application.getNom());
		tvPackageName.setText(activity.getString(R.string.packagename,application.getPackageName()));
		tvNbLancement.setText(activity.getString(R.string.nbLancements, application.getNbLancements()));
		tvVersion.setText(activity.getString(R.string.version, application.getAppVersion(activity)));

		bOuvreSysteme.setOnClickListener(view ->
		{
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromParts("package", application.getPackageName(), null);
			intent.setData(uri);
			activity.startActivity(intent);
		});

		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}

}
