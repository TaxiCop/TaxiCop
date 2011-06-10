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
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.taxicop.auth.AuthActivity;
import com.taxicop.data.PlateContentProvider;

public class TabSync extends Activity implements OnClickListener {

	ImageButton sync;
	private AccountManager mAccountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_activity);
		sync = (ImageButton) findViewById(R.id.bt_sync);
		sync.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_sync:
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
			if (cuenta == null) {
				Intent auth = new Intent(this, AuthActivity.class);
				startActivity(auth);
			} 
			else if(active>0)
				ContentResolver.requestSync(cuenta,
						PlateContentProvider.AUTHORITY, extras);
			else showToastInfo(getString(R.string.sync_error));
			

			break;

		default:
			break;
		}

	}
	void showToastInfo(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
