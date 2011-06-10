package com.taxicop;

import java.util.Date;

import com.taxicop.data.Complaint;
import com.taxicop.data.PlateContentProvider;
import com.taxicop.data.Fields;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class TabInsert extends Activity implements OnClickListener,
		RatingBar.OnRatingBarChangeListener {
	private static final String TAG = "TabInsert";
	private EditText et_car_plate, et_desc;
	private RatingBar ratingBar;
	private Button bt_submmit;
	private float currentRating;

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

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_submmit:
			String plate = et_car_plate.getText().toString().trim();
			String desc = et_desc.getText().toString();
			float rating = ratingBar.getRating();
			StringBuilder processedPlate = null,
			finalStringtoInsert = null;
			if (!plate.equals("") || plate != null || plate.length() >= 6) {
				processedPlate = new StringBuilder(plate.replaceAll("\\W", ""));
				if (processedPlate.length() == 6) {
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
					showToastInfo(getString(R.string.error_message_length));
			} else
				showToastInfo(getString(R.string.error_message));
			currentRating = ratingBar.getRating();
			if (currentRating != -1 && finalStringtoInsert!=null) {
				Log.e(TAG, "" + ratingBar.getRating());
				Complaint newDataToInsert = new Complaint(currentRating,
						finalStringtoInsert.toString(), desc);
				insertId(newDataToInsert);
				showToastInfo(getString(R.string.confirm_message));
				et_car_plate.setText("");
				et_desc.setText("");
				ratingBar.setRating(0.0f);

			} else
				showToastInfo(getString(R.string.error_message_rating));

			break;

		default:
			break;
		}
		// TODO Auto-generated method stub

	}

	public void insertId(Complaint data) {
		Log.i(TAG, "insertIdc: ");
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Fields.PLACA, data.PLACA);
		values.put(Fields.RANKING, data.RANKING);
		values.put(Fields.DESCRIPCION, data.DESCRIPCION);
		values.put(Fields.DATE_REPORT, new Date().toGMTString());
		cr.insert(PlateContentProvider.URI_DENUNCIAS, values);
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
