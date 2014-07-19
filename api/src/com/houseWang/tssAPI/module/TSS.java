package com.houseWang.tssAPI.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
	private boolean isLogin = false;

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

	public String touchTSSLogin() {
		try {
			URL realUrl = new URL(
					"https://218.94.159.102/GlobalLogin/login.jsp?ReturnURL=http%3A%2F%2F218.94.159.102%2Ftss%2Fen%2Fhome%2FpostSignin.html");
			HttpsURLConnection conn = (HttpsURLConnection) realUrl
					.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.connect();
			String jumpCookie = conn.getHeaderField("Set-Cookie");
			return jumpCookie;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void login(String uname, String pw, int days) {
		this.userName = uname;
		this.password = new String(pw);

		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		String jumpCookie = touchTSSLogin();
		try {
			URL realUrl = new URL(ConstURL.LOGIN_REQUEST);
			// �򿪺�URL֮�������
			HttpsURLConnection conn = (HttpsURLConnection) realUrl
					.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			System.out.println("jump: " + jumpCookie);
			conn.setRequestProperty("Cookie", jumpCookie);
			conn.setRequestProperty(
					"Referer",
					"https://218.94.159.102/GlobalLogin/login.jsp?ReturnURL=http%3A%2F%2F218.94.159.102%2Ftss%2Fen%2Fhome%2FpostSignin.html");
			// ����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			// ��ȡURLConnection�����Ӧ�������
			out = new PrintWriter(conn.getOutputStream());
			// �����������
			out.print("username=" + userName + "&password=" + password
					+ "&days=" + days + "&Submit=Login");
			// flush������Ļ���
			out.flush();
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
			System.out.println(result);
			if (result.contains("Login Failed")) {
				System.out.println("��¼ʧ��");
			} else {
				isLogin = true;
				System.out.println("��¼�ɹ�");
				String location = conn.getHeaderField("Location");
				System.out.println(location);
				System.out.println("��ȡ��½��Կ...");
				URL seUrl = new URL(location);
				// �򿪺�URL֮�������
				HttpURLConnection seconn = (HttpURLConnection) seUrl
						.openConnection();
				// ����ͨ�õ���������
				seconn.setRequestProperty("accept", "*/*");
				seconn.setRequestProperty("connection", "Keep-Alive");
				seconn.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				seconn.connect();
				cookie = seconn.getHeaderField("Set-Cookie");
				System.out.println(cookie);
			}
		} catch (Exception e) {
			System.out.println("POST ERROR: " + e);
		}
		// ʹ��finally�����ر��������������
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void getMyCourceList() {
		if (!isLogin) {
			System.out.println("��û�е�¼");
			return;
		}
		try {
			URL realUrl = new URL(
					"http://218.94.159.102/tss/en/c0738/slide/index.html");
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setRequestProperty("Cookie", cookie);
			conn.connect();
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String ss = null;
			String total = "";
			while ((ss = bfr.readLine()) != null) {
				total += ss + "\r\n";
			}
			System.out.println(total);
			bfr.close();
			BufferedWriter bfw = new BufferedWriter(new FileWriter("d:/1.html"));
			bfw.write(total);
			bfw.flush();
			bfw.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getUserName() {
		return userName;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public static void main(String[] args) {
		String name = "";
		String password = "";
		TSS tss = TSS.getInstance();
		tss.login(name, password, 1);
		tss.getMyCourceList();
	}
}
