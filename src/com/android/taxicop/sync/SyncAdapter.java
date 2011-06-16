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

package com.android.taxicop.sync;

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
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.taxicop.client.NetworkUtilities;
import com.android.taxicop.data.Complaint;
import com.android.taxicop.data.DataBase;
import com.android.taxicop.data.Fields;
import com.android.taxicop.data.Lists;
import com.android.taxicop.data.PlateContentProvider;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String TAG = "SyncAdapter";

	private final AccountManager mAccountManager;
	private final Context mContext;
	public DataBase dba;
	private Date mLastUpdated;
	ArrayList<ContentValues> insert;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContext = context;
		dba = new DataBase(context);
		mAccountManager = AccountManager.get(context);
		insert = new ArrayList<ContentValues>();
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
		ArrayList<Complaint> queries = query(provider);
		for (Complaint c : queries) {
			NetworkUtilities.add(c.RANKING, c.CAR_PLATE, c.DESCRIPTION, c.USER,
					c.DATE);
		}
		String response;
		if (NetworkUtilities.adapter.size() > 0) {
			response = ":" + NetworkUtilities.process_upload();
			Log.d(TAG, response);
		} else
			Log.e(TAG, "no data");
		int FROM = queryLastId(provider);
		String USER = queryUser(provider);
		if (FROM != -1 && USER != null) {
			try {
				provider.delete(PlateContentProvider.URI_REPORT, null, null);
			} catch (Exception e) {
				Log.e(TAG, "" + e.getMessage());
			}
			NetworkUtilities.reset();

			NetworkUtilities.add(USER);
			NetworkUtilities.add(FROM);
			response = null;
			if (NetworkUtilities.adapter.size() > 0) {
				response = NetworkUtilities.process_download();
				Log.d(TAG, response);
			} else
				Log.e(TAG, "no data");
			try {
				final JSONArray cars = new JSONArray(response);
				Log.d(TAG, "" + response);
				for (int i = 0; i < cars.length(); i++) {
					JSONObject e1 = cars.getJSONObject(i);
					float rank = (float) e1.getDouble(Fields.RANKING);
					String car = e1.getString(Fields.CAR_PLATE);
					String desc = e1.getString(Fields.DESCRIPTION);
					String date = e1.getString(Fields.DATE_REPORT);
					ContentValues in = new ContentValues();
					in.put(Fields.CAR_PLATE, car);
					in.put(Fields.RANKING, rank);
					in.put(Fields.DATE_REPORT, date);
					in.put(Fields.DESCRIPTION, desc);
					insert.add(in);
				}
				try {
					provider.applyBatch(insertData());
					insert.clear();

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					Log.e("Json", e.getMessage());
				} catch (OperationApplicationException e) {
					// TODO Auto-generated catch block
					Log.e("Json", e.getMessage());
				}

			} catch (Exception e) {
				Log.e(TAG, "" + e.getMessage());
			}

		}

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

	public String queryUser(ContentProviderClient provider) {
		Cursor c;
		String usr = null;
		try {
			c = provider.query(PlateContentProvider.URI_USERS, null, null,
					null, null);
			if (c.moveToFirst()) {
				usr = c.getString(c.getColumnIndex(Fields.ID_USR));
			}
		} catch (Exception e) {
			Log.e(TAG, "" + e.getMessage());
		}
		return usr;
	}

	public int queryLastId(ContentProviderClient provider) {
		Cursor c;
		int id = -1;
		try {
			c = provider.query(PlateContentProvider.URI_REPORT, null, null,
					null, null);
			if (c.moveToLast())
				;
			id = c.getInt(c.getColumnIndex(Fields.ID_KEY));

		} catch (Exception e) {
			Log.e(TAG, "" + e.getMessage());
		}
		return id;

	}

	public ArrayList<Complaint> query(ContentProviderClient provider) {
		ArrayList<Complaint> reports = new ArrayList<Complaint>();
		try {

			Cursor c = provider.query(PlateContentProvider.URI_REPORT, null,
					null, null, null);
			if (c.moveToFirst()) {
				do {
					float rank = c.getFloat(c.getColumnIndex(Fields.RANKING));
					String plate = ""
							+ (c.getString(c.getColumnIndex(Fields.CAR_PLATE)));
					String desc = ""
							+ (c.getString(c.getColumnIndex(Fields.DESCRIPTION)));
					String date = ""
							+ (c.getString(c.getColumnIndex(Fields.DATE_REPORT)));
					reports.add(new Complaint(rank, plate, desc,
							queryUser(provider), date));
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, "" + e.getMessage());
		}

		return reports;
	}

}
