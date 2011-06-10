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
	private TextView tx_output1,tx_output2;
	private RatingBar ratingBar;
	private static final String TAG="TabRequest";

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request_activity);

		et_car_plate = (EditText) findViewById(R.id.et_car_plate);
		tx_output1 = (TextView) findViewById(R.id.tx_output1);
		tx_output2= (TextView) findViewById(R.id.tx_output2);
		
		bt_query = (Button) findViewById(R.id.bt_query);
		ratingBar= (RatingBar)findViewById(R.id.ratingBar);
		bt_query.setOnClickListener(this);

	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_query:
			String who = "" + et_car_plate.getText().toString();
			if (!who.equals("null") && !who.equals("") && who != null){
				Complaint result=query(who);
				if(result==null){
					
					ratingBar.setVisibility(View.VISIBLE);
					ratingBar.setRating(5.0f);
					tx_output1.setText("EDS-345");
					tx_output2.setText("Taxista GONORREA");
					
					
				}
				else showToastInfo(getString(R.string.error_message));
			}				
			else
				showToastInfo(getString(R.string.error_message));

			break;

		default:
			break;
		}

	}

	public Complaint query(String who) {
		Complaint ret = null;
		try{
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(PlateContentProvider.URI_DENUNCIAS, null,
					Fields.PLACA + "= '" + who + "'", null, null);
			
			while (c.moveToFirst()) {
				float rank = c.getFloat(c.getColumnIndex(Fields.RANKING));
				String info = "" + (c.getString(c.getColumnIndex(Fields.PLACA)));
				String desc = ""
						+ (c.getString(c.getColumnIndex(Fields.DESCRIPCION)));
				ret = new Complaint(rank, info, desc);
			}
		}
		catch (Exception e) {
			Log.e(TAG, ""+e.getMessage());
		}
		
		return ret;
	}
	void showToastInfo(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
