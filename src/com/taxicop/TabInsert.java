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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.taxicop.R;
import com.taxicop.data.Complaint;
import com.taxicop.data.Fields;
import com.taxicop.data.PlateContentProvider;
import com.taxicop.sync.SyncAdapter;

public class TabInsert extends Activity implements OnClickListener,
		RatingBar.OnRatingBarChangeListener {
	private static final String TAG = "TabInsert";
	private EditText et_car_plate, et_desc;
	private RatingBar ratingBar;
	private Button bt_submmit;
	private float currentRating;
	private int n;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submmit_activity);
		currentRating = -1.0f;
		et_car_plate = (EditText) findViewById(R.id.et_car_plate);
		et_desc = (EditText) findViewById(R.id.et_description);
		
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		bt_submmit = (Button) findViewById(R.id.bt_submmit);
		bt_submmit.setOnClickListener(this);
		n=0;

	}

	public void onClick(View v) {
		try{
			switch (v.getId()) {
			case R.id.bt_submmit:
				String plate = et_car_plate.getText().toString().trim();
				String desc = et_desc.getText().toString();
				float rating = ratingBar.getRating();
				StringBuilder processedPlate = null,
				finalStringtoInsert = null;
				if (!plate.equals("") && plate != null && plate.length() >= 4) {	
					processedPlate = new StringBuilder(plate.replaceAll("\\^[a-zA-Z0-9]", ""));

					finalStringtoInsert = new StringBuilder();
					for (int i = 0; i < processedPlate.length(); i++) {

						char ith = (processedPlate.charAt(i));
						if (Character.isLowerCase(ith)) {
							char add = Character.toUpperCase(ith);
							finalStringtoInsert.append(add);
						} else {
							finalStringtoInsert.append(ith);
						}

					}
				} else
					showToastInfo(getString(R.string.error_message));

				currentRating = ratingBar.getRating();
				
				if (currentRating != -1 && finalStringtoInsert != null) {
					Log.d(TAG, " insert: "+finalStringtoInsert+" desc "+desc);
					Complaint newDataToInsert = new Complaint(currentRating,
							finalStringtoInsert.toString(), desc);
					insertId(newDataToInsert);
					
					et_car_plate.setText("");
					et_desc.setText("");
					ratingBar.setRating(0.0f);
					et_car_plate.requestFocus();
					showToastInfo(getString(R.string.confirm_message));

				} else
					showToastInfo(getString(R.string.error_message_rating));

				break;

			default:
				break;
			}
		}
		catch (Exception e) {
			Log.e(TAG, ""+e.getMessage());
		}
		
		// TODO Auto-generated method stub

	}

	public void insertId(Complaint data) {
		Log.i(TAG, "insertIdc: ");
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		int ith=count(cr);
		Log.e(SyncAdapter.TAG, "ith ="+(++ith));
		values.put(Fields.ID_KEY, (ith));
		values.put(Fields.CAR_PLATE, data.CAR_PLATE);
		values.put(Fields.RANKING, data.RANKING);
		values.put(Fields.DESCRIPTION, data.DESCRIPTION);
		Date d = new Date();
		String date = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
		values.put(Fields.DATE_REPORT, date);
		cr.insert(PlateContentProvider.URI_REPORT, values);
		cr.notifyChange(PlateContentProvider.URI_REPORT, null);
	}
	public int count(ContentResolver provider){
		try {
			Cursor c=provider.query(PlateContentProvider.URI_REPORT, null,
					null, null, null);
			int ret=c.getCount();
			c.close();
			return ret;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, ""+e.getMessage());
		}
		return 0;
	}
	

	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		Log.e(TAG, "" + rating);
		currentRating = rating;
	}

	void showToastInfo(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
