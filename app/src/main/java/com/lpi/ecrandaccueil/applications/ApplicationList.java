package com.lpi.ecrandaccueil.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

import com.lpi.ecrandaccueil.Preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ApplicationList
{
	ArrayList<ApplicationInstallee> _applications;


	public ArrayList<ApplicationInstallee> getApplications()
	{
		return _applications;
	}

	public void supprimeApplication(@NonNull final ApplicationInstallee a)
	{
		for (ApplicationInstallee app : _applications)
			if (a.getNom().equals(app.getNom()))
			{
				_applications.remove(app);
				break;
			}
	}

	public void tri()
	{
		triListe(_applications);
	}

	public void ajoute(@NonNull final ApplicationInstallee application)
	{
		if (_applications == null)
			_applications = new ArrayList<>();
		_applications.add(application);
	}


	public ApplicationList()
	{

	}

	public void litApplications(@NonNull final Context context, boolean inclureApplicationsCachees)
	{
		_applications = getListeAsync(context, inclureApplicationsCachees);
	}

	/***
	 * Retrouve la liste des applications installees, a appeler dans un thread d'arriere plan
	 * @param context
	 * @param inclureApplicationsCachees
	 * @return
	 */
	protected ArrayList<ApplicationInstallee> getListeAsync(@NonNull final Context context, boolean inclureApplicationsCachees)
	{
		ArrayList<ApplicationInstallee> a = new ArrayList<>();
		PackageManager packageManager = context.getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> intentActivities = packageManager.queryIntentActivities(mainIntent, 0);

		//mainIntent = new Intent(Intent.ACTION_MAIN, null);
		//mainIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);

		final Collection<? extends ResolveInfo> activities = packageManager.queryIntentActivities(mainIntent, 0);
		if (activities != null)
		{
			intentActivities.addAll(activities);

			for (ResolveInfo resolveInfo : intentActivities)
			{
				if (includeApplication(a, resolveInfo.activityInfo.packageName, inclureApplicationsCachees))
					a.add(new ApplicationInstallee(packageManager, resolveInfo));
			}

			triListe(a);

		}
		return a;
	}

	private boolean includeApplication(final ArrayList<ApplicationInstallee> appList, final String packageName, final boolean inclureApplicationsCachees)
	{
		if (dansLaListe(appList, packageName))
			return false;

		if (inclureApplicationsCachees)
		{
			// On nous dit de re-inclure les applications cachees, alors on annule toutes les applications cachees
			Preferences.getInstance(null).setInt(Preferences.PREF_CACHEE, packageName, 0);
			return true;
		}

		return Preferences.getInstance(null).getInt(Preferences.PREF_CACHEE, packageName, 0) == 0;
	}

	private void triListe(@NonNull ArrayList<ApplicationInstallee> a)
	{
		a.sort((a1, a2) ->
		{
			int l1 = a1.getNbLancements();
			int l2 = a2.getNbLancements();
			// Trier par ordre decroissant de nombre de lancements ou nom
			if (l1 < l2)
				return 1;
			else if (l1 > l2)
				return -1;
			else
				return a1.getNom().compareToIgnoreCase(a2.getNom());
		});
	}

	private boolean dansLaListe(ArrayList<ApplicationInstallee> app, final String packageName)
	{
		for (ApplicationInstallee a : app)
		{
			if (packageName.equalsIgnoreCase(a.getPackageName()))
				return true;
		}
		return false;
	}

	public void demarre(final int noAppli, final Context context)
	{
		if (_applications == null)
			return;
		if (noAppli < 0 || noAppli > _applications.size())
			return;
		ApplicationInstallee appli = _applications.get(noAppli);
		appli.demarre(context);
		triListe(_applications);
	}

	public void affiche(@NonNull final Canvas canvas, final int i, final float x, final float y, final float largeurCase, final float hauteurCase, final boolean selectionnee, @NonNull final ApplicationInstallee.AttributsGraphiques attributs)
	{
		_applications.get(i).affiche(canvas, x, y, largeurCase, hauteurCase, selectionnee, attributs);
	}

	public ApplicationInstallee get(int i)
	{
		if (_applications == null)
			return null;
		if (i < 0 || i >= _applications.size())
			return null;
		return _applications.get(i);
	}

	public int getNb()
	{
		if (_applications == null)
			return 0;
		else
			return _applications.size();
	}

}

