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

public class DataContentProvider extends ContentProvider {

	static final String TAG= "DataContentProvider";	
	public static final Uri URI_DENUNCIAS = Uri
			.parse("content://com.taxicop.taxicop/denuncias");
	public DataBase dba;
	private static final UriMatcher sUriMatcher;
	public static final String AUTHORITY = "com.taxicop.taxicop";
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, Fields.TABLE_REPORT, 1);
		
	}
	@Override
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
			//count=2;
			count=dba.delete(Fields.TABLE_REPORT, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		/*
		 * switch (sUriMatcher.match(uri)) { case 1: return
		 * "vnd.paad.cursor.dir/myprovidercontent"; case 2: return
		 * "vnd.paad.cursor.item/myprovidercontent"; default: throw new
		 * IllegalArgumentException("Unsupported URI: " + _uri); }
		 */
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		switch (sUriMatcher.match(uri)) {
		case 1:
			dba.insertData(Fields.TABLE_REPORT, values);
			return DataContentProvider.URI_DENUNCIAS;
			
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
			c = dba.getData(Fields.TABLE_REPORT, selection, selectionArgs, null,
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
			ret=dba.update(Fields.TABLE_REPORT, values, selection,selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);

		}
		getContext().getContentResolver().notifyChange(uri, null);
		return ret;
	}

}
