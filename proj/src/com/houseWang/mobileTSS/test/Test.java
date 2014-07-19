package com.houseWang.mobileTSS.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Test {
	private static void initTrustSSL() {
		try {
			SSLContext sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null, new TrustManager[] { new X509TrustManager() {
				// do nothing, let the check pass.
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} }, new SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx
					.getSocketFactory());
			HttpsURLConnection
					.setDefaultHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String hostname,
								SSLSession session) {
							return true;
						}
					});
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		URL mURL = new URL("https://218.94.159.102/GlobalLogin/loginservlet");
		initTrustSSL();
		HttpsURLConnection loginConn = (HttpsURLConnection) mURL
				.openConnection();
		System.out.println("Conn ok");
		loginConn.setRequestProperty("accept", "*/*");
		loginConn.setRequestProperty("connection", "Keep-Alive");
		loginConn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		loginConn.setDoInput(true);
		loginConn.setDoOutput(true);

		loginConn.setRequestMethod("POST");
		loginConn.setRequestProperty("username", "wx12");
		loginConn.setRequestProperty("password", "popkart888");
		loginConn.setRequestProperty("days", "1");
		loginConn.setRequestProperty("Submit", "Login");
		loginConn.connect();
		String cookies = loginConn.getHeaderField("Set-Cookie");
		System.out.println(cookies);
		loginConn.disconnect();

		URL listURL = new URL("http://218.94.159.102/tss/en/home/mycourse.html");
		HttpURLConnection requestConn = (HttpURLConnection) listURL
				.openConnection();
		requestConn.setRequestProperty("accept", "*/*");
		requestConn.setRequestProperty("connection", "Keep-Alive");
		requestConn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		requestConn.setRequestProperty("Cookie", cookies);
		requestConn.connect();
		InputStream urlStream = requestConn.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(urlStream));
		String ss = null;
		while ((ss = bufferedReader.readLine()) != null) {
			System.out.println(ss);
		}
		bufferedReader.close();
	}
}
