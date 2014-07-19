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

import com.houseWang.tssAPI.constant.URLConst;

public class TSS {
	/**
	 * TSS单例
	 */
	private static TSS instance = null;

	/**
	 * 用户名
	 */
	private String userName = null;
	/**
	 * 密码
	 */
	/**
	 * 记录用户登录的cookie
	 */
	private String cookie = null;
	/**
	 * 是否登陆
	 */
	private boolean isLogin = false;

	/**
	 * 构造方法，初始化了不经验证的HTTPS连接
	 */
	private TSS() {
		initTrustSSL();
	}

	/**
	 * 得到TSS单例
	 * 
	 * @return TSS单例
	 */
	public static TSS getInstance() {
		if (instance == null) {
			instance = new TSS();
		}
		return instance;
	}

	/**
	 * 得到当前的用户名
	 * 
	 * @return 用户名
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 是否登录
	 * 
	 * @return 是/否登录
	 */
	public boolean isLogin() {
		return isLogin;
	}

	/**
	 * 在连接HTTPS连接时不经过SSL证书验证
	 * 
	 * @see http://blog.csdn.net/dengbodb/article/details/8281763
	 */
	private void initTrustSSL() {
		try {
			SSLContext sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null, new TrustManager[] { new X509TrustManager() {
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

	/**
	 * 登录，若成功，将会把isLogin置为true，否则保持false； 在已经登录的情况下不允许重复登录
	 * 
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param days
	 *            cookies有效期
	 */
	public void login(String userName, char[] password, int days) {
		this.userName = userName;

		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		String jumpCookie = getLoginRequestCookie();

		try {
			URL realUrl = new URL(URLConst.LOGIN_REQUEST);
			HttpsURLConnection conn = (HttpsURLConnection) realUrl
					.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Cookie", jumpCookie);
			conn.setRequestProperty("Referer", URLConst.LOGIN_PAGE);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			out = new PrintWriter(conn.getOutputStream());
			out.print("username=" + userName + "&password="
					+ new String(password) + "&days=" + days + "&Submit=Login");
			out.flush();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
			if (result.contains("Login Failed")) {
				System.out.println("Login Failed");
			} else {
				isLogin = true;
				String location = conn.getHeaderField("Location");
				URL seUrl = new URL(location);
				HttpURLConnection seconn = (HttpURLConnection) seUrl
						.openConnection();
				seconn.setRequestProperty("accept", "*/*");
				seconn.setRequestProperty("connection", "Keep-Alive");
				seconn.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				seconn.connect();
				cookie = seconn.getHeaderField("Set-Cookie");
				System.out.println("Login Succeed");
			}
		} catch (Exception e) {
			System.out.println("POST ERROR: " + e);
		} finally {
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

	/**
	 * 得到发送登录请求的cookie，用于跳转到TSS
	 * 
	 * @return 跳转用的cookie
	 */
	private String getLoginRequestCookie() {
		try {
			URL realUrl = new URL(URLConst.LOGIN_PAGE);
			HttpsURLConnection conn = (HttpsURLConnection) realUrl
					.openConnection();
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

	public void getMyCourceList() {
		if (!isLogin) {
			System.out.println("NOT LOGIN YET");
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
			bfr.close();
			BufferedWriter bfw = new BufferedWriter(new FileWriter("d:/1.html"));
			bfw.write(total);
			bfw.flush();
			bfw.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String name = "wx12";
		String password = "popkart888";
		TSS tss = TSS.getInstance();
		tss.login(name, password.toCharArray(), 1);
		tss.getMyCourceList();
	}
}
