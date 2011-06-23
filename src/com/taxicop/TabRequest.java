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

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.taxicop.R;
import com.taxicop.data.Complaint;
import com.taxicop.data.Fields;
import com.taxicop.data.PlateContentProvider;

public class TabRequest extends Activity implements OnClickListener {

	private EditText _etCarPlate;
	private Button _bt_query, _bt_back;
	private TextView _txCarPlate, _txDescrip;
	private LinearLayout _OutLayout, _InputLayout;
	private RatingBar _RatingBar;
	private static final String TAG = "TabRequest";
	private boolean MainViewState=true;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request_activity);
		
		_OutLayout   = (LinearLayout)findViewById(R.id.OutLayout);
		_InputLayout = (LinearLayout)findViewById(R.id.InputLayout);
		_etCarPlate = (EditText) findViewById(R.id.et_car_plate);
		_txCarPlate = (TextView) findViewById(R.id.txOutCarPlate);
		_txDescrip = (TextView) findViewById(R.id.txOutDescription);

		_bt_query = (Button) findViewById(R.id.bt_query);
		_bt_back= (Button) findViewById(R.id.bt_back);
		_RatingBar = (RatingBar) findViewById(R.id.ratingBar);
		_bt_query.setOnClickListener(this);
		_bt_back.setOnClickListener(this);

	}
	

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_query:
			String who = "" + _etCarPlate.getText().toString().trim();
			StringBuilder processedPlate = null,
			finalStringtoQuery = null;
			if (who != null && !who.equals("") && !who.equals("null")
					&& who.length() >= 4) {
				processedPlate = new StringBuilder(who.replaceAll("\\[a-zA-Z0-9]", ""));
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
				_etCarPlate.setText("");
				if (result != null) {
					
					MainViewState=false;  // En vista de resultados
					
					_bt_back.setVisibility(View.VISIBLE);
					_OutLayout.setVisibility(View.VISIBLE);
					_InputLayout.setVisibility(View.GONE);
					_OutLayout.setBackgroundResource(R.drawable.back_out);
					_RatingBar.setRating(result.RANKING);
					_txCarPlate.setText(result.CAR_PLATE);
					_txDescrip.setText(result.DESCRIPTION);
					
				} else
					showToastInfo(getString(R.string.error_message));
			} else
				showToastInfo(getString(R.string.error_message_length));

			break;
		case R.id.bt_back:
			LoadMainView();
			break;

		default:
			break;
		}

	}

	public Complaint query(String who) {
		Complaint ret = null;
		try {
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(PlateContentProvider.URI_REPORT, null,
					Fields.CAR_PLATE + "= '" + who + "'", null, null);

			if (c.moveToFirst()) {
				float rank = c.getFloat(c.getColumnIndex(Fields.RANKING));
				String plate = ""
						+ (c.getString(c.getColumnIndex(Fields.CAR_PLATE)));
				String desc = ""
						+ (c.getString(c.getColumnIndex(Fields.DESCRIPTION)));
				
				ret = new Complaint(rank, plate, desc);
			}
		} catch (Exception e) {
			Log.e(TAG, "" + e.getMessage());
		}

		return ret;
	}
	
	public void LoadMainView(){
		_InputLayout.setVisibility(View.VISIBLE);
		_bt_back.setVisibility(View.GONE);
		_OutLayout.setVisibility(View.GONE);
		MainViewState=true;
		
	}
	
	@Override
	public void onBackPressed() {
		if(!MainViewState)LoadMainView();
		else this.finish();
			
	}

	void showToastInfo(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
