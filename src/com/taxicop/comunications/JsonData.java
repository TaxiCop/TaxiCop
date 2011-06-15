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

import com.taxicop.data.Fields;

public class JsonData {

	public static final String RANK = Fields.RANKING;
	public static final String USER = Fields.ID_USR;
	public static final String DESC = Fields.DESCRIPTION;
	public static final String PLATE = Fields.CAR_PLATE;
	float rank;
	String user;
	String desc;
	String plate;
	public JsonData(float rank, String plate, String desc) {
		this.rank=rank;
		this.plate=plate;
		this.desc=desc;
		this.user=user;
	}
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(RANK, rank);
		obj.put(DESC, desc);
		obj.put(PLATE, plate);
		return obj;
	}
	public JSONObject toJSON_HEADER() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(USER, user);
		return obj;
	}
	public JsonData(float rank, String plate, String desc, String user) {
		this.rank=rank;
		this.plate=plate;
		this.desc=desc;
		this.user=user;
	}
	
}
