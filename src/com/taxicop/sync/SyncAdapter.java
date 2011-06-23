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

import org.json.JSONArray;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.taxicop.client.NetworkUtilities;
import com.taxicop.data.Complaint;
import com.taxicop.data.DataBase;
import com.taxicop.data.Fields;
import com.taxicop.data.Lists;
import com.taxicop.data.PlateContentProvider;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	public static final String TAG = "SyncAdapter";

	private final AccountManager mAccountManager;
	private final Context mContext;
	public DataBase dba;
	private Date mLastUpdated;
	ArrayList<ContentValues> insert;

	String USER;
	int FROM;

	public SyncAdapter(Context context, boolean autoInitialize) {

		super(context, autoInitialize);

		mContext = context;
		dba = new DataBase(context);
		mAccountManager = AccountManager.get(context);

		insert = new ArrayList<ContentValues>();
		FROM=0;

	}

	String username;
	String password;
	SharedPreferences myprefs;

	String server = NetworkUtilities.URL;

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		
		USER = queryUser(provider);
		Log.i(TAG, "onPerformSync: Start");
		NetworkUtilities.reset();
		FROM = queryLastId(provider);
		Log.d(TAG, "data from= "+FROM);
		ArrayList<Complaint> queries = query(provider,FROM);
		NetworkUtilities.add(USER);
		for (Complaint c : queries) {
			NetworkUtilities.add(c.RANKING, c.CAR_PLATE, c.DESCRIPTION, c.USER,
					c.DATE);
		}
		Log.d(TAG, "from= "+FROM+ " size del query= "+queries.size());
		
		String response = null;
		if (NetworkUtilities.adapter.size() > 0) {
			response = ":" + NetworkUtilities.process_upload();
			Log.i(TAG, "response: " + response);
		} else
			Log.i(TAG, "no data");
		
		if (USER != null) {
			NetworkUtilities.reset();
			NetworkUtilities.add(USER);
			NetworkUtilities.add(FROM);
			response = null;
			if (NetworkUtilities.adapter.size() > 0) {
				response = NetworkUtilities.process_download();
				Log.d(TAG, ""+response);
			} else
				Log.e(TAG, "no data");
			try {
				if (response != null) {
					final JSONArray cars = new JSONArray(response);
					provider.delete(PlateContentProvider.URI_REPORT, null, null);
					Log.i(TAG, "" + response);
					for (int i = 0; i < cars.length(); i++) {
						JSONObject COMPLETE = cars.getJSONObject(i);
						JSONObject e1 = COMPLETE.getJSONObject("fields");
						float rank = (float) e1.getDouble(Fields.RANKING);
						String car = e1.getString(Fields.CAR_PLATE);
						String desc = e1.getString(Fields.DESCRIPTION);
						String date = e1.getString(Fields.DATE_REPORT);
						ContentValues in = new ContentValues();
						in.put(Fields.ID_KEY, i);
						in.put(Fields.CAR_PLATE, car);
						in.put(Fields.RANKING, rank);
						in.put(Fields.DATE_REPORT, date);
						in.put(Fields.DESCRIPTION, desc);
						insert.add(in);
					}
					Log.d(TAG, "current ammount to insert= "+insert.size()+",  FROM="+FROM);
					ContentValues upd= new ContentValues();
					upd.put(Fields.ITH, insert.size());
					upd.put(Fields.ID_USR, USER);
					
					provider.update(PlateContentProvider.URI_USERS, upd, ""+Fields.ID_USR+" = '"+USER+"'", null);
					//if(insert.size()>FROM)
					provider.applyBatch(insertData());
					insert.clear();
				} else {
					Log.e(TAG, "null response");
				}

			} catch (Exception e) {
				Log.e(TAG, "inserting .... fucked => message: " + e.getMessage());
			}

		}

	}
	@Override
	public void onSyncCanceled() {
		Log.i(TAG, "OnSyncCanceled");
		// TODO Auto-generated method stub
		super.onSyncCanceled();
	}

	private static ContentProviderOperation insert(ContentValues buff) {
		final ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(PlateContentProvider.URI_REPORT);
		builder.withValue(Fields.RANKING, buff.get(Fields.RANKING));
		builder.withValue(Fields.DESCRIPTION, buff.get(Fields.DESCRIPTION));
		builder.withValue(Fields.DATE_REPORT, buff.get(Fields.DATE_REPORT));
		builder.withValue(Fields.CAR_PLATE, buff.get(Fields.CAR_PLATE));
		return builder.build();
	}

	public ArrayList<ContentProviderOperation> insertData() {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		for (ContentValues val : insert) {
			batch.add(insert(val));
		}
		return batch;
	}

	public int queryLastId(ContentProviderClient provider) {		
		Cursor c = null;
		int id = 0;
		try {
			c = provider.query(PlateContentProvider.URI_USERS, null, null,
					null, null);
			if (c.moveToFirst())
				id = c.getInt(c.getColumnIndex(Fields.ITH));

		} catch (Exception e) {
			Log.e(TAG, "lastId= " + e.getMessage());
		}
		c.close();
		return id;
	}
	public int count(ContentProviderClient provider){
		try {
			Cursor c=provider.query(PlateContentProvider.URI_REPORT, null,
					null, null, null);
			int ret=c.getCount();
			c.close();
			return ret;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, ""+e.getMessage());
		}
		return 0;
	}

	public ArrayList<Complaint> query(ContentProviderClient provider, int FROM) {
		ArrayList<Complaint> reports = new ArrayList<Complaint>();
		try {
			Cursor c = provider.query(PlateContentProvider.URI_REPORT, null,
					Fields.ID_KEY+" > "+FROM+"", null, null);
			if (c.moveToFirst()) {
				do {
					float rank = c.getFloat(c.getColumnIndex(Fields.RANKING));
					String plate = ""
							+ (c.getString(c.getColumnIndex(Fields.CAR_PLATE)));
					String desc = ""
							+ (c.getString(c.getColumnIndex(Fields.DESCRIPTION)));
					String date = ""
							+ (c.getString(c.getColumnIndex(Fields.DATE_REPORT)));
					Log.d(TAG, "plate=" + plate);
					reports.add(new Complaint(rank, plate, desc, USER, date));
				} while (c.moveToNext());
			}
			c.close();
		} catch (Exception e) {
			Log.e(TAG, "query= " + e.getMessage());
		}

		return reports;
	}

	public String queryUser(ContentProviderClient provider) {
		Cursor c = null;
		String usr = null;
		try {
			c = provider.query(PlateContentProvider.URI_USERS, null, null,
					null, null);
			if (c.moveToFirst()) {
				usr = c.getString(c.getColumnIndex(Fields.ID_USR));
			}
		} catch (Exception e) {
			Log.e(TAG, "queryuser= " + e.getMessage());
		}
		c.close();
		return usr;
	}

}
