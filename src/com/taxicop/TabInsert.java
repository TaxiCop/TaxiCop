package com.taxicop;

import com.taxicop.data.Complaint;
import com.taxicop.data.DataContentProvider;
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

public class TabInsert extends Activity implements OnClickListener,
		RatingBar.OnRatingBarChangeListener {
	private static final String TAG = "TabInsert";
	private EditText et_car_plate, et_desc;
	private RatingBar ratingBar;
	private Button bt_submmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		et_car_plate = (EditText) findViewById(R.id.et_car_plate);
		et_desc = (EditText) findViewById(R.id.et_description);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		bt_submmit = (Button) findViewById(R.id.bt_submmit);
		bt_submmit.setOnClickListener(this);

	}

	public void insertId(Complaint data) {
		Log.i(TAG, "insertIdc: ");
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Fields.PLACA, data.PLACA);
		values.put(Fields.RANKING, data.RANKING);
		values.put(Fields.DESCRIPCION, data.DESCRIPCION);
		cr.insert(DataContentProvider.URI_DENUNCIAS, values);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}
}
