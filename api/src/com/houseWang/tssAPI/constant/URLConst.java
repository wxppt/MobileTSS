package com.houseWang.tssAPI.constant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLConst {
	public static final String ROOT = "http://218.94.159.102";
	// 登录用
	public static final String LOGIN_PAGE = "https://218.94.159.102/GlobalLogin/login.jsp?ReturnURL=http%3A%2F%2F218.94.159.102%2Ftss%2Fen%2Fhome%2FpostSignin.html";
	public static final String LOGIN_REQUEST = "https://218.94.159.102/GlobalLogin/loginservlet";
	// 课程列表用
	public static final String ALL_COURSE = "http://218.94.159.102/tss/en/home/courselist.html";
	public static final String MY_COURSE = "http://218.94.159.102/tss/en/home/mycourse.html";
	// 课件列表用
	private static String courseware_path = "http://218.94.159.102/tss/en/couId/slide/viewSlides";

	public static String COURSEWARE_LIST(String couId, String path) {
		String url = courseware_path.replace("couId", couId);
		String[] pathsp = path.split("/");
		for (int i = 0; i < pathsp.length; i++) {
			try {
				url += URLEncoder.encode(pathsp[i], "gbk") + "/";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return url;
	}

}
