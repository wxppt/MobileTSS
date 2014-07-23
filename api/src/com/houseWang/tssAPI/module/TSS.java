package com.houseWang.tssAPI.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.houseWang.tssAPI.constant.URLConst;
import com.houseWang.tssAPI.exception.NotLoginException;
import com.houseWang.tssAPI.helper.Filter;
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
	public void login(String userName, char[] password) {
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

			// 登录
			PostConnection requestConn = new PostConnection(
					URLConst.LOGIN_REQUEST);
			requestConn.setRequestProperty("cookie", jumpCookie);
			requestConn.putFormData("username", userName);
			requestConn.putFormData("username", userName);
			requestConn.putFormData("password", new String(password));
			requestConn.putFormData("days", 30);
			requestConn.putFormData("Submit", "Login");
			String result = requestConn.getSourceCode();
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
				} else {
					System.out.println("Login Failed");
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 得到所有的课程列表
	 * 
	 * @return 课程列表
	 * @throws NotLoginException
	 *             没有登录
	 */
	public ArrayList<Course> getTotalCourseList() throws NotLoginException {
		if (!isLogin) {
			throw new NotLoginException();
		}
		try {
			GetConnection conn = new GetConnection(URLConst.ALL_COURSE);
			if (cookie != null) {
				conn.setRequestProperty("Cookie", cookie);
			}
			String source = conn.getSourceCode();
			ArrayList<Course> list = Filter.filterAllCourseList(source);
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到我的课程列表
	 * 
	 * @return 我的课程列表
	 * @throws NotLoginException
	 *             没有登录
	 */
	public ArrayList<Course> getMyCourseList() throws NotLoginException {
		if (!isLogin) {
			throw new NotLoginException();
		}
		try {
			GetConnection conn = new GetConnection(URLConst.MY_COURSE);
			if (cookie != null) {
				conn.setRequestProperty("Cookie", cookie);
			}
			String source = conn.getSourceCode();
			ArrayList<Course> cList = this.getTotalCourseList();
			ArrayList<Course> myCList = Filter
					.filterMyCourseList(source, cList);
			return myCList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 得到一门课的课程信息
	 * 
	 * @param couId
	 *            课程号
	 * @return 课程
	 * @throws NotLoginException
	 *             没有登录
	 */
	public Course getCourseInfo(String couId) throws NotLoginException {
		if (!isLogin) {
			throw new NotLoginException();
		}
		return new Course(couId);
	}

	/**
	 * 得到一门课的公告列表
	 * 
	 * @param couId
	 *            课程号
	 * @return 公告列表
	 * @throws NotLoginException
	 *             没有登录
	 */
	public ArrayList<Announce_record> getAnnoucementList(String couId)
			throws NotLoginException {
		if (!isLogin) {
			throw new NotLoginException();
		}
		return (ArrayList<Announce_record>) new Announcements(couId).getAnnouncements();
	}

	/**
	 * 得到一门课的某路径下的课件列表
	 * 
	 * @param couId
	 *            课程号
	 * @param path
	 *            路径，如果是根目录则为/
	 * @return 课件列表
	 * @throws NotLoginException
	 *             没有登录
	 */
	public ArrayList<Courseware> getCoursewareList(String couId, String path)
			throws NotLoginException {
		if (!isLogin) {
			throw new NotLoginException();
		}
		try {
			GetConnection conn = new GetConnection(URLConst.COURSEWARE_LIST(
					couId, path));
			if (cookie != null) {
				conn.setRequestProperty("Cookie", cookie);
			}
			String source = conn.getSourceCode();
			System.out.println(source);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到一门课的课程列表
	 * 
	 * @param couId
	 *            课程号
	 * @return 课程列表
	 * @throws NotLoginException
	 *             没有登录
	 */
	public ArrayList<Assignment> getAssignmentList(String couId)
			throws NotLoginException {
		if (!isLogin) {
			throw new NotLoginException();
		}
		try {
			GetConnection conn = new GetConnection(
					"http://218.94.159.102/tss/en/" + couId
							+ "/assignment/index.html");
			if (cookie != null) {
				conn.setRequestProperty("Cookie", cookie);
			}
			String source = conn.getSourceCode();
			ArrayList<Assignment> myCList = Filter.filterAssignmentList(source);
			return myCList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws NotLoginException {
		String name = "";
		String password = "";
		TSS tss = TSS.getInstance();
		tss.login(name, password.toCharArray());
		ArrayList<Assignment> aList = tss.getAssignmentList("c0886");
		tss.getCoursewareList("c0867", "/安装双系统资料/VMware虚拟机安装Ubuntu/");
	}
}
