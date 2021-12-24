package com.lpi.ecrandaccueil;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class FileUtils
{
//	static final HashMap<String, Drawable> _icones = new HashMap<>();
//	static MimeTypeMap _mimeTypeMap = MimeTypeMap.getSingleton();
//
//	public static @Nullable
//	Drawable getIcon(@NonNull final Context context, @NonNull final String path)
//	{
//		String extension = getExtension(path);
//		Drawable d = _icones.getOrDefault(extension, null);
//		if (d != null)
//			return d;
//
//		// Nouvelle extension
//		final Intent intent = new Intent(Intent.ACTION_VIEW);
//		Uri uri = Uri.fromFile(new File(path));
//		String type = getMimeType(context, uri);
//		intent.setData(uri);
//		intent.setType(type);
//		Drawable icon = null;
//
//		final List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
//		for (ResolveInfo match : matches)
//		{
//			icon = match.loadIcon(context.getPackageManager());
//			if (icon != null)
//			{
//				_icones.put(extension, icon);
//				//return icon;
//			}
//		}
//
//		return icon;
//	}
//
//	public static String getMimeType(@NonNull final Context context, @NonNull final Uri uri)
//	{
//		String mimeType = null;
//
//		if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()))
//		{
//			ContentResolver cr = context.getContentResolver();
//			mimeType = cr.getType(uri);
//		}
//		else
//		{
//			String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
//			mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
//		}
//
//		if (mimeType == null)
//		{
//			String extension = getExtension(uri.getEncodedPath());
//			// Derniere chance!
//			if (".ts".equalsIgnoreCase(extension))
//				mimeType = "video/ts";
//			if (".avi".equalsIgnoreCase(extension))
//				mimeType = "video/avi";
//			if (".mp4".equalsIgnoreCase(extension))
//				mimeType = "video/mp4";
//			if (".mkv".equalsIgnoreCase(extension))
//				mimeType = "video/mkv";
//		}
//		return mimeType;
//	}
//
//	/***
//	 * Retourne l'extension d'un fichier
//	 * @param fileName
//	 * @return
//	 */
//	public static String getExtension(final @NonNull String fileName)
//	{
//		final int indice = fileName.lastIndexOf(".");
//		if (indice == -1)
//			return ".";
//		else
//			return fileName.substring(indice);
//	}
//
//	public static void openFile(final @NonNull Context context, final @NonNull File fichier)
//	{
//		if (isVideo(fichier))
//			ouvreAvecVlc(context, fichier);
//		else
//		{
//			// Get URI and MIME type of file
//			Uri uri = Uri.parse("content://" + fichier.getAbsolutePath());
//			String mime = getMimeType(context, uri);
//
//			// Open file with user selected app
//			Intent intent = new Intent();
//			intent.setAction(Intent.ACTION_VIEW);
//			intent.setDataAndType(uri, mime);
//			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(intent);
//		}
//	}
//
//	private static boolean isVideo(final File fichier)
//	{
//		String extension = getExtension(fichier.getAbsolutePath());
//		if (".ts".equalsIgnoreCase(extension))
//			return true;
//		else if (".avi".equalsIgnoreCase(extension))
//			return true;
//		else if (".mp4".equalsIgnoreCase(extension))
//			return true;
//		else if (".mkv".equalsIgnoreCase(extension))
//			return true;
//		return false;
//	}
//
//	public static void ouvreAvecVlc(@NonNull final Context context, final @NonNull File fichier)
//	{
//		Uri uri = Uri.parse("file://" + fichier.getAbsolutePath());
//		Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
//		vlcIntent.setPackage("org.videolan.vlc");
//		vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
//		vlcIntent.putExtra("title", fichier.getName());
//		vlcIntent.putExtra("from_start", false);
//		//vlcIntent.putExtra("subtitles_location", "/sdcard/Movies/Fifty-Fifty.srt"); //subtitles file
//		context.startActivity(vlcIntent);
//	}
}
