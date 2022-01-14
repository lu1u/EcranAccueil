package com.lpi.ecrandaccueil.medias.mediaelements;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

/***
 * Classe d'abstraction des elements (fichiers) trouvés sur les supports de stockage
 */
public abstract class ElementListeMedias
{
	protected final @Nullable ElementListeMedias _parent;

	public ElementListeMedias(@Nullable final ElementListeMedias parent)
	{
		_parent = parent;
	}

	public @Nullable ElementListeMedias getParent() { return _parent;}

	public abstract String Nom(@NonNull final Context context);
	public abstract void setIcon(@NonNull final Context context, @NonNull final ImageView image);
	public abstract boolean isDirectory();
	public abstract File getFile();
	public abstract @NonNull
	ArrayList<ElementListeMedias> getContenu(@NonNull final Context context, @Nullable final ElementListeMedias parent);
	public abstract @NonNull String Titre(@NonNull final Context context);
	public abstract String getPath();
}
