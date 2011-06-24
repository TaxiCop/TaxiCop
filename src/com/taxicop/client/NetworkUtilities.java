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

package com.taxicop.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.taxicop.auth.AuthActivity;
import com.taxicop.comunications.JsonData;
import com.taxicop.data.Fields;
import com.taxicop.data.PlateContentProvider;

/**
 * Provides utility methods for communicating with the server.
 */

/**
 * Provides utility methods for communicating with the server.
 */
public class NetworkUtilities {
	public static final String TAG = "NetworkUtilities";
	boolean busy = false;
	public static Queue<JsonData> adapter;
	String errMsg;
	static String request;
	String response;

	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_COUNTRY = "country";
	public static final String PARAM_REQUEST = "request";

	public static final String PARAM_UPDATED = "timestamp";
	public static final String USER_AGENT = "AuthenticationService/1.0";
	public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
	// public static String URL="192.168.1.6:8001";
	// public static String URL = "www.taxicop.org";
	public static String URL = "hpsaturnserver.appspot.com";
	public static final String BASE_URL = "http://";
	public static final String LAST_SEQ_URI = "/";
	public static final String AUTH_URI = "/auth";
	public static final String JSON_URI_UPLOAD = "/upload";
	public static final String JSON_URI_DOWNLOAD = "/download";
	public static final String ID_URI = "/";
	private static HttpClient mHttpClient;

