package com.houseWang.tssAPI.module;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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

import com.houseWang.tssAPI.constant.ConstURL;

public class TSS {

	private static TSS instance = null;

	private String userName = null;
	private String password = null;
	private String cookie = null;

	private TSS() {
		initTrustSSL();
	}

	public static TSS getInstance() {
		if (instance == null) {
			instance = new TSS();
		}
		return instance;
	}

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

	public void login(String uname, char[] pw, int days) {
		this.userName = uname;
		this.password = new String(pw);

		try {
			URL loginUrl = new URL(ConstURL.LOGIN_REQUEST);
			initTrustSSL();
			HttpsURLConnection loginConn = (HttpsURLConnection) loginUrl
					.openConnection();
			loginConn.setRequestProperty("accept", "*/*");
			loginConn.setRequestProperty("connection", "Keep-Alive");
			loginConn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			loginConn.setDoInput(true);
			loginConn.setDoOutput(true);

			loginConn.setRequestMethod("POST");
			loginConn.setRequestProperty("username", userName);
			loginConn.setRequestProperty("password", password);
			loginConn.setRequestProperty("days", "" + days);
			loginConn.setRequestProperty("Submit", "Login");
			loginConn.connect();
			String feedback = loginConn.getResponseMessage();
			System.out.println("Response: " + feedback);
			cookie = loginConn.getHeaderField("Set-Cookie");
			System.out.println(cookie);
			loginConn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TSS tss = TSS.getInstance();
		String name = "";
		String password = "";
		tss.login(name, password.toCharArray(), 1);
	}
}
