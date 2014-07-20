package com.houseWang.tssAPI.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.houseWang.tssAPI.constant.ConfConst;
import com.houseWang.tssAPI.helper.HttpsHelper;

public class GetConnection {
	public static final String DEFAULT_CHARSET = ConfConst.DEFAULT_CHARSET;
	private String urlStr;
	private URL url;
	private HttpURLConnection conn;
	private boolean isConnected = false;

	public GetConnection(String urlStr) throws IOException {
		this.urlStr = urlStr;
		url = new URL(urlStr);
		init();
	}

	public GetConnection(String urlStr, HashMap<String, String> queryList)
			throws IOException {
		if (!queryList.isEmpty()) {
			urlStr += "?";
			for (String queryName : queryList.keySet()) {
				String queryValue = queryList.get(queryName);
				urlStr += queryName + "=" + queryValue + "&";
			}
		}
		this.urlStr = urlStr;
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
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
	}

	public void setRequestProperty(String key, Object value) {
		if (conn != null) {
			conn.setRequestProperty(key, value.toString());
		} else {
			System.out.println("Error: " + urlStr
					+ " - Connection not initialize!");
		}
	}

	public Map<String, List<String>> getResponseHeaderFields()
			throws IOException {
		if (conn != null) {
			if (!isConnected) {
				conn.connect();
				isConnected = true;
			}
			return conn.getHeaderFields();
		} else {
			System.out.println("Error: " + urlStr
					+ " - Connection not initialize!");
		}
		return null;
	}

	public String getSourceCode(String charset)
			throws UnsupportedEncodingException, IOException {
		String curCharset;
		if (charset != null) {
			curCharset = charset;
		} else {
			curCharset = DEFAULT_CHARSET;
		}
		if (!isConnected) {
			conn.connect();
			isConnected = true;
		}
		BufferedReader bfr = null;
		String source = "";
		bfr = new BufferedReader(new InputStreamReader(conn.getInputStream(),
				curCharset));
		String line;
		while ((line = bfr.readLine()) != null) {
			source += line.trim() + "\n";
		}
		bfr.close();
		return source;
	}
}
