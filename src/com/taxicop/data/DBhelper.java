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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper {
	

	public static final String CREATE_TABLE1 = "create table " + Fields.TABLE_REPORT
			+ " ( " + Fields.ID_KEY + " integer primary key, "
			+ Fields.RANKING + " real not null, " 
			+ Fields.CAR_PLATE	+ " text not null, " 
			+ Fields.DATE_REPORT + " text not null , "
			+ Fields.DESCRIPTION + " text ) ;";
			

	public static final String CREATE_TABLE2 = "create table " + Fields.TABLE_USERS
			+ " ( " + Fields.ID_KEY + " integer primary key autoincrement, "
			 + Fields.ITH+ " integer not null, "
			+ Fields.ID_USR + " text not null);";

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
			db.execSQL(CREATE_TABLE2);
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
		db.execSQL("DROP TABLE IF EXISTS " + Fields.TABLE_USERS);
		db.execSQL("DROP TABLE IF EXISTS " + Fields.TABLE_REPORT);
		onCreate(db);
	}

}
