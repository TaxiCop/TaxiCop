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

package com.taxicop.data;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class PlateContentProvider extends ContentProvider {

	static final String TAG = "PlateContentProvider";
	public static final Uri URI_REPORT = Uri
			.parse("content://com.taxicop.taxicop/report");
	public static final Uri URI_USERS = Uri
			.parse("content://com.taxicop.taxicop/users");
	public DataBase dba;
	private static final UriMatcher sUriMatcher;
	public static final String AUTHORITY = "com.taxicop.taxicop";
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, Fields.TABLE_REPORT, 1);
		sUriMatcher.addURI(AUTHORITY, Fields.TABLE_USERS, 2);
	}
	
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		Log.i(TAG, "applyBatch(): fast db operations");
		final SQLiteDatabase db = dba.db;
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; i++) {
				results[i] = operations.get(i).apply(this, results, i);
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int count;
		switch (sUriMatcher.match(uri)) {
		case 1:
				count = dba.delete(Fields.TABLE_REPORT, selection, selectionArgs);
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		switch (sUriMatcher.match(uri)) {
		case 1:
			dba.insertData(Fields.TABLE_REPORT, values);
			return PlateContentProvider.URI_REPORT;
		case 2:
			dba.insertData(Fields.TABLE_USERS, values);
			return PlateContentProvider.URI_USERS;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		

	}

	@Override
	public boolean onCreate() {
		Log.i(TAG, "onCreate");
		// TODO Auto-generated method stub
		dba = new DataBase(getContext());
		dba.open();
		// DB = dba.dbhelper.getWritableDatabase();
		return false;
	}

	// SQLiteDatabase DB;
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Cursor c = null;
		switch (sUriMatcher.match(uri)) {
		case 1:
			c = dba.getData(Fields.TABLE_REPORT, selection, selectionArgs,
					null, null, null);
			break;
		case 2:
			c = dba.getData(Fields.TABLE_USERS, selection, selectionArgs, null,
					null, null);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int ret;
		switch (sUriMatcher.match(uri)) {
		case 1:
			ret = dba.update(Fields.TABLE_REPORT, values, selection,
					selectionArgs);
			break;
		case 2:
			ret = dba.update(Fields.TABLE_USERS, values, selection,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);

		}
		getContext().getContentResolver().notifyChange(uri, null);
		return ret;
	}

}
