package com.houseWang.tssAPI.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Course {
	private String cid="";
	private String cname="";
	private String term="";
	private String modifyTime="";
	private ArrayList<User> teachers = new ArrayList<User>();
	private ArrayList<User> assistents = new ArrayList<User>();

	private String getAccount(String href) {
		String[] a = href.split("=");
		return a[1];
	}

	public Course(String courseId) {
		cid = courseId;
		init_info();
	}

	private void init_info() {
		String info_url = "http://218.94.159.102/tss/en/" + cid + "/index.html";
		Document doc = null;
		try {
			doc = Jsoup.connect(info_url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get cname, semester
		Elements bt = doc.getElementsByTag("i");
		Element fa = bt.first().parent();
		String[] words = fa.text().split(" ");
		List<String> wordList = new ArrayList<String>();
		for (String word : words) {
			if (!word.contains("    ") && !word.contains(" "))
				wordList.add(word);
		}
		int index[] = new int[5];
		for (int i = 0; i < wordList.size(); i++) {
			String word = wordList.get(i);
			if (word.equals("Name:"))
				index[0] = i;
			if (word.equals("Semester:"))
				index[1] = i;
			if (word.equals("Instructor:"))
				index[2] = i;
			// for check:
			if (word.equals("Assistants:"))
				index[3] = i;
			if (word.equals("Description"))
				index[4] = i;
			// System.out.println( "/"+word+"/" );
		}
		for (int i = index[0] + 1; i < index[1]; i++) {
			cname += wordList.get(i) + " ";
		}
		// semester
		for (int i = index[1] + 1; i < index[2]; i++) {
			term += wordList.get(i) + " ";
		}
		// for check:
		int t_num = (index[3] - 1) - (index[2] + 1);
		int a_num = index[4] - (index[3] + 1);

		Elements as = fa.getElementsByTag("a");
		int cont = t_num;
		for (Element e : as) {
			String href = e.attr("href");
			String account = getAccount(href);
			String name = e.text();
			// System.out.println(name+"  "+account);
			if (cont > 0) {
				teachers.add(new User(account, name));
				cont--;
			} else {
				assistents.add(new User(account, name));
			}
		}
		// if wrong :will output the info
		if (t_num != teachers.size() || a_num != assistents.size()) {
			System.out
					.println("!!!!!!!!!!!!!!!!!!!!FBI WARNING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(t_num);
			System.out.println(teachers.size());
			System.out.println(a_num);
			System.out.println(assistents.size());
		}
	}

	public String getCid() {
		return cid;
	}

	public String getCname() {
		return cname;
	}

	public ArrayList<User> getTeachers() {
		return teachers;
	}

	public ArrayList<User> getAssistents() {
		return assistents;
	}

	public String getTerm() {
		return term;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	@Override
	public String toString() {
		String ts = "";
		for (User u : teachers) {
			ts += u.getUname() + " ";
		}
		String as = "";
		for (User u : assistents) {
			as += u.getUname() + " ";
		}
		return "Course [cid=" + cid + ", cname=" + cname + ", term=" + term
				+ ", modifyTime=" + modifyTime + ", teachers=" + ts
				+ ", assistents=" + as + "]";
	}

	public Course() {
	}

	public void setTerm(String term2) {
		// TODO Auto-generated method stub
		term = term2;
	}

	public void setCid(String cid2) {
		cid = cid2;
	}

	public void setCname(String cname2) {
		cname = cname2;
	}

	public void setTeachers(ArrayList<User> ts) {
		teachers = ts;
	}

	public void setModifyTime(String m2) {
		modifyTime = m2;
	}
	
	public static void main(String[] args) {
		Course c=new Course("c0738");
		System.out.println(c.cid);
		System.out.println(c.cname);
		System.out.println(c.term);
		System.out.println(c.teachers);
		System.out.println(c.assistents);
	}
}
