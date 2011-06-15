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

package com.taxicop;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.taxicop.auth.AuthActivity;
import com.taxicop.data.PlateContentProvider;

public class TabSync extends Activity implements OnClickListener {

	private static final String TAG = "TabSync";
	ImageButton sync;
	private AccountManager mAccountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_activity);
		sync = (ImageButton) findViewById(R.id.bt_sync);
		sync.setOnClickListener(this);

	}

	public static Criteria createCoarseCriteria() {

		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		c.setAltitudeRequired(false);
		c.setBearingRequired(false);
		c.setSpeedRequired(false);
		c.setCostAllowed(false);
		// c.setPowerRequirement(Criteria.POWER_HIGH);
		return c;

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_sync:
			LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			LocationProvider low = locMgr.getProvider(locMgr.getBestProvider(
					(createCoarseCriteria()), false));
			Location location = locMgr.getLastKnownLocation(low.getName());
			Geocoder gcd = new Geocoder(this, Locale.getDefault());
			List<Address> addresses;
			mAccountManager = AccountManager.get(this);
			Account ac[] = mAccountManager.getAccounts();
			Bundle extras = new Bundle();
			Account cuenta = null;
			for (int i = 0; i < ac.length; i++) {
				if (ac[i].type.equals(PlateContentProvider.AUTHORITY)) {
					cuenta = ac[i];
					break;
				}
			}
			int active = ContentResolver.getIsSyncable(cuenta,
					PlateContentProvider.AUTHORITY);
			
			try {
				if (location != null) {
					addresses = gcd.getFromLocation(location.getLatitude(),
							location.getLongitude(), 1);
					if (addresses.size() > 0)
						Log.d(TAG, "city: " + addresses.get(0).getLocality()
								+ " country: "
								+ addresses.get(0).getCountryName());
				
					if (cuenta == null) {
						ContentResolver.requestSync(cuenta,
								PlateContentProvider.AUTHORITY, extras);
//						Intent auth = new Intent(this, AuthActivity.class);
//						auth.putExtra("country",addresses.get(0).getCountryCode());
//						startActivity(auth);
					} 
				} else {
					if (cuenta == null) {
						Intent auth = new Intent(this, AuthActivity.class);
						auth.putExtra("country","null");
						startActivity(auth);
					} else if (active > 0)
						ContentResolver.requestSync(cuenta,
								PlateContentProvider.AUTHORITY, extras);
					else
						showToastInfo(getString(R.string.sync_error));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "" + e.getMessage());
			}
			break;

		default:
			break;
		}

	}

	void showToastInfo(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
