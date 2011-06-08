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

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Consulte")
				.setContent(new Intent(this, Consultas.class)));

		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Denuncie")
				.setContent(new Intent(this, Denuncias.class)));
		
		tabHost.addTab(tabHost
				.newTabSpec("tab3")
				.setIndicator("Sincronizacion")
				.setContent(new Intent(this, Sincronizacion.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

	}

}
