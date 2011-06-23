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

package com.taxicop.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.taxicop.R;
import com.taxicop.TabRequest;
import com.taxicop.auth.Constants;
import com.taxicop.data.PlateContentProvider;

/**
 * Service to handle Account sync. This is invoked with an intent with action
 * ACTION_AUTHENTICATOR_INTENT. It instantiates the syncadapter and returns its
 * IBinder.
 */
public class SyncService extends Service {
	private static final Object sSyncAdapterLock = new Object();
	private static SyncAdapter sSyncAdapter = null;
	private NotificationManager mNM;
	private static final int DIALOG_LOADING = 0;
	public ProgressDialog dialog;
	private AccountManager mAccountManager;
	private int NOTIFICATION = 0;

	private void showNotification() {
		Notification notification = new Notification(R.drawable.icon_sync,
				null, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, TabRequest.class), 0);
		notification.setLatestEventInfo(this, null, null, contentIntent);
		mNM.notify(NOTIFICATION, notification);
		
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				sSyncAdapter = new SyncAdapter(getApplicationContext(), false);
			}
		}
	
	}


	/*
	 * {@inheritDoc}
	 */
	@Override
	public IBinder onBind(Intent intent) {
		
		return sSyncAdapter.getSyncAdapterBinder();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mNM.cancel(NOTIFICATION);
		stopSelf();
		mAccountManager = AccountManager.get(this);
		Log.i(SyncAdapter.TAG, "onDestroy");
		
		Account ac[] = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
		for (int i = 0; i < ac.length; i++) {
			ContentResolver.cancelSync(ac[i],PlateContentProvider.AUTHORITY);
			ContentResolver.setSyncAutomatically(ac[i], PlateContentProvider.AUTHORITY, false);
		}
		
		
		
	}

}
