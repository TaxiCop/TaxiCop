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

import java.io.IOException;
import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.client.NetworkUtilities;
import com.taxicop.auth.Constants;
import com.taxicop.data.Complaint;
import com.taxicop.data.DataBase;
import com.taxicop.data.PlateContentProvider;
import com.taxicop.data.Fields;

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

	String server=NetworkUtilities.URL;
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.d(TAG, "onPerformSync: Start");
		String authtoken = null;

	}
	
	public Complaint query(String who){
		ContentResolver cr= mContext.getContentResolver();
		Cursor c= cr.query(PlateContentProvider.URI_DENUNCIAS, null, Fields.PLACA+"= '"+who+"'", null, null);
		Complaint ret=null;
		while(c.moveToFirst()){
			int rank = c.getInt(c.getColumnIndex(Fields.RANKING));
			String info = "" + (c.getString(c.getColumnIndex(Fields.PLACA)));
			String desc= "" + (c.getString(c.getColumnIndex(Fields.DESCRIPCION)));
			ret= new Complaint(rank, info, desc);
		}
		return ret;		
	}
	public void insertId(Complaint data) {
		Log.i(TAG, "insertIdc: ");
		ContentResolver cr = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Fields.PLACA, data.PLACA);
		values.put(Fields.RANKING, data.RANKING);
		values.put(Fields.DESCRIPCION, data.DESCRIPCION);
		values.put(Fields.DATE_REPORT, new Date().toGMTString());
		cr.insert(PlateContentProvider.URI_DENUNCIAS, values);
	}
	
	
	
	
	
}
