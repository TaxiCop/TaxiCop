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

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TaxiCop extends TabActivity {
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

	}

}
