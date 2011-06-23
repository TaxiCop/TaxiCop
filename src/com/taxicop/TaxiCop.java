package com.taxicop;
/* 
 * PROJECT: TaxiCop
 * --------------------------------------------------------------------------------
 *   Antonio Vanegas hpsaturn(at)gmail.com
 *   Camilo Soto cmsvalenzuela(at)gmail.com
 *   Javier Buitrago javierbuitrago123(at)gmail.com
 *   Website: http://www.taxicop.org
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * For further information please contact.
 *  devel(at)taxicop.org
 * 
 */

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taxicop.R;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class TaxiCop extends TabActivity {
	
	public static final String TAG = "TaxiCop";
	/** Called when the activity is first created. */
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		final TabHost tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("TAB1").setIndicator(getString(R.string.tab_request))
				.setContent(new Intent(this, TabRequest.class)));
		tabHost.addTab(tabHost.newTabSpec("TAB2").setIndicator(getString(R.string.tab_insert))
				.setContent(new Intent(this, TabInsert.class)));
		tabHost.addTab(tabHost.newTabSpec("TAB3").setIndicator(getString(R.string.tab_sync))
				.setContent(new Intent(this, TabSync.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		
		String locale = this.getResources().getConfiguration().locale.getCountry(); 
		Log.d(TAG,"Locale:"+locale);
		Eula.show(this);

	}
	/**********************************************************************
	 * MENU Y PREFERENCIAS SobreEscritura del metodo para el boton de menu.
	 **********************************************************************/

	private final int MENU1 = 1;
	private final int GROUP_DEFAULT = 0;
	static final private int MENU_PREFERENCES = 3;
	private static final int SHOW_PREFERENCES = 1;

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(GROUP_DEFAULT, MENU1, 0, "About").setIcon(
				R.drawable.icon_white_settings); 
		return super.onCreateOptionsMenu(menu);
	}

	private AccountManager mAccountManager;
	ProgressDialog mProgressDialog;

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU1:
			
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					this);
			builder.setMessage(readEula(this));
			builder.create().show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private static final String ASSET_EULA = "EULA";
	private static CharSequence readEula(Activity activity) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(activity.getAssets()
					.open(ASSET_EULA)));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = in.readLine()) != null)
				buffer.append(line).append('\n');
			return buffer;
		} catch (IOException e) {
			return "";
		} finally {
			closeStream(in);
		}
	}
	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}


}
