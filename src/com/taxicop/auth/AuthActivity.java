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

package com.taxicop.auth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.taxicop.R;
import com.taxicop.client.NetworkUtilities;
import com.taxicop.data.PlateContentProvider;

/**
 * Activity which displays login screen to the user.
 */
public class AuthActivity extends AccountAuthenticatorActivity {
	public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
	private Pattern pattern;
	private Matcher matcher;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/**
	 * Validate hex with regular expression
	 * 
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public boolean validate(final String hex) {

		matcher = pattern.matcher(hex);
		return matcher.matches();

	}

	private static final String TAG = "AuthActivity";

	private AccountManager mAccountManager;
	private Thread mAuthThread;
	private String mAuthtoken;
	private String mAuthtokenType;
	SharedPreferences myprefs;

	String server = NetworkUtilities.URL;

	/**
	 * If set we are just checking that the user knows their credentials; this
	 * doesn't cause the user's password to be changed on the device.
	 */
	private Boolean mConfirmCredentials = false;

	/** for posting authentication attempts back to UI thread */
	private final Handler mHandler = new Handler();
	private TextView mMessage;
	private String mPassword;
	private EditText mPasswordEdit;

	/** Was the original caller asking for an entirely new account? */
	protected boolean mRequestNewAccount = false;

	private String mUsername, mCountry;
	private EditText mUsernameEdit;
	private Spinner mSpinCountry;
	private Account acc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle icicle) {
		Log.i(TAG, "onCreate(" + icicle + ")");
		super.onCreate(icicle);
		mAccountManager = AccountManager.get(this);
		Log.i(TAG, "loading data from Intent");
		final Intent intent = getIntent();
		mUsername = intent.getStringExtra(PARAM_USERNAME);
		mAuthtokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
		mRequestNewAccount = mUsername == null;
		mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS,
				false);

		Log.i(TAG, "    request new: " + mRequestNewAccount);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.login_activity);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				android.R.drawable.ic_dialog_alert);
		pattern = Pattern.compile(EMAIL_PATTERN);
		mMessage = (TextView) findViewById(R.id.message);
		mUsernameEdit = (EditText) findViewById(R.id.username_edit);
		mPasswordEdit = (EditText) findViewById(R.id.password_edit);
		String option = intent.getStringExtra("country");
		mSpinCountry = (Spinner) findViewById(R.id.spinner1);
		if (!option.equals("null")) {
			mSpinCountry.setVisibility(View.GONE);
			mCountry = option;

		} else {
			mSpinCountry.setVisibility(View.VISIBLE);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this, R.array.countries,
							android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinCountry.setAdapter(adapter);
			mSpinCountry
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							mCountry = ""
									+ getResources().getStringArray(
											R.array.countries_short)[position];
						}

						public void onNothingSelected(AdapterView<?> parent) {

						}
					});

		}

		mUsernameEdit.setText(mUsername);
		mMessage.setText(getMessage());
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(getText(R.string.ui_activity_authenticating));
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				Log.i(TAG, "dialog cancel has been invoked");
				if (mAuthThread != null) {
					mAuthThread.interrupt();
					finish();
				}
			}
		});
		return dialog;
	}

	/**
	 * Handles onClick event on the Submit button. Sends username/password to
	 * the server for authentication.
	 * 
	 * @param view
	 *            The Submit button for which this method is invoked
	 */
	public void handleLogin(View view) {
		if (mRequestNewAccount) {
			mUsername = mUsernameEdit.getText().toString();
		}
		mPassword = mPasswordEdit.getText().toString();
		if (!validate(mUsername)||TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
			mMessage.setText(getMessage());
		} else {
			showProgress();
			// Start authenticating...

			mAuthThread = NetworkUtilities.attemptAuth(mUsername, mPassword,
					mCountry, mHandler, AuthActivity.this);
		}
	}

	/**
	 * Called when response is received from the server for confirm credentials
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller.
	 * 
	 * @param the
	 *            confirmCredentials result.
	 */
	protected void finishConfirmCredentials(boolean result) {
		Log.i(TAG, "finishConfirmCredentials()");
		final Account account = new Account(mUsername, Constants.ACCOUNT_TYPE);
		mAccountManager.setPassword(account, mPassword);
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 
	 * Called when response is received from the server for authentication
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller. Also sets
	 * the authToken in AccountManager for this account.
	 * 
	 * @param the
	 *            confirmCredentials result.
	 */

	protected void finishLogin() {
		Log.i(TAG, "finishLogin()");
		final Account account = new Account(mUsername, Constants.ACCOUNT_TYPE);
		acc = account;
		if (mRequestNewAccount) {
			Bundle data = new Bundle();
			data.putString("country", mCountry);
			mAccountManager.addAccountExplicitly(account, mPassword, data);
			ContentResolver.setSyncAutomatically(account,
					PlateContentProvider.AUTHORITY, true);

		} else {
			mAccountManager.setPassword(account, mPassword);

		}
		ContentResolver.requestSync(account, PlateContentProvider.AUTHORITY,
				new Bundle());
		final Intent intent = new Intent();
		mAuthtoken = mPassword;
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
		if (mAuthtokenType != null
				&& mAuthtokenType.equals(Constants.AUTHTOKEN_TYPE)) {
			intent.putExtra(AccountManager.KEY_AUTHTOKEN, mAuthtoken);
		}
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);

		finish();

	}

	/**
	 * Hides the progress UI for a lengthy operation.
	 */
	protected void hideProgress() {
		dismissDialog(0);
	}

	/**
	 * Called when the authentication process completes (see attemptLogin()).
	 */
	public void onAuthenticationResult(boolean result) {
		Log.i(TAG, "onAuthenticationResult(" + result + ")");
		// Hide the progress dialog
		hideProgress();
		if (result) {
			if (!mConfirmCredentials) {
				finishLogin();
			} else {
				finishConfirmCredentials(true);
			}
		} else {
			Log.e(TAG, "onAuthenticationResult: failed to authenticate");
			if (mRequestNewAccount) {
				// "Please enter a valid username/password.
				mMessage.setText(getText(R.string.login_activity_loginfail_text_both));
			} else {
				// "Please enter a valid password." (Used when the
				// account is already in the database but the password
				// doesn't work.)
				mMessage.setText(getText(R.string.login_activity_loginfail_text_pwonly));
			}
		}

	}

	/**
	 * Returns the message to be displayed at the top of the login dialog box.
	 */
	private CharSequence getMessage() {
		getString(R.string.label);
		if (TextUtils.isEmpty(mUsername)||!validate(mUsername)) {
			// If no username, then we ask the user to log in using an
			// appropriate service.
			final CharSequence msg = getText(R.string.login_activity_newaccount_text);
			return msg;
		}
		if (TextUtils.isEmpty(mPassword)) {
			// We have an account but no password
			return getText(R.string.login_activity_loginfail_text_pwmissing);
		}
		return null;
	}

	/**
	 * Shows the progress UI for a lengthy operation.
	 */
	protected void showProgress() {
		showDialog(0);
	}
}
