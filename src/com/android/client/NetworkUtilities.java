/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.client;

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
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.taxicop.authenticator.AuthenticatorActivity;
import com.taxicop.comunications.JsonData;

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

	public static final String PARAM_USERNAME = "us";
	public static final String PARAM_PASSWORD = "pw";
	public static final String PARAM_REQUEST = "request";

	public static final String PARAM_UPDATED = "timestamp";
	public static final String USER_AGENT = "AuthenticationService/1.0";
	public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
	public static String URL="173.230.155.201:80/asteras";
	public static final String BASE_URL ="http://";
	public static final String LAST_SEQ_URI =  "/";
	public static final String AUTH_URI =  "/";
	public static final String JSON_URI = "/";
	public static final String ID_URI =  "/";
	private static HttpClient mHttpClient;

	/**
	 * Configures the httpClient to connect to the URL provided.
	 */
	public static void CreateHttpClient() {
		Log.i(TAG, "CreateHttpClient(): ");
		mHttpClient = new DefaultHttpClient();
		final HttpParams params = mHttpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
		ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
	}

	/**
	 * Executes the network requests on a separate thread.
	 * 
	 * @param runnable
	 *            The runnable instance containing network mOperations to be
	 *            executed.
	 */
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

	public static String process(String url) {
		Log.i(TAG, "process(): enviar datos al servidor");
		JSONArray elements = new JSONArray();
		Iterator<JsonData> i = adapter.iterator();
		String resp = "";
		try {
			if (elements == null || adapter == null || adapter.size() == 0)
				return "-1";
			elements.put(adapter.poll().toJsonHeader());
		} catch (JSONException ex) {
			Log.e("Json", "JSONException", ex);
		}
		Log.i(TAG, "process(): listo para enviar " +adapter.size()+" datos al servidor");
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
			resp = sendToServer(request,url);
			Log.d(TAG, "response: " + resp);
		} catch (Exception ex) {
			Log.e(TAG, "IOException", ex);
		}
		return resp;
	}

	private static String sendToServer(final String request,final String url) throws IOException {
		final HttpResponse resp;
		final HttpPost post = new HttpPost(BASE_URL+url+JSON_URI);
		Log.d(TAG, "URI=" + (BASE_URL+url+JSON_URI));
		post.addHeader("Content-Type", "text/vnd.aexp.json.req");
		post.setEntity(new StringEntity(request));
		CreateHttpClient();
		String response = "-";
		Log.d(TAG, "sendToServer(): enviando datos a URI=" + (BASE_URL + url + JSON_URI));
		try {
			resp = mHttpClient.execute(post);
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			resp.getEntity().writeTo(outstream);
			byte[] responseBody = outstream.toByteArray();
			response = new String(responseBody);
			Log.i(TAG, "sendToServer(): datos enviados con respuesta="+ response);
			int status = resp.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_OK)
				throw new IOException("HTTP status: "
						+ Integer.toString(status));
		} catch (Exception e) {
			Log.e(TAG, "" + e.getMessage());
		}
		return response;
	}

	public static String id(String password, String username,final String url)
			throws IOException {
		
		final HttpResponse resp;
		String response = "-1";
		final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARAM_USERNAME, username));
		params.add(new BasicNameValuePair(PARAM_PASSWORD, password));
		
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (final UnsupportedEncodingException e) {
			// this should never happen.
			throw new AssertionError(e);
		}
		final HttpPost post = new HttpPost(BASE_URL+url+ID_URI);
		Log.d(TAG, "id(): obtencion de id del partido. URI=" + (BASE_URL+url+ID_URI));
		post.addHeader(entity.getContentType());
		post.setEntity(entity);
		CreateHttpClient();
		try {
			resp = mHttpClient.execute(post);
			
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			resp.getEntity().writeTo(outstream);
			byte[] responseBody = outstream.toByteArray();
			response = new String(responseBody);
			Log.d(TAG, "id(): obtencion de id del partido. con respuesta="+response);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return response;
			} else {
				return response;
			}
		} catch (final IOException e) {
			return response;
		}
	}

	/**
	 * Connects to the Voiper server, authenticates the provided username and
	 * password.
	 * 
	 * @param username
	 *            The user's username
	 * @param password
	 *            The user's password
	 * @param handler
	 *            The hander instance from the calling UI thread.
	 * @param context
	 *            The context of the calling Activity.
	 * @return boolean The boolean result indicating whether the user was
	 *         successfully authenticated.
	 */
	public static final String PREF_FILE_NAME = "preferences";

	public static String ultimoConsecutivo(String username, String password,final String url,
			String idpartido, Handler handler, final Context context) {
		final HttpResponse resp;
		String response = "-1";
		
		final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARAM_USERNAME, username));
		params.add(new BasicNameValuePair(PARAM_PASSWORD, password));
		params.add(new BasicNameValuePair(PARAM_REQUEST, idpartido));
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (final UnsupportedEncodingException e) {
			// this should never happen.
			throw new AssertionError(e);
		}
		final HttpPost post = new HttpPost(BASE_URL+url+LAST_SEQ_URI);
		Log.d(TAG, "ultimoConsecutivo(): URI= " + ((BASE_URL + url + LAST_SEQ_URI)));
		post.addHeader(entity.getContentType());
		post.setEntity(entity);
		CreateHttpClient();
		try {
			resp = mHttpClient.execute(post);
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			resp.getEntity().writeTo(outstream);
			byte[] responseBody = outstream.toByteArray();
			response = new String(responseBody);
			Log.i(TAG, "ultimoConsecutivo(): respuesta obetenida del servidor : "+response );
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
			return response;
		} finally {
			if (Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG, "getAuthtoken completing");
			}
		}
	}

	public static boolean authenticate(String username, String password,
			final String url,Handler handler, final Context context) {
		final HttpResponse resp;
		final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARAM_USERNAME, username));
		params.add(new BasicNameValuePair(PARAM_PASSWORD, password));
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (final UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}
		final HttpPost post = new HttpPost(BASE_URL+url+AUTH_URI);
		Log.d(TAG, "URI= " + ((BASE_URL+"-"+url+"-"+AUTH_URI)));
		post.addHeader(entity.getContentType());
		post.setEntity(entity);
		CreateHttpClient();
		Log.i(TAG, "authenticate(): obtencion de autenticacion. " );
		
		try {
			resp = mHttpClient.execute(post);
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			resp.getEntity().writeTo(outstream);
			byte[] responseBody = outstream.toByteArray();
			String response = new String(responseBody);
			Log.i(TAG, "authenticate(): respuesta obtenida del servidor. response="+response );
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "Successful authentication");
				}
				sendResult(true, handler, context);
				return true;
			} else {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "Error authenticating" + resp.getStatusLine());
				}
				sendResult(false, handler, context);
				return false;
			}
		} catch (final IOException e) {
			if (Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG, "IOException when getting authtoken", e);
			}
			sendResult(false, handler, context);
			return false;
		} finally {
			if (Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG, "getAuthtoken completing");
			}
		}
	}

	/**
	 * Sends the authentication response from server back to the caller main UI
	 * thread through its handler.
	 * 
	 * @param result
	 *            The boolean holding authentication result
	 * @param handler
	 *            The main UI thread's handler instance.
	 * @param context
	 *            The caller Activity's context.
	 */
	private static void sendResult(final Boolean result, final Handler handler,
			final Context context) {
		if (handler == null || context == null) {
			return;
		}
		handler.post(new Runnable() {
			public void run() {
				((AuthenticatorActivity) context)
						.onAuthenticationResult(result);
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
			final String password, final Handler handler, final Context context,final String url) {
		final Runnable runnable = new Runnable() {
			public void run() {
				authenticate(username, password, url,handler, context);
			}
		};
		// run on background thread.
		return NetworkUtilities.performOnBackgroundThread(runnable);
	}


	

	public static void add(int I, int X, int Y, String date) {
		adapter.add(new JsonData(I, X,Y, date));
	}

	public static void addHeader(int I, String u, String p) {
		adapter.add(new JsonData(I,u,p));
	}



}

