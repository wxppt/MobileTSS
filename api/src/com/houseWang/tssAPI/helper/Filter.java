package com.houseWang.tssAPI.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.houseWang.tssAPI.constant.URLConst;
import com.houseWang.tssAPI.module.Course;
import com.houseWang.tssAPI.module.Courseware;
import com.houseWang.tssAPI.module.User;

public class Filter {

	/**
	 * 清洗所有课程列表的网页的源代码，得到课程列表
	 * 
	 * @param source
	 *            网页源代码
	 * @return 课程列表
	 * @throws IOException
	 */
	public static ArrayList<Course> filterAllCourseList(String source)
			throws IOException {
		ArrayList<Course> cList = new ArrayList<Course>();
		// 读取Dom，去除源代码中所有文本空格
		Document dom = Jsoup.parse(source.replace("&nbsp;", ""));
		Elements tables = dom.getElementsByTag("tbody");
		Element courseTable = tables.get(11);
		Elements rows = courseTable.getElementsByTag("tr").get(1)
				.getElementsByTag("tbody").get(0).getElementsByTag("tr");
		String term = "";
		for (Element row : rows) {
			String text = row.text();
			// 忽略无用的行，读取学期行，读取数据行
			if (text.contains("CourseNo") || text.equals("")) {
				continue;
			} else if (row.toString().contains("tdtitle")) {
				term = row.text().trim();
			} else {
				Elements tds = row.getElementsByTag("td");
				Course c = new Course();
				c.setTerm(term);
				c.setCid(tds.get(0).text().trim());
				c.setCname(tds.get(1).text().trim());
				String[] teachers = tds.get(2).text().trim().split(" ");
				ArrayList<User> tList = new ArrayList<User>();
				for (int i = 0; i < teachers.length; i++) {
					User u = new User();
					u.setUname(teachers[i]);
					tList.add(u);
				}
				c.setTeachers(tList);
				c.setModifyTime(tds.get(3).text());
				cList.add(c);
			}
		}
		return cList;
	}

	/**
	 * 清洗我的课程列表的网页的源代码，得到我的课程号，根据课程号匹配课程
	 * 
	 * @param source
	 *            网页源码
	 * @param cList
	 *            所有课程列表
	 * @return myCList 我的课程列表
	 */
	public static ArrayList<Course> filterMyCourseList(String source,
			ArrayList<Course> cList) throws IOException {
		ArrayList<Course> myCList = new ArrayList<Course>();
		// 读取Dom，去除源代码中所有文本空格
		Document dom = Jsoup.parse(source.replace("&nbsp;", ""));
		String newSource = dom.text().toString();
		Pattern pattern = Pattern.compile("c[0-9]{4}");
		Matcher matcher = pattern.matcher(newSource);
		while (matcher.find()) {
			String cid = matcher.group();
			System.out.println(cid);
			for (Course course : cList) {
				if (course.getCid().equals(cid)) {
					myCList.add(course);
				}
			}
		}
		return myCList;
	}

	/**
	 * 清洗课件列表源代码，返回课件列表
	 * 
	 * @param source
	 *            页面源代码
	 * @return 该页面的课件列表
	 */
	public static ArrayList<Courseware> filterCoursewareList(String source) {
		ArrayList<Courseware> list = new ArrayList<Courseware>();
		Document dom = Jsoup.parse(source.replace("&nbsp;", ""));
		Elements uls = dom.getElementsByTag("ul");
		String target = uls.get(0).html();
		String[] tarsp = target.split("<br />");
		for (int i = 1; i < tarsp.length; i++) {
			if (tarsp[i].length() > 20) {
				Courseware c = new Courseware();
				Pattern urlp = Pattern.compile("href=\".+?\"");
				Matcher urlm = urlp.matcher(tarsp[i]);
				if (urlm.find()) {
					String path = urlm.group().replace("href=", "")
							.replace("\"", "");
					c.setUrl(URLConst.ROOT + path);
				}
				if (tarsp[i]
						.contains("<img src=\"/tss/pic/dir.gif\" border=\"0\" alt=\"Dir\" />")) {
					c.setType(Courseware.FOLDER);
				} else {
					c.setType(Courseware.FILE);
					Pattern sizep = Pattern.compile("<i>.+?</i>");
					Matcher sizem = sizep.matcher(tarsp[i]);
					if (sizem.find()) {
						String sizeStr = sizem.group();
						c.setSize(Integer.parseInt(sizeStr.toLowerCase()
								.replaceAll("<.+?>", "")
								.replaceAll("[a-z]*", "")));
					}
				}
				String[] nameAndTime = tarsp[i].replaceAll("<.+?>", "").split(
						",");
				c.setName(nameAndTime[0].trim());
				Pattern timep = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
				Matcher timem = timep.matcher(tarsp[i]);
				if (timem.find()) {
					c.setTime(timem.group());
				}
				list.add(c);
			}
		}
		return list;
	}

}
