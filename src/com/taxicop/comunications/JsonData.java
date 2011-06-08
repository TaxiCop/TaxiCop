package com.taxicop.comunications;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonData {
	private String PASS;
	private String USER;
	public static final String Pass = "pw";
	public static final String User = "us";
	public long status;
	public boolean resultSet;
	private int id;
	private int _x;
	private int _y;
	private static int nextId = 0;
	private String date;
	public int idP;
	public static final String DATE = "d";
	public static final String ID_P = "idP";
	public static final String ID_KEY = "id";
	public static final String ID_PARTIDO = "idP";
	public static final String X = "x";
	public static final String Y = "y";
	public static final String ID_TRANS = "idT";
	public static final String STATUS_KEY = "sts";

	public JsonData(int id_trans, int x, int y, String date) {
		this.id = id_trans;
		this._x = x;
		this._y = y;
		this.date = date;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(ID_KEY, id);
		obj.put(X, _x);
		obj.put(Y, _y);
		obj.put(DATE, date);
		return obj;
	}

	public JSONObject toJsonHeader() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(ID_P, idP);
		obj.put(Pass, PASS);
		obj.put(User, USER);
		return obj;
	}

	

	public JsonData(int id, String user, String pass) {
		this.idP = id;
		this.USER=user;
		this.PASS=pass;
		
	}

	public void setStatus(long status) {
		this.status = status;
		resultSet = true;
	}

}
