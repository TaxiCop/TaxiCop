/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.taxicop.syncadapter;

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
import com.taxicop.authenticator.Constants;
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
//		try {
//			
//			
//			myprefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//			server = myprefs.getString("ip", NetworkUtilities.URL);
//			// use the account manager to request the credentials
//			authtoken = mAccountManager.blockingGetAuthToken(account,
//					Constants.AUTHTOKEN_TYPE,
//					true /* notifyAuthFailure */);
//			mLastUpdated = new Date();
//			String ret=NetworkUtilities.id(account.name, authtoken,server);
//			Log.d(NetworkUtilities.TAG, "response: "+ret);
//			if((ret!=null)&&!ret.equals("")){
//				Log.i(TAG, "onPerformSync: los datos del servidor llegan correctamente.");
//			
//			}			
//			else{
//				Log.i(TAG, "onPerformSync: DATOS ??? no hay!!!.");
//				Toast.makeText(mContext, "La sinzronizacion fue incorrecta",
//						Toast.LENGTH_SHORT);
//			}
//				
//
//		} catch (final AuthenticatorException e) {
//            syncResult.stats.numParseExceptions++;
//            Log.e(TAG, "AuthenticatorException", e);
//        } catch (final OperationCanceledException e) {
//            Log.e(TAG, "OperationCanceledExcetpion", e);
//        } catch (final IOException e) {
//            Log.e(TAG, "IOException", e);
//            syncResult.stats.numIoExceptions++;
//        } 
        

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
