package com.lpi.ecrandaccueil.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public class BackgroundTaskWithSpinner
{
	/*******************************************************************************************
	 * Execute une tache en arriere plan, avec une fenetre affichee pendant ce temps (devrait
	 * contenir une progress bar circulaire
	 * @param context
	 * @param layoutId
	 * @param listener
	 *******************************************************************************************/
	public static void execute(@NonNull final Activity context, @LayoutRes int layoutId, @NonNull final TaskListener listener)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();

		LayoutInflater inflater = context.getLayoutInflater();
		final View dialogView = inflater.inflate(layoutId, null);
		dialogBuilder.setView(dialogView);

		new AsyncTask<Void, Void, Void>()
		{
			@Override protected void onPreExecute()
			{
				super.onPreExecute();
				dialogBuilder.show();
			}

			@Override
			protected void onProgressUpdate(final Void... values)
			{
				super.onProgressUpdate(values);
			}

			@Override protected void onPostExecute(final Void aVoid)
			{
				super.onPostExecute(aVoid);
				if (dialogBuilder.isShowing())
					dialogBuilder.dismiss();
				listener.onFinished();
			}

			@Override
			protected Void doInBackground(final Void... voids)
			{
				listener.execute();
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public interface TaskListener
	{
		void execute();         // Tache a effectuer en arriere plan
		void onFinished();      // Tache terminee
	}
}
