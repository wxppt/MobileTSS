package com.houseWang.tssAPI.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.houseWang.tssAPI.constant.ConfConst;
import com.houseWang.tssAPI.helper.HttpsHelper;

public class PostConnection {
	public static final String DEFAULT_CHARSET = ConfConst.DEFAULT_CHARSET;
	private String urlStr;
	private URL url;
	private HttpURLConnection conn;
	private boolean isPost = false;

	HashMap<String, String> form = new HashMap<String, String>();

	public PostConnection(String urlStr) throws IOException {
		this.urlStr = urlStr;
		url = new URL(urlStr);
		init();
	}

	private void init() throws IOException {
		url = new URL(urlStr);
		if (urlStr.toLowerCase().startsWith("https")) {
			HttpsHelper.initTrustSSL();
			conn = (HttpsURLConnection) url.openConnection();
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
	}

	private void post() throws IOException {
		if (!isPost) {
			String formStr = "";
			if (!form.isEmpty()) {
				for (String queryName : form.keySet()) {
					String queryValue = form.get(queryName);
					formStr += queryName + "=" + queryValue + "&";
				}
			}
			PrintWriter out = new PrintWriter(conn.getOutputStream());
			out.print(formStr);
			out.flush();
			isPost = true;
		}
	}

	public void setRequestProperty(String key, Object value) {
		if (conn != null) {
			conn.setRequestProperty(key, value.toString());
		} else {
			System.out.println("Error: " + urlStr
					+ " - Connection not initialize!");
		}
	}

	public void putFormData(String key, Object value) {
		form.put(key, value.toString());
	}

	public Map<String, List<String>> getResponseHeaderFields()
			throws IOException {
		if (conn != null) {
			post();
			return conn.getHeaderFields();
		} else {
			System.out.println("Error: " + urlStr
					+ " - Connection not initialize!");
		}
		return null;
	}

	public String getSourceCode() throws UnsupportedEncodingException,
			IOException {
		String curCharset;
		curCharset = DEFAULT_CHARSET;
		if (conn != null) {
			post();
			BufferedReader bfr = null;
			String source = "";
			bfr = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), curCharset));
			String line;
			while ((line = bfr.readLine()) != null) {
				source += line.trim() + "\n";
			}
			bfr.close();
			return source;
		} else {
			System.out.println("Error: " + urlStr
					+ " - Connection not initialize!");
		}
		return null;
	}
}
