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

package com.taxicop;

import com.taxicop.data.Complaint;
import com.taxicop.data.PlateContentProvider;
import com.taxicop.data.Fields;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class TabRequest extends Activity implements OnClickListener {

	private EditText et_car_plate;
	private Button bt_query;
	private TextView tx_output1, tx_output2;
	private RatingBar ratingBar;
	private static final String TAG = "TabRequest";

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request_activity);

		et_car_plate = (EditText) findViewById(R.id.et_car_plate);
		tx_output1 = (TextView) findViewById(R.id.tx_output1);
		tx_output2 = (TextView) findViewById(R.id.tx_output2);

		bt_query = (Button) findViewById(R.id.bt_query);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		bt_query.setOnClickListener(this);

	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_query:
			String who = "" + et_car_plate.getText().toString().trim();
			StringBuilder processedPlate = null,
			finalStringtoQuery = null;
			if (who != null && !who.equals("") && !who.equals("null")
					&& who.length() >= 6) {
				processedPlate = new StringBuilder(who.replaceAll("\\W", ""));
				if (processedPlate.length() == 6) {
					finalStringtoQuery = new StringBuilder();
					for (int i = 0; i < processedPlate.length(); i++) {

						char ith = (processedPlate.charAt(i));
						if (Character.isLowerCase(ith)) {
							char add = Character.toUpperCase(ith);
							finalStringtoQuery.append(add);
						} else {
							finalStringtoQuery.append(ith);
						}
					}
					Log.e(TAG, "query= " + finalStringtoQuery.toString());

					Complaint result = query(finalStringtoQuery.toString());

					if (result != null) {

						ratingBar.setVisibility(View.VISIBLE);
						ratingBar.setRating(result.RANKING);
						tx_output1.setText(result.PLACA);
						tx_output2.setText(result.DESCRIPCION);
					} else
						showToastInfo(getString(R.string.error_message));
				} else
					showToastInfo(getString(R.string.error_message_length));
			} else
				showToastInfo(getString(R.string.error_message));

			break;

		default:
			break;
		}

	}

	public Complaint query(String who) {
		Complaint ret = null;
		try {
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(PlateContentProvider.URI_DENUNCIAS, null,
					Fields.PLACA + "= '" + who + "'", null, null);

			if (c.moveToFirst()) {
				float rank = c.getFloat(c.getColumnIndex(Fields.RANKING));
				String info = ""
						+ (c.getString(c.getColumnIndex(Fields.PLACA)));
				String desc = ""
						+ (c.getString(c.getColumnIndex(Fields.DESCRIPCION)));
				ret = new Complaint(rank, info, desc);
			}
		} catch (Exception e) {
			Log.e(TAG, "" + e.getMessage());
		}

		return ret;
	}

	void showToastInfo(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
