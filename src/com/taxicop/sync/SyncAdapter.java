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

import java.util.ArrayList;
import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.android.client.NetworkUtilities;
import com.taxicop.data.Complaint;
import com.taxicop.data.DataBase;
import com.taxicop.data.Fields;
import com.taxicop.data.PlateContentProvider;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the
 * platform ContactOperations provider.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String TAG = "SyncAdapter";

	private final AccountManager mAccountManager;
	private final Context mContext;
	public DataBase dba;
	private Date mLastUpdated;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContext = context;
		dba = new DataBase(context);
		mAccountManager = AccountManager.get(context);
	}

	String username;
	String password;
	SharedPreferences myprefs;

	String server = NetworkUtilities.URL;

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.d(TAG, "onPerformSync: Start");
		NetworkUtilities.reset();
		ArrayList<Complaint> queries = query();

		for (Complaint c : queries) {
			NetworkUtilities.add(c.RANKING, c.CAR_PLATE, c.DESCRIPTION, c.USER);
		}
		String response;
		if (NetworkUtilities.adapter.size() > 0) {
			response = ":" + NetworkUtilities.process();
			Log.d(TAG, response);
		} else
			Log.e(TAG, "no data");

	}

	public ArrayList<Complaint> query(){
		ArrayList<Complaint> reports= new ArrayList<Complaint>();
		ContentResolver cr= mContext.getContentResolver();
		Cursor c= cr.query(PlateContentProvider.URI_REPORT, null, null, null, null);
		try{
			while(c.moveToFirst()){
				float rank = c.getFloat(c.getColumnIndex(Fields.RANKING));
				String plate =  (c.getString(c.getColumnIndex(Fields.CAR_PLATE)));
				String desc=  (c.getString(c.getColumnIndex(Fields.DESCRIPTION)));
				reports.add(new Complaint(rank, plate, desc));
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, ""+e.getMessage());
		}
		c.close();
		
		return reports;		
	}

}
