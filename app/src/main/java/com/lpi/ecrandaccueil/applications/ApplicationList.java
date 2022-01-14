package com.lpi.ecrandaccueil.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.ecrandaccueil.Preferences;

import java.util.ArrayList;
import java.util.Collection;

public class ApplicationList
{
	ArrayList<ApplicationInstallee> _applications;

	//
	// Options: par défaut, ignorer un certain nombre d'applications
	//
	public static final String[] PACKAGE_NAMES_EXCLUS = {"com.android.deskclock", "com.mediatek.wwtv.tvcenter",
			"com.google.android.tv.remote.service", "com.mstar.android.tv.disclaimercustomization", "com.disney.disneyplus",
			"com.mstar.netflixobserver", "com.android.tv.tou", "com.tpv.xmic.ots.cms.client"};

	private static boolean inPackageExclus(@NonNull final String packageName)
	{
		for (String s : PACKAGE_NAMES_EXCLUS)
			if (packageName.equals(s))
				return true;

		return false;
	}

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
		_applications = getListe(context, inclureApplicationsCachees);
	}

	/***
	 * Retrouve la liste des applications installees, a appeler dans un thread d'arriere plan
	 * @param context
	 * @param inclureApplicationsCachees
	 * @return
	 */
	protected @NonNull
	ArrayList<ApplicationInstallee> getListe(@NonNull final Context context, boolean inclureApplicationsCachees)
	{
		ArrayList<ApplicationInstallee> a = new ArrayList<>();
		PackageManager packageManager = context.getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		//mainIntent = new Intent(Intent.ACTION_MAIN, null);
		//mainIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);

		final Collection<? extends ResolveInfo> activities = packageManager.queryIntentActivities(mainIntent, 0);
		if (activities != null)
		{
			for (ResolveInfo resolveInfo : activities)
			{
				if (includeApplication(a, resolveInfo.activityInfo.packageName, inclureApplicationsCachees))
					a.add(new ApplicationInstallee(packageManager, resolveInfo));
			}

			triListe(a);

		}
		return a;
	}

	private boolean includeApplication(final @NonNull ArrayList<ApplicationInstallee> appList, final @NonNull String packageName, final boolean inclureApplicationsCachees)
	{
		// Option: un certain nombre d'applications exclues automatiquement
		if (inPackageExclus(packageName))
			return false;

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

	private boolean dansLaListe(@NonNull final ArrayList<ApplicationInstallee> app, @NonNull final String packageName)
	{
		for (ApplicationInstallee a : app)
		{
			if (packageName.equalsIgnoreCase(a.getPackageName()))
				return true;
		}
		return false;
	}

	public void demarre(final int noAppli, @NonNull final Context context)
	{
		if (_applications == null)
			return;
		if (noAppli < 0 || noAppli > _applications.size())
			return;
		ApplicationInstallee appli = _applications.get(noAppli);
		appli.demarre(context);
		triListe(_applications);
	}

	public @Nullable ApplicationInstallee get(int i)
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

