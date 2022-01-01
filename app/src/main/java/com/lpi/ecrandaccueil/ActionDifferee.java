package com.lpi.ecrandaccueil;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/***
 * Classe pour implementer des actions differees (multitache), eventuellement avec action sur
 * l'interface utilisateur quand l'operation est terminee
 * Utilisation:
 *  ActionDifferee.execute( ActionDifferee.Action );
 *
 */


public class ActionDifferee
{
	public interface Action
	{
		void onStart( @Nullable Object startObject);
		void onFinish(@Nullable Object finishObject );

		/***
		 * Executer la function
		 * @param startObject
		 * @return
		 */
		@Nullable Object execute(@Nullable Object startObject );
	}

	public static void execute(@NonNull final Action action, @Nullable final Object object)
	{
		new AsyncTask<Object, Object, Object>()
		{
			@Override protected void onPostExecute(final Object object)
			{
				action.onFinish(object);
			}

			@Override protected void onPreExecute()
			{
				super.onPreExecute();
				action.onStart(object);
			}

			@Override
			protected Object doInBackground(final Object... voids)
			{
				return action.execute(object);
			}
		}.execute();
	}
}
