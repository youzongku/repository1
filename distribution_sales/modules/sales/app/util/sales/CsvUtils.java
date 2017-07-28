package util.sales;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import liquibase.util.csv.opencsv.CSVReader;
import play.Logger;

public class CsvUtils {
	/**
	 * 判断是否是csv文件（文件名以.csv结尾）
	 * @param filename
	 * @return
	 */
	public static boolean isCsvFile(String filename){
		if(StringUtils.isBlankOrNull(filename)){
			return false;
		}
		
		return filename.endsWith(".csv");
	}

	/**
	 * 读取csv文件的表头用于判断上传文件名
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static List<String> readCsvHeader(File file) {
		List<String> csvHeader = new ArrayList<String>();
		CSVReader csvReader = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		String[] readNext = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "GBK");
			csvReader = new CSVReader(isr);
			readNext = csvReader.readNext();
		} catch (FileNotFoundException e) {
			Logger.error(e.toString());
		} catch (UnsupportedEncodingException e) {
			Logger.error(e.toString());
		} catch (IOException e) {
			Logger.error(e.toString());
		} finally {
			try {
				if (fis!=null) {
					fis.close();
					isr.close();
				}
			} catch (IOException e) {
				Logger.error(e.toString());
			}
		}
		if (readNext == null || readNext.length < 1) {
			return csvHeader;
		}
		Logger.info(">>>>>当前上传文件表头" + Arrays.asList(readNext));
		for (String title : readNext) {
			if(title == null){
				continue;
			}
			csvHeader.add(title.trim());
		}
		Logger.info(">>>>>当前去除空格后表头" + Arrays.asList(csvHeader));
		return csvHeader;
	}

	
	/**
	 * //查找是否有匹配的实体
	 * @param file
	 * @return
	 */
	public static String confirmFile(File file) {
		List<String> readCsvHeader = CsvUtils.readCsvHeader(file);
		Map<String, String> goodTitle = TitleUtils.tbOrderGoodsTitleToEntity();
		Map<String, String> orderTitle = TitleUtils.tbOrderTitleToEntity();
		Set<String> goodSet = goodTitle.keySet();
		Set<String> orderSet = orderTitle.keySet();
		boolean isContain1 = true;
		boolean isContain2 = true;
		String filename = null;
		for (String string : orderSet) {
			if (!readCsvHeader.contains(string)) {
				Logger.info("淘宝订单表不包含的表头字段》》》》" + string);
				isContain1 = false;
				break;
			}
		}
		for (String string : goodSet) {
			if (!readCsvHeader.contains(string)) {
				Logger.info("淘宝商品表不包含的表头字段》》》》" + string);
				isContain2 = false;
				break;
			}
		}
		if (isContain1) {// 淘宝订单信息模板
			filename = "1";
		}
		if (isContain2) {// 淘宝商品信息模板
			filename = "2";
		}
		return filename;
	}

	/**
	 * 读取csv文件的表头和实体表字段对应关系
	 * 
	 * @param file,flag
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, String> readCsvHeader(File file, String flag) {
		Map<Integer, String> csvHeader = Maps.newHashMap();
		CSVReader csvReader = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		String[] readNext = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "GBK");
			csvReader = new CSVReader(isr);
			readNext = csvReader.readNext();
		} catch (FileNotFoundException e) {
			Logger.error(e.toString());
		} catch (UnsupportedEncodingException e) {
			Logger.error(e.toString());
		} catch (IOException e) {
			Logger.error(e.toString());
		} finally {
			try {
				if (fis!=null) {
					fis.close();
					isr.close();
				}
			} catch (IOException e) {
				Logger.error(e.toString());
			}
		}
		if (readNext == null || readNext.length < 1) {
			return csvHeader;
		}
		for (int i = 0; i < readNext.length; i++) {
			String temp = readNext[i];
			if(temp == null){
				continue;
			}
			if (flag.equals("1")) {// 淘宝订单map
				if (TitleUtils.tbOrderTitleToEntity().containsKey(temp.trim())) {
					csvHeader.put(i, TitleUtils.tbOrderTitleToEntity().get(temp.trim()));
				}
			} 
			if (flag.equals("2")) {// 淘宝商品map
				if (TitleUtils.tbOrderGoodsTitleToEntity().containsKey(temp.trim())) {
					csvHeader.put(i, TitleUtils.tbOrderGoodsTitleToEntity().get(temp.trim()));
				}
			} 
		}
		return csvHeader;
	}

}
