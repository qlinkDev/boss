package com.qlink.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CsvWriter {
	
	public static void main(String args[]) {
		/*CsvWriter cw = new CsvWriter();
		String csvFile = "G:/workspace/JavaApp/csv/createCSV.csv";
		//cw.createCSV(csvFile);

		String localPath = "G:/workspace/JavaApp/csv/";
		String fName = "createCsvByList.csv";
		List listSource = new ArrayList();
		listSource.add("1,上将,male,tomcat@tomcat.com,1383838438");
		listSource.add("2,Jboss,male,jboss@jboss.com,1484848748");

		cw.createCSVFile(listSource, localPath, fName);*/
	}

	/**
	 * 创建csv文件
	 * @param data 行数据
	 * @param fPath 目录路径
	 * @param fName 文件名
	 * 
	 */
	public static void createCSVFile(List<String> data, String fPath, String fName) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileOutputStream fos = null;
		try {

			@SuppressWarnings("rawtypes")
			Iterator it = data.iterator();
			while (it.hasNext()) {
				String value = (String) it.next();
				out.write(value.getBytes());
				out.write(",".getBytes()); // 以逗号为分隔符
				out.write("\n".getBytes()); // 换行
			}
			
			// 没有目录，先生成目录
			FileUtils.createDirectory(fPath);

			File newfile = new File(fPath, fName);

			fos = new FileOutputStream(newfile);
			fos.write(out.toByteArray());

			fos.flush();
			out.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != out)
					out.close();
				if (null != fos)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("生成" + fPath + File.separator + fName + "完成");
	}
}
