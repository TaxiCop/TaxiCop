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

package com.taxicop.comunications;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonData {
	private String PASS;
	private String USER;
	public static final String Pass = "pw";
	public static final String User = "us";
	public long status;
	public boolean resultSet;
	private int id;
	private int _x;
	private int _y;
	private static int nextId = 0;
	private String date;
	public int idP;
	public static final String DATE = "d";
	public static final String ID_P = "idP";
	public static final String ID_KEY = "id";
	public static final String ID_PARTIDO = "idP";
	public static final String X = "x";
	public static final String Y = "y";
	public static final String ID_TRANS = "idT";
	public static final String STATUS_KEY = "sts";

	public JsonData(int id_trans, int x, int y, String date) {
		this.id = id_trans;
		this._x = x;
		this._y = y;
		this.date = date;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(ID_KEY, id);
		obj.put(X, _x);
		obj.put(Y, _y);
		obj.put(DATE, date);
		return obj;
	}

	public JSONObject toJsonHeader() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(ID_P, idP);
		obj.put(Pass, PASS);
		obj.put(User, USER);
		return obj;
	}

	

	public JsonData(int id, String user, String pass) {
		this.idP = id;
		this.USER=user;
		this.PASS=pass;
		
	}

	public void setStatus(long status) {
		this.status = status;
		resultSet = true;
	}

}
