package com.lpi.ecrandaccueil.medias.mediaelements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.storage.StorageVolume;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.lpi.ecrandaccueil.R;

import java.io.File;
import java.util.ArrayList;

public class Device extends ElementListeMedias
{
	private static Drawable _icone;
	final StorageVolume _volume;

	public Device(@NonNull final StorageVolume volume, @NonNull final ElementListeMedias parent)
	{
		super(parent);
		_volume = volume;
	}

	@Override public @NonNull String Titre(@NonNull final Context context)
	{
		return Nom(context);
	}

	@Override @NonNull public String Nom(@NonNull final Context context)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
			return _volume.getMediaStoreVolumeName();
		else
			return _volume.getDescription(context);
	}

	@Override public void setIcon(@NonNull final Context context, @NonNull final ImageView image)
	{
		if (_icone == null)
			_icone = AppCompatResources.getDrawable(context, R.drawable.ic_usb_flash_drive);

		if (_icone != null)
			image.setImageDrawable(_icone);
	}

	@Override public boolean isDirectory()
	{
		return true;
	}

	@Override public String getPath()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
			return _volume.getDirectory().getAbsolutePath();
		else
			return "/storage/" + _volume.getUuid();
	}

	@NonNull @Override
	public ArrayList<ElementListeMedias> getContenu(@NonNull final Context context, @Nullable final ElementListeMedias parent)
	{
		ArrayList<ElementListeMedias> contenu = new ArrayList<>();
		File file = getFile();

		if (file != null)
		{
			// Lien vers le repertoire parent
			contenu.add(new RepertoireParent(parent));

			File[] files = file.listFiles();
			if (files != null)
				for (File f : files)
				{
					if (f.canRead())
					{
						if (f.isDirectory())
							contenu.add(new Repertoire(f, this));
						else
							contenu.add(new Fichier(f, this));
					}
				}
		}
		return contenu;
	}

	@Override public File getFile()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
			return _volume.getDirectory();
		else
			return new File("/storage/" + _volume.getUuid());
	}

}
