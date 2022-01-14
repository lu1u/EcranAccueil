package com.lpi.ecrandaccueil.medias.mediaelements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.lpi.ecrandaccueil.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DevicesRoot extends ElementListeMedias
{
	static private Drawable _icone = null;

	public DevicesRoot()
	{
		super(null);
	}

	@Override public String Nom(@NonNull final Context context)
	{
		return "Stockages externes";
	}

	@Override public @NonNull String Titre(@NonNull final Context context)
	{
		return Nom(context);
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

	@Override public File getFile()
	{
		return null;
	}

	@NonNull @Override
	public ArrayList<ElementListeMedias> getContenu(@NonNull final Context context, @Nullable final ElementListeMedias parent)
	{
		ArrayList<ElementListeMedias> devices = new ArrayList<>();
		try
		{
			// Parcourir la liste des supports de stockage
			StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

			List<StorageVolume> volumes = sm.getStorageVolumes();
			for (StorageVolume v : volumes)
			{
				devices.add(new Device(v, this));
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return devices;
	}

	@Override public String getPath() { return File.separator;}
}
