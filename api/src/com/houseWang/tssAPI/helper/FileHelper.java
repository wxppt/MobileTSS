package com.houseWang.tssAPI.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileHelper {
	public static void writeFile(String path, String data, String charset)
			throws IOException {
		BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path), charset));
		bfw.write(data);
		bfw.flush();
		bfw.close();
	}

	public static String readFile(String path, String charset)
			throws IOException {
		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				new FileInputStream(path)));
		String total = "";
		String newline = null;
		while ((newline = bfr.readLine()) != null) {
			total += newline + "\r\n";
		}
		bfr.close();
		return total;
	}
}
