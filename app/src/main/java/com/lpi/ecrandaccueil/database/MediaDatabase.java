package com.lpi.ecrandaccueil.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Base des traces (log)
 */
public class MediaDatabase
{
	private static final int NB_MAX_MEDIAS = 100;

	@Nullable
	protected static MediaDatabase INSTANCE = null;
	protected final SQLiteDatabase database;
	protected final DatabaseHelper dbHelper;

	protected MediaDatabase(Context context)
	{
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized MediaDatabase getInstance(Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new MediaDatabase(context);
		}
		return INSTANCE;
	}

	@Override
	public void finalize()
	{
		try
		{
			super.finalize();
		} catch (Throwable throwable)
		{
			throwable.printStackTrace();
		}
		dbHelper.close();
	}

	/***
	 * Ajoute (ou update) une ligne pour memoriser le lancement d'un document
	 * @param path
	 */
	public void ajoute(final @NonNull String path, int longueur, int position)
	{
		try
		{
			if (getNbLignes() > NB_MAX_MEDIAS)
				// Supprimer les NB_MAX_MEDIAS premieres pour eviter que la table des traces ne grandisse trop
				database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_MEDIAS_VUS + " WHERE " + DatabaseHelper.MEDIA_VU_DATE + " IN (SELECT " + DatabaseHelper.MEDIA_VU_DATE + " FROM " + DatabaseHelper.TABLE_MEDIAS_VUS + " ORDER BY " + DatabaseHelper.MEDIA_VU_DATE + " LIMIT 50)");

			int date = DatabaseHelper.calendarToSQLiteDate(null);
			database.execSQL("insert or replace into " + DatabaseHelper.TABLE_MEDIAS_VUS + " VALUES (?,?,?,?)", new Object[]{path, date, longueur, position});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/***
	 * Retrouve le nombre de lignes d'une table
	 * @return nombre de lignes
	 */
	protected int getNbLignes()
	{
		Cursor cursor = database.rawQuery("SELECT COUNT (*) FROM " + DatabaseHelper.TABLE_MEDIAS_VUS, null);
		int count = 0;
		try
		{
			if (null != cursor)
			{
				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					count = cursor.getInt(0);
				}
				cursor.close();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return count;
	}

	@SuppressLint("Range") public @Nullable MediaDBInfo getInfo(@Nullable final String path)
	{
		if (null == path)
			return null;

		MediaDBInfo infos = null;
		try
		{
			Cursor cursor = database.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_MEDIAS_VUS + " WHERE " + DatabaseHelper.MEDIA_VU_PATH + "= ?", new String[]{path});
			if (cursor != null)
			{
				if (cursor.getCount() > 0)
				{
					infos = new MediaDBInfo();
					cursor.moveToFirst();
					infos.longueur = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MEDIA_LONGUEUR));
					infos.position = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MEDIA_POSITION));
				}
				cursor.close();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return infos;
	}
}
