package com.houseWang.tssAPI.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Announcements {
	private List<Announce_record> ar_list = new ArrayList<Announce_record>();
	
	public List<Announce_record> getAnnouncements(){
		return ar_list;
	}

	public Announcements(String courseNo) {
		String url = "http://218.94.159.102/tss/en/" + courseNo
				+ "/announcement/index.html";
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
			Elements ts = doc.getElementsByClass("forum_text");
			for (Element te : ts) {
				Elements tds = te.getElementsByTag("td");
				String timeStr = tds.get(0).text().trim();
				String title = tds.get(1).text().trim();
				String content = tds.get(2).text().trim();
				ar_list.add(new Announce_record(timeStr, title, content));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showRecords() {
		for (Announce_record ar : ar_list) {
			System.out.println(ar);
		}
	}

	// http://218.94.159.102/tss/en/c0738/announcement/index.html
	public static void main(String[] args) {
		new Announcements("c0738").showRecords();
	}
}