	public static void CreateHttpClient() {
		Log.i(TAG, "CreateHttpClient(): ");
		mHttpClient = new DefaultHttpClient();
		final HttpParams params = mHttpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
		ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
	}

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {
				}
			}
		};
		t.start();
		return t;
	}

	public static void reset() {
		adapter = new LinkedList<JsonData>();
	}

	public static String process_upload() {
		Log.i(TAG, "process(): enviar datos al servidor");
		JSONArray elements = new JSONArray();
		Iterator<JsonData> i = adapter.iterator();
		String resp = "";
		try {
			if (elements == null || adapter == null || adapter.size() == 0)
				return "-1";
			elements.put(adapter.poll().toJSON_HEADER());
		} catch (JSONException ex) {
			Log.e("Json", "JSONException", ex);
		}
		Log.i(TAG, "process(): listo para enviar " + adapter.size()
				+ " datos al servidor");
		while (i.hasNext()) {
			try {
				elements.put((adapter.poll().toJSON()));
			} catch (JSONException ex) {
				Log.e("Json", "JSONException", ex);
			}
		}
		request = elements.toString();
		try {
			Log.d(TAG, "send = " + request);
			resp = sendToServer(request, JSON_URI_UPLOAD);
			Log.d(TAG, "response: " + resp);
		} catch (Exception ex) {
			Log.e(TAG, "IOException", ex);
		}
		return resp;
	}

	public static String process_download() {
		Log.i(TAG, "process(): enviar datos al servidor");
		JSONArray elements = new JSONArray();
		Iterator<JsonData> i = adapter.iterator();
		String resp = "";
		try {
			if (elements == null || adapter == null || adapter.size() == 0)
				return "-1";
			elements.put(adapter.poll().toJSON_HEADER());
			elements.put(adapter.poll().toJSON_DOWNLOAD());
		} catch (JSONException ex) {
			Log.e("Json", "JSONException", ex);
		}
		Log.i(TAG, "process(): listo para enviar " + adapter.size()
				+ " datos al servidor");

		request = elements.toString();
		try {
			Log.d(TAG, "send = " + request);
			resp = sendToServer(request, JSON_URI_DOWNLOAD);
			Log.d(TAG, "response: " + resp);
		} catch (Exception ex) {
			Log.e(TAG, "IOException", ex);
		}
		return resp;
	}

	private static String sendToServer(final String request, final String url)
			throws IOException {
		final HttpResponse resp;
		final HttpPost post = new HttpPost(BASE_URL + URL + url);
		Log.d(TAG, "URI=" + (BASE_URL + URL + url));
		// post.addHeader("Content-Type", "application/json");
		post.addHeader("Content-Type", "text/vnd.aexp.json.req");
		post.setEntity(new StringEntity(request));
		CreateHttpClient();
		String response = null;
		Log.d(TAG, "sendToServer(): enviando datos a URI="
				+ (BASE_URL + URL + url));
		try {
			resp = mHttpClient.execute(post);
			// BufferedReader reader = new BufferedReader(new
			// InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
			// String json = reader.readLine();

			response = EntityUtils.toString(resp.getEntity());
//			response = response.replaceAll("Status: 200", "")
//			.replaceAll("OK", "")
//			.replaceAll("Content\\-Type: text\\/html", "")
//			.replaceAll("charset=utf\\-8", "")
//			.replaceAll(";", "")
//			.replaceAll("\\\n", "").trim();
			Log.i(TAG, "sendToServer(): datos enviados con respuesta="
					+ response);
			int status = resp.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_OK)
				throw new IOException("HTTP status: "
						+ Integer.toString(status));
		} catch (Exception e) {
			Log.e(TAG, "" + e.getMessage());
		}
		return response;
	}

	public static final String PREF_FILE_NAME = "preferences";

	public static String authenticate(String username, String password,
			String country, Handler handler, final Context context) {
		final HttpResponse resp;
		final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARAM_USERNAME, username));
		params.add(new BasicNameValuePair(PARAM_PASSWORD, password));
		params.add(new BasicNameValuePair(PARAM_COUNTRY, country));
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (final UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}
		final HttpPost post = new HttpPost(BASE_URL + URL + AUTH_URI);
		Log.d(TAG, "URI= " + (BASE_URL + URL + AUTH_URI));
		post.addHeader(entity.getContentType());
		post.setEntity(entity);
		CreateHttpClient();
		Log.i(TAG, "authenticate(): obtencion de autenticacion. ");

		try {
			resp = mHttpClient.execute(post);
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			resp.getEntity().writeTo(outstream);
			byte[] responseBody = outstream.toByteArray();
			String response = new String(responseBody);
//			response = response.replaceAll("Status: 200", "")
//					.replaceAll("OK", "")
//					.replaceAll("Content\\-Type: text\\/html", "")
//					.replaceAll("charset=utf\\-8", "")
//					.replaceAll(";", "")
//					.replaceAll("\\\n", "").trim();

			Log.i(TAG,
					"authenticate(): respuesta obtenida del servidor. response="
							+ response);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "Successful authentication");
				}
				sendResult(true, handler, context);
				return response;
			} else {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "Error authenticating" + resp.getStatusLine());
				}
				sendResult(false, handler, context);
				return response;
			}
		} catch (final IOException e) {
			if (Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG, "IOException when getting authtoken", e);
			}
			sendResult(false, handler, context);
			return null;
		} finally {
			if (Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG, "getAuthtoken completing");
			}
		}
	}

	private static void sendResult(final Boolean result, final Handler handler,
			final Context context) {
		if (handler == null || context == null) {
			return;
		}
		handler.post(new Runnable() {
			public void run() {
				((AuthActivity) context).onAuthenticationResult(result);
			}
		});
	}

	/**
	 * Attempts to authenticate the user credentials on the server.
	 * 
	 * @param username
	 *            The user's username
	 * @param password
	 *            The user's password to be authenticated
	 * @param handler
	 *            The main UI thread's handler instance.
	 * @param context
	 *            The caller Activity's context
	 * @return Thread The thread on which the network mOperations are executed.
	 */
	public static Thread attemptAuth(final String username,
			final String password, final String country, final Handler handler,
			final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String ret = ""
						+ authenticate(username, password, country, handler,
								context);
				Log.d(TAG, "respuesta de autenticacion= " + ret);
				if (!ret.equals("")) {
					try {
						ContentResolver next = context.getContentResolver();
						ContentValues values = new ContentValues();
						values.put(Fields.ITH, 0);
						values.put(Fields.ID_USR, ret);
						next.insert(PlateContentProvider.URI_USERS, values);
						Log.e(TAG, "i did it.");
					} catch (Exception e) {
						Log.e(TAG, "" + e.getMessage());
					}

				}
			}
		};
		return NetworkUtilities.performOnBackgroundThread(runnable);
	}

	public static void add(float rank, String plate, String desc, String user,
			String date) {
		adapter.add(new JsonData(rank, plate, desc, user, date));
	}

	public static void add(String user) {
		adapter.add(new JsonData(user));
	}

	public static void add(int from) {
		adapter.add(new JsonData(from));
	}

}
