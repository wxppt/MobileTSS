package com.houseWang.tssAPI.helper;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.houseWang.tssAPI.module.Course;
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
}
