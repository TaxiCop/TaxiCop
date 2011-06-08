package com.taxicop;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TaxiCop extends TabActivity {
	/** Called when the activity is first created. */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TabHost tabHost = getTabHost();

		tabHost.addTab(tabHost.newTabSpec("TAB1").setIndicator(getString(R.string.tab_request))
				.setContent(new Intent(this, TabRequest.class)));

		tabHost.addTab(tabHost.newTabSpec("TAB2").setIndicator(getString(R.string.tab_insert))
				.setContent(new Intent(this, TabInsert.class)));
		
		tabHost.addTab(tabHost.newTabSpec("TAB3").setIndicator(getString(R.string.tab_sync))
				.setContent(new Intent(this, TabSync.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

	}

}
