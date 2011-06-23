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

public class Complaint {
	public float RANKING;
	public String CAR_PLATE;
	public String DESCRIPTION;
	public String USER;
	public String DATE;
	public 	Complaint(float r, String p, String d,String u,String date){
		RANKING=r;
		CAR_PLATE=p;
		DESCRIPTION=d;
		USER=u;
		DATE=date;
	}
	public 	Complaint(float r, String p, String d){
		RANKING=r;
		CAR_PLATE=p;
		DESCRIPTION=d;
		
	}

}
