package com.taxicop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper {
	
	
	static final String CREATE_TABLE1 = "create table "+ 
			Fields.TABLE_DENUNCIAS + " ( " + 
			Fields.ID_KEY + " integer primary key autoincrement, " + 
			Fields.RANKING+" real not null, "+
			Fields.PLACA+" integer not null, "+
			Fields.DATE_NAME + " text not null , " +
			Fields.DESCRIPCION + " text not null  );";
	
	public DBhelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);

		// TODO Auto-generated constructor stub
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v("open onCreate", "Creating all the tables");

		try {
		
			db.execSQL(CREATE_TABLE1);
		} catch (SQLiteException ex) {
			Log.v("open exception caught", ex.getMessage());

		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Fields.TABLE_DENUNCIAS);
		onCreate(db);
	}

}
