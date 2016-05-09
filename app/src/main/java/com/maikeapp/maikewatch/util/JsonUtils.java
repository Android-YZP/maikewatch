package com.maikeapp.maikewatch.util;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtils {

	public static Integer getInt(JSONObject jo, String key) {
		try {
			return jo.getInt(key);
		} catch (Exception e) {
			return null;
		}
	}

	public static Integer getInt(JSONObject jo, String key, Integer defaultValue) {
		try {
			return jo.getInt(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public static Double getDouble(JSONObject jo, String key) {
		try {
			return jo.getDouble(key);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getString(JSONObject jo, String key) {
		try {
			return jo.getString(key);
		} catch (Exception e) {
			return "";
		}
	}
	public static Boolean getBoolean(JSONObject jo, String key) {
		try {
			return jo.getBoolean(key);
		} catch (Exception e) {
			return false;
		}
	}
	public static JSONObject getObj(JSONObject jo, String key) {
		try {
			return jo.getJSONObject(key);
		} catch (Exception e) {
			return null;
		}
	}
	public static JSONArray getArray(JSONObject jo, String key) {
		try {
			return jo.getJSONArray(key);
		} catch (Exception e) {
			return null;
		}
	}
}
