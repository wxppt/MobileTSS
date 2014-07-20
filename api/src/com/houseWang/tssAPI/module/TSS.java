package com.houseWang.tssAPI.module;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.houseWang.tssAPI.constant.ConfConst;
import com.houseWang.tssAPI.constant.URLConst;
import com.houseWang.tssAPI.helper.FileHelper;
import com.houseWang.tssAPI.helper.HttpsHelper;
import com.houseWang.tssAPI.net.GetConnection;
import com.houseWang.tssAPI.net.PostConnection;

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
		HttpsHelper.initTrustSSL();
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
		String jumpCookie = "";
		try {
			// 取得tss登录的cookie
			GetConnection jumpConn = new GetConnection(URLConst.LOGIN_PAGE);
			List<String> jumpSetCookies = jumpConn.getResponseHeaderFields()
					.get("Set-Cookie");
			if (!jumpSetCookies.isEmpty()) {
				jumpCookie = jumpSetCookies.get(0);
			}
			System.out.println(jumpCookie);

			// 登录
			PostConnection requestConn = new PostConnection(
					URLConst.LOGIN_REQUEST);
			requestConn.setRequestProperty("cookie", jumpCookie);
			requestConn.putFormData("username", userName);
			requestConn.putFormData("username", userName);
			requestConn.putFormData("password", new String(password));
			requestConn.putFormData("days", 30);
			requestConn.putFormData("Submit", "Login");
			String result = requestConn
					.getSourceCode(ConfConst.DEFAULT_CHARSET);
			if (result.contains("Login Failed")) {
				System.out.println("Login Failed");
			} else {
				List<String> locations = requestConn.getResponseHeaderFields()
						.get("Location");
				String location = null;
				if (!locations.isEmpty()) {
					location = locations.get(0);
					// 取得登录认证的cookie
					GetConnection cookieConn = new GetConnection(location);
					List<String> loginSetCookies = cookieConn
							.getResponseHeaderFields().get("Set-Cookie");
					if (!loginSetCookies.isEmpty()) {
						cookie = loginSetCookies.get(0);
						isLogin = true;
						System.out.println("Login Succeed");
					}
					System.out.println(cookie);
				} else {
					System.out.println("Login Failed");
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void test() {
		if (!isLogin) {
			System.out.println("NOT LOGIN YET");
			return;
		}
		try {
			GetConnection conn = new GetConnection(
					"http://218.94.159.102/tss/en/c0738/slide/index.html");
			conn.setRequestProperty("Cookie", cookie);
			String total = conn.getSourceCode(ConfConst.DEFAULT_CHARSET);
			FileHelper.writeFile("d:/1.html", total, ConfConst.DEFAULT_CHARSET);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String name = "";
		String password = "";
		TSS tss = TSS.getInstance();
		tss.login(name, password.toCharArray(), 1);
		tss.test();
	}
}
