package com.taxicop;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.taxicop.authenticator.AuthenticatorActivity;
import com.taxicop.data.PlateContentProvider;

public class TabSync extends Activity implements OnClickListener {

	ImageButton sync;
	private AccountManager mAccountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_activity);
		sync = (ImageButton) findViewById(R.id.bt_sync);
		sync.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_sync:
			mAccountManager = AccountManager.get(this);
			Account ac[] = mAccountManager.getAccounts();
			Bundle extras = new Bundle();
			Account cuenta = null;
			for (int i = 0; i < ac.length; i++) {
				if (ac[i].type.equals(PlateContentProvider.AUTHORITY)) {
					cuenta = ac[i];
					break;
				}
			}
			int active = ContentResolver.getIsSyncable(cuenta,
					PlateContentProvider.AUTHORITY);
			if (cuenta == null) {
				Intent auth = new Intent(this, AuthenticatorActivity.class);
				startActivity(auth);
			} 
			else if(active>0)
				ContentResolver.requestSync(cuenta,
						PlateContentProvider.AUTHORITY, extras);
			else showToastInfo(getString(R.string.sync_error));
			

			break;

		default:
			break;
		}

	}
	void showToastInfo(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
