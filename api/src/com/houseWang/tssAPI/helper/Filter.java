package com.houseWang.tssAPI.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.houseWang.tssAPI.module.Assignment;
import com.houseWang.tssAPI.module.Assignment.Status;
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
	 * 清洗作业提交的网页源码，得到课程作业信息的列表
	 * 
	 * @param source
	 *            网页源码
	 * @return aList 作业信息列表
	 */
	public static ArrayList<Assignment> filterAssignmentList(String source)
			throws IOException {
		ArrayList<Assignment> aList = new ArrayList<Assignment>();
		// 读取Dom，去除源代码中所有文本空格
		Document dom = Jsoup.parse(source.replace("&nbsp;", ""));
		Element table = dom.getElementsByTag("tbody").get(13);
		Elements allAssignments = table.getElementsByTag("tbody");
		for (Element e : allAssignments) {
			Assignment assignment = new Assignment();
			Elements rows = e.getElementsByTag("tr");
			// 提取ID
			String id = rows.get(0).getElementsByTag("td").get(1).text().trim();
			assignment.setId(id);
			// 提取deadline
			String ddl = rows.get(1).getElementsByTag("td").get(1).text()
					.trim();
			assignment.setDeadline(ddl);
			// 提取文件
			String fileName = rows.get(2).getElementsByTag("td").get(1).text()
					.trim();
			Courseware file = new Courseware();
			file.setName(fileName);
			// 如果文件名不是none，则提取超链接
			if (!fileName.equals("none")) {
				Elements links = rows.get(2).getElementsByTag("td").get(1)
						.select("a");
				file.setUrl(links.attr("href"));
			}
			assignment.setFile(file);
			// 提取描述
			String content = rows.get(3).getElementsByTag("td").get(1).text()
					.trim();
			assignment.setContent(content);
			// 提取状态
			String status = rows.get(4).getElementsByTag("td").get(0).text()
					.trim();
			if (status.equals("")) {
				assignment.setStatus(Status.PERMISSION_DENIED);
			} else if(status.equals("Deadline is over")){
				assignment.setStatus(Status.DDL_OVER);
			}
			aList.add(assignment);
		}

		return aList;
	}
}
