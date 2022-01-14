package com.lpi.ecrandaccueil.database;

/*
  Utilitaire de gestion de la base de donnees
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "database.db";

	public static final String TABLE_MEDIAS_VUS = "MEDIAS_VUS";

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Medias VUS
	public static final String MEDIA_VU_PATH = "PATH";
	public static final String MEDIA_VU_DATE = "DATE";
	public static final String MEDIA_LONGUEUR = "LONGUEUR";
	public static final String MEDIA_POSITION = "POSITION";
	private static final String DATABASE_NOTIFICATIONS_CREATE = "create table "
			+ TABLE_MEDIAS_VUS + "("
			+ MEDIA_VU_PATH + " string not null unique,"
			+ MEDIA_VU_DATE + " integer,"
			+ MEDIA_LONGUEUR + " integer DEFAULT 0 NOT NULL,"
			+ MEDIA_POSITION + " integer DEFAULT 0 NOT NULL"
			+ ");";

	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	static public int calendarToSQLiteDate(@Nullable Calendar cal)
	{
		if (cal == null)
			cal = Calendar.getInstance();
		return (int) (cal.getTimeInMillis() / 1000L);
	}

//	@NonNull
//	static public Calendar SQLiteDateToCalendar(int date)
//	{
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis((long) date * 1000L);
//		return cal;
//	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIAS_VUS);
			onCreate(db);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		try
		{
			database.execSQL(DATABASE_NOTIFICATIONS_CREATE);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
