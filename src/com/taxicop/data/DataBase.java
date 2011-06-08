package com.taxicop.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DataBase {
	public SQLiteDatabase db;
	private final Context context;
	
	private final DBhelper dbhelper;
	private int n;
	public static final String TAG = "DataBase";

	public DataBase(Context c) {
		n = 0;
		context = c;
		dbhelper = new DBhelper(context, Fields.DATABASE_NAME, null,
				Fields.DATABASE_VERSION);
	}

	public void close() {
		db.close();
	}

	public void open() throws SQLiteException {
		try {
			db = dbhelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("open exception caught", ex.getMessage());
			db = dbhelper.getReadableDatabase();
			
		}
	}
	public void eraseData()throws SQLiteException{
		
		db.execSQL("DROP TABLE IF EXISTS " + Fields.TABLE_DENUNCIAS);
		db.execSQL(DBhelper.CREATE_TABLE1);
	}

	public int delete(String table, String whereClause, String [] whereArgs) {
		Log.i(TAG, "delete()");
		return db.delete(table, whereClause, whereArgs);
	}

	public long insertData(String Table, ContentValues values) {
		try {
			return db.insertOrThrow(Table, null, values);
		} catch (SQLiteException ex) {
			Log.e("SQLITE ERROR", ex.getMessage());
			return -1;
			
		} 

	}
	public Cursor getData(String table, String selection,
			String[] selectionArgs, Object object2, Object object3,
			Object object4) {
		Log.i(TAG, "getData(): obtener todos los datos por tabla con seleccion");
		Cursor c = db.query(table, selectionArgs, selection,
				null, null, null, null);
		// TODO Auto-generated method stub
		return c;
	}
	
	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		Log.i(TAG, "update(): actualizaciones.");
		return db.update(table, values, whereClause, whereArgs);
	}


}
