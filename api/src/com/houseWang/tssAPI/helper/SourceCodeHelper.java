package com.houseWang.tssAPI.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class SourceCodeHelper {

	public static final String DEFAULT_CHARSET = "gbk";
	private String urlStr = null;
	private URL url = null;
	private HttpURLConnection conn = null;
	private long readTime = 0;

	public SourceCodeHelper(String urlStr) throws IOException {
		this.urlStr = urlStr.replace(" ", "+");
		url = new URL(urlStr.replace(" ", "+"));
		System.out.println("TOUCH: " + url.toString());
	}

	private void openConnection() throws IOException {
		if (urlStr.toLowerCase().startsWith("https")) {
			conn = (HttpsURLConnection) url.openConnection();
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
	}

	private void closeConnection() {
		if (conn != null) {
			conn.disconnect();
		}
	}

	public String getCharset() {
		String charsetLine = null;
		BufferedReader bfr = null;
		try {
			openConnection();
			System.out.println("READ: " + url.toString());
			Date readStart = new Date();
			bfr = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), DEFAULT_CHARSET));
			Date readEnd = new Date();
			readTime = readEnd.getTime() - readStart.getTime();
			System.out.println("READ OK! TIME: " + readTime + "(ms)");
			String line;
			while ((line = bfr.readLine()) != null) {
				if (line.toLowerCase().contains("charset")) {
					charsetLine = line.trim();
					break;
				}
			}
			bfr.close();
			closeConnection();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("error_charset");
		} finally {
			try {
				bfr.close();
			} catch (Exception e) {
			}
		}
		String charset = filterCharset(charsetLine);
		return charset;
	}

	public String getSourceCode(String charset, String cookie)
			throws UnsupportedEncodingException, IOException {
		BufferedReader bfr = null;
		String source = null;
		openConnection();
		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		conn.setRequestProperty(
				"Cookie",
				"slide_order=0; JSESSIONID=4190BFD750A974AE43CF5E1552D06320; course_order=0; __utmz=71084115.1404450229.1.1.utmccn=(direct)|utmcsr=(direct)|utmcmd=(none); __utma=71084115.656670420.1404450229.1405673721.1405755921.17; __utmc=71084115; __utmb=71084115");
		conn.connect();
		System.out.println("READ: " + url.toString());
		Date readStart = new Date();
		bfr = new BufferedReader(new InputStreamReader(conn.getInputStream(),
				charset));
		Date readEnd = new Date();
		readTime = readEnd.getTime() - readStart.getTime();
		System.out.println("READ OK! TIME: " + readTime + "(ms)");
		String line;
		Date lineStart = new Date();
		while ((line = bfr.readLine()) != null) {
			Date lineCur = new Date();
			if (lineCur.getTime() - lineStart.getTime() > 10000) {
				bfr.close();
				return "toolong";
			}
			source += line.trim();
		}
		bfr.close();
		closeConnection();
		if (source.startsWith("null")) {
			source = source.substring(4);
		}
		return source;
	}

	private String filterCharset(String source) {
		String charset = "utf-8";
		if (source != null) {
			String[] spstr = source.split("<");
			for (int i = 0; i < spstr.length; i++) {
				spstr[i] = spstr[i].toLowerCase();
				if (spstr[i].contains("meta") && spstr[i].contains("charset")) {
					spstr[i] = spstr[i].split("charset")[1];
					spstr[i] = spstr[i].replace("\"", "");
					spstr[i] = spstr[i].replace("/", "");
					spstr[i] = spstr[i].replace("\\", "");
					spstr[i] = spstr[i].replace("=", "");
					spstr[i] = spstr[i].replace(">", "");
					spstr[i] = spstr[i].trim();
					charset = spstr[i];
					break;
				}
			}
		}
		return charset;
	}

	public long getReadTime() {
		return readTime;
	}

}