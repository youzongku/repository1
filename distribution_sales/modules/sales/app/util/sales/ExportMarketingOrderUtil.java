package util.sales;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dto.marketing.MarketingOrderDto;
import dto.sales.ExportMarketOrderInfo;
import entity.marketing.MarketingOrderDetail;
import play.Logger;
/**
 * 导出营销单工具类
 * @author huangjc
 * @date 2017年2月21日
 */
public class ExportMarketingOrderUtil {

	/**
	 * 服务器临时目录
	 */
	private static String filePath = "/tmp/";
	
	private ExportMarketingOrderUtil(){
		
	}

	public static ExportMarketingOrderUtil  getInstance(){
		return new ExportMarketingOrderUtil();
	}
	
	/**
	 * @param fileName
	 *            文件名
	 * @param header
	 *            excel列头
	 * @param headerMapping
	 *            列头与显示的中文映射
	 * @param data
	 *            数据
	 * @return
	 */
	public static File export(String fileName, List<ExportMarketOrderInfo> moList) {
		Logger.info("导出时间start：" + new Date());
		XSSFWorkbook workBook = new XSSFWorkbook();// 创建一个工作簿
		XSSFSheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		Map<String, CellStyle> cellStyleMap = getCellStyleMap(workBook);
		
		// 序列，日期，        申请人，                                 BBC单号，       样品SKU，样品名称，数量，进价，成本进价小计，合计数量，备注    共11列
		
		// 序列，下单日期，录入人，订单编号（YX），发货单号（XS），SKU，中文品名，数量，到仓价，到仓价小计，SKU数量小计，备注（业务备注的内容）
		
		
//		CellRangeAddress  对象的构造方法需要传入合并单元格的首行、最后一行、首列、最后一列。
		CellRangeAddress firstTitleCellRangeAddress = new CellRangeAddress(0, 1, 0, 11);
		sheet.addMergedRegion(firstTitleCellRangeAddress);
		Row headRow = sheet.createRow(0);
		Cell headCell = headRow.createCell(0);
		headCell.setCellType(XSSFCell.CELL_TYPE_STRING);
		// 设置样式
		headCell.setCellStyle(cellStyleMap.get("cellStyle"));
		headCell.setCellValue("免费样品登记表");
		String[] headers = {"seq","createDateStr","createUser","marketingOrderNo",
		                    "salesOrderNo","sku","productName","qty","arriveWarePrice",
		                    "arriveTotal","totalQty","businessRemark"};
		int[] columnWidths ={1856,2784,2752,4256,5856,3040,9984,1792,1792,4832,4192,9312};
		Row titleRow = sheet.createRow(2);
		sheet.createFreezePane(0, 3);  
		CellStyle titleCellstyle = cellStyleMap.get("titleCellstyle");
		for (int j = 0; j < headers.length; j++) {
			Cell titleCell = titleRow.createCell(j);
			// 设置单元格格式
			sheet.setColumnWidth(j, columnWidths[j]);
			titleCell.setCellType(XSSFCell.CELL_TYPE_STRING);
			titleCell.setCellStyle(titleCellstyle);
			// 将数据填入单元格
			titleCell.setCellValue(Constant.EXPORT_MARKET_ORDER_MAP_FIN.get(headers[j]));
		}
		CellRangeAddress c = CellRangeAddress.valueOf("A3:L3");
		sheet.setAutoFilter(c);
		insertMarketingOrders(sheet, cellStyleMap.get("dataCellStyle"), moList,headers);
		// 生成文件
		return createExcel(fileName, workBook);
	}

	/**
	 * 插入具体营销单
	 * @param sheet
	 * @param cellStyleMap
	 * @param moDtoList
	 */
	private static void insertMarketingOrders(Sheet sheet, CellStyle dataCellStyle,
			List<ExportMarketOrderInfo> moDtoList,String[] headers){
		// 从第4行开始是具体数据了
		int rowNum = 3;
		LinkedHashMap<Integer,Integer> firstCol2LastCol = Maps.newLinkedHashMap();
		firstCol2LastCol.put(0, 0);
		firstCol2LastCol.put(1, 1);
		firstCol2LastCol.put(2, 2);
		firstCol2LastCol.put(3, 3);
		firstCol2LastCol.put(4,4);
		firstCol2LastCol.put(10,10);
		firstCol2LastCol.put(11,11);
		ExportMarketOrderInfo info = null;
		String orderNo = "";
		//相同订单计数
		int count = 1;
		Row row = null;
		Cell cell = null;
		Integer seq = 0;
		for (int i = 0; i < moDtoList.size(); i++) {
			info = moDtoList.get(i);
			//合并单元格计算
			if(orderNo.equals(info.getMarketingOrderNo())){
				//如果订单相同累加数量
				count++;
				if(i == moDtoList.size()-1 && count>1){
					setRowMergedRegions(sheet, rowNum-count+1, rowNum, firstCol2LastCol);
				}
			}else{
				seq++;
				orderNo = info.getMarketingOrderNo();
				if(count > 1){
					setRowMergedRegions(sheet, rowNum-count, rowNum-1, firstCol2LastCol);
				}
				count = 1;
			}
			row = sheet.createRow(rowNum);
				// 单元格
			for (int j = 0; j < headers.length; j++) {
				cell = row.createCell(j);
				cell.setCellStyle(dataCellStyle);
				if("seq".equals(headers[j])){
					cell.setCellValue(seq);
				}else{
					Object value = parseData(headers[j], info);
					if (null == value) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(value + "");
					}
				}
			}
			rowNum++;
		}
	}
	
	/**
	 * 只做合并行的，不做合并列
	 * @param sheet
	 * @param fromRow
	 * @param lastRow
	 */
	private static void setRowMergedRegions(Sheet sheet, int fromRow, int lastRow, LinkedHashMap<Integer,Integer> firstCol2LastCol){
		for(Map.Entry<Integer,Integer> entryFirstCol2LastCol : firstCol2LastCol.entrySet()){
			sheet.addMergedRegion(new CellRangeAddress(fromRow, lastRow,
					entryFirstCol2LastCol.getKey(), entryFirstCol2LastCol
							.getValue()));
		}
	}
	
	private static Object parseData(String field, Object object) {
		try {
			String firstLetter = field.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + field.substring(1);
			Method method = object.getClass().getMethod(getter);
			Object value = method.invoke(object);
			return value;
		} catch (Exception e) {
			Logger.error("字段不存在：" + field);
			return null;
		}
	}
	private Sum sum(List<MarketingOrderDto> list){
		List<MarketingOrderDetail> detailList = Lists.newArrayList();
		list.forEach(e->{
			if(e.getDetailList() != null){
				detailList.addAll(e.getDetailList());
			}
		});
		if(detailList==null || detailList.size()==0){
			return new Sum(0, 0.00);
		}
		int totalQty = 0;
		BigDecimal sumPrice = BigDecimal.ZERO;
		for(MarketingOrderDetail detail : detailList){
			totalQty += detail.getQty();
			sumPrice = new BigDecimal(detail.getQty()).multiply(new BigDecimal(detail.getDisPrice())).add(sumPrice);
		}
		return new Sum(totalQty,sumPrice.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	
	/**
	 * 样式，最上边的标题是cellStyle，表头的是titleCellstyle，具体数据的是dataCellStyle
	 * @param workBook
	 * @return
	 */
	private static Map<String,CellStyle> getCellStyleMap(Workbook workBook){
		Map<String,CellStyle> map = Maps.newHashMap();
		
		CellStyle cellStyle = workBook.createCellStyle();
		Font font = workBook.createFont();
		font.setColor(HSSFColor.RED.index);
		cellStyle.setFont(font);
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 居中  
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//垂直
		map.put("cellStyle", cellStyle);
		
		// 表头样式
		CellStyle titleCellstyle = workBook.createCellStyle();
		Font titleCellFont = workBook.createFont();
		titleCellFont.setFontName("Arial");
		titleCellFont.setFontHeightInPoints((short)10);
		titleCellstyle.setFont(titleCellFont);
		titleCellstyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 居中  
		titleCellstyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//垂直 
		titleCellstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框    
		titleCellstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框    
		titleCellstyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框    
		titleCellstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
		map.put("titleCellstyle", titleCellstyle);
		
		// 具体数据样式
		CellStyle dataCellStyle = workBook.createCellStyle();
		Font dataCellFont = workBook.createFont();
		dataCellFont.setFontName("Arial");
		dataCellFont.setFontHeightInPoints((short)10);
		dataCellStyle.setFont(dataCellFont);
		dataCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 居中  
		dataCellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//垂直 
		dataCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框    
		dataCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框    
		dataCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框    
		dataCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
		map.put("dataCellStyle", dataCellStyle);
		return map;
	}
	
	private static Map<String,XSSFCellStyle> getSaleManCellStyleMap(XSSFWorkbook workBook){
		Map<String,XSSFCellStyle> map = Maps.newHashMap();
		
		// 表头样式
		XSSFCellStyle titleCellstyle = workBook.createCellStyle();
		XSSFFont titleCellFont = workBook.createFont();
		titleCellFont.setFontHeightInPoints((short)11);
		titleCellFont.setFontName("宋体");
		titleCellstyle.setFont(titleCellFont);
		titleCellstyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		titleCellstyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		titleCellstyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		titleCellstyle.setAlignment(XSSFCellStyle.ALIGN_LEFT); // 居中  
		map.put("titleCellstyle", titleCellstyle);
		
		// 具体数据样式
		XSSFCellStyle dataCellStyle = workBook.createCellStyle();
		XSSFFont dataCellFont = workBook.createFont();
		dataCellStyle.setFont(dataCellFont);
		dataCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); // 居中  
		dataCellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//垂直 
		dataCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框    
		dataCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框    
		dataCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框    
		dataCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
		map.put("dataCellStyle", dataCellStyle);
		return map;
	}
	
	/**
	 * 
	 * @param fileName
	 * @param workBook
	 * @return
	 */
	private static File createExcel(String fileName, Workbook workBook) {
		FileOutputStream fos = null;
		File file = null;
		try {
			File path = new File(filePath);
			if (!path.isDirectory() && !path.exists()) {
				path.mkdir();
			}
			String name = filePath + "Export-" + new Date().getTime() + "-" + fileName;
			Logger.info("execl导出临时路径:"+name);
			file = new File(name);
			file.createNewFile();
			fos = new FileOutputStream(file);
			workBook.write(fos);
			Logger.info("-----生成Excel成功-----");
		} catch (Exception e) {
			Logger.error(e + "");
			return null;
		} finally {
			try {
				if (fos!=null) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				Logger.error(e + "");
				return null;
			}
		}
		Logger.info("导出时间end：" + new Date());
		return file;
	}
	
	public static File saleManExport(String fileName, List<MarketingOrderDto> moDtoList) {
		Logger.info("导出时间start：" + new Date());
		XSSFWorkbook workBook = new XSSFWorkbook();// 创建一个工作簿
		XSSFSheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		Map<String, XSSFCellStyle> cellStyleMap = getSaleManCellStyleMap(workBook);
		// 表头
		String[] headers = { "营销单号", "下单时间", "分部", "业务员", "分销商", "分销商名称", "商品编码","商品名称","数量", "分销价", "小计", "业务备注" };
		int[] columnWidths = {6400, 4160,3424, 1824, 3264, 2816, 2592, 7392, 2048, 1824, 2560, 3808};
		XSSFRow titleRow = sheet.createRow(0);
		XSSFCellStyle titleCellstyle = cellStyleMap.get("titleCellstyle");
		for (int j = 0; j < headers.length; j++) {
			XSSFCell titleCell = titleRow.createCell(j);
			// 设置单元格格式
			sheet.setColumnWidth(j, columnWidths[j]);
			titleCell.setCellType(XSSFCell.CELL_TYPE_STRING);
			titleCell.setCellStyle(titleCellstyle);
			// 将数据填入单元格
			titleCell.setCellValue(headers[j]);
		}
		insertOrders(sheet, cellStyleMap, moDtoList);
		// 生成文件
		return createExcel(fileName, workBook);
	}
	
	class Sum{
		Integer sql;
		Double sumPrice;
		/**
		 * @param sql
		 * @param sumPrice
		 */
		public Sum(Integer sql, Double sumPrice) {
			super();
			this.sql = sql;
			this.sumPrice = sumPrice;
		}
		public Integer getSql() {
			return sql;
		}
		public void setSql(Integer sql) {
			this.sql = sql;
		}
		public Double getSumPrice() {
			return sumPrice;
		}
		public void setSumPrice(Double sumPrice) {
			this.sumPrice = sumPrice;
		}
	}
	
	/**
	 * 
	 * @author zbc
	 * @since 2017年3月17日 下午12:18:36
	 */
	private static void insertOrders(XSSFSheet sheet, Map<String, XSSFCellStyle> cellStyleMap,
			List<MarketingOrderDto> moDtoList) {
		// TODO Auto-generated method stub
		// 从第4行开始是具体数据了
		int fromRow = 1;
		int rowNum = 1;
		XSSFCellStyle dataCellStyle = cellStyleMap.get("dataCellStyle");
		LinkedHashMap<Integer,Integer> firstCol2LastCol = Maps.newLinkedHashMap();
		firstCol2LastCol.put(0, 0);
		firstCol2LastCol.put(1, 1);
		firstCol2LastCol.put(2, 2);
		firstCol2LastCol.put(3, 3);
		firstCol2LastCol.put(4, 4);
		firstCol2LastCol.put(5, 5);
		firstCol2LastCol.put(11, 11);
		for (int i = 0; i < moDtoList.size(); i++) {
			MarketingOrderDto moDto = moDtoList.get(i);
			List<MarketingOrderDetail> detailList = moDto.getDetailList();
			if (detailList == null || detailList.size() == 0) {
				Logger.info("第{}个营销单{}，因为详情为空，所以不导出", i,
						moDto.getMarketingOrderNo());
				continue;
			}
			//Logger.info("第{}个营销单{}", i, moDto.getMarketingOrderNo());
			// 合并单元格，前4列，最后两列的要做行合并
			int rowMergeSize = detailList.size();
			setRowMergedRegions(sheet, fromRow, (fromRow+rowMergeSize-1), firstCol2LastCol);
			fromRow = fromRow + rowMergeSize;// 下一次要合并的
			// 插入一条详情，所以rowNum要递增
			for (int detailIndex = 0; detailIndex < detailList.size(); detailIndex++,rowNum++) {
				insertEachRowData(sheet, dataCellStyle, i, rowNum, moDto,
						detailList.get(detailIndex));
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum,0, 11));
		Sum sum = getInstance().sum(moDtoList);
		XSSFRow dataRow = sheet.createRow(rowNum);
		XSSFCell cell  = null;
		for(int i = 0;i <= 11; i++){
			cell = dataRow.createCell(i);
			cell.setCellStyle(dataCellStyle);
			if(i == 0){
				cell.setCellValue("共"+sum.getSql()+"个商品           商品金额合计：￥"+sum.getSumPrice());
			}
		}
		
	}

	/**
	 * @author zbc
	 * @since 2017年3月17日 下午12:27:10
	 */
	private static void insertEachRowData(XSSFSheet sheet, XSSFCellStyle dataCellStyle, int i, int rowNum,
			MarketingOrderDto moDto, MarketingOrderDetail marketingOrderDetail) {
		// 创建1行数据行
		XSSFRow dataRow = sheet.createRow(rowNum);
		XSSFCell cell1 = dataRow.createCell(0);
		XSSFCell cell2 = dataRow.createCell(1);
		XSSFCell cell3 = dataRow.createCell(2);
		XSSFCell cell4 = dataRow.createCell(3);
		XSSFCell cell5 = dataRow.createCell(4);
		XSSFCell cell6 = dataRow.createCell(5);
		XSSFCell cell7 = dataRow.createCell(6);
		XSSFCell cell8 = dataRow.createCell(7);
		XSSFCell cell9 = dataRow.createCell(8);
		XSSFCell cell10 = dataRow.createCell(9);
		XSSFCell cell11 = dataRow.createCell(10);
		XSSFCell cell12 = dataRow.createCell(11);
		cell1.setCellStyle(dataCellStyle);
		cell2.setCellStyle(dataCellStyle);
		cell3.setCellStyle(dataCellStyle);
		cell4.setCellStyle(dataCellStyle);
		cell5.setCellStyle(dataCellStyle);
		cell6.setCellStyle(dataCellStyle);
		cell7.setCellStyle(dataCellStyle);
		cell8.setCellStyle(dataCellStyle);
		cell9.setCellStyle(dataCellStyle);
		cell10.setCellStyle(dataCellStyle);
		cell11.setCellStyle(dataCellStyle);
		cell12.setCellStyle(dataCellStyle);
		
		cell1.setCellValue(moDto.getMarketingOrderNo());
		cell2.setCellValue(DateUtils.date2string(moDto.getCreateDate(), DateUtils.FORMAT_LOCAL_DATE));
		cell3.setCellValue(moDto.getBranchName());
		cell4.setCellValue(moDto.getSalesman() == null?"":moDto.getSalesman());
		cell5.setCellValue(moDto.getEmail());
		cell6.setCellValue(moDto.getNickName() == null?"":moDto.getNickName());
		cell7.setCellValue(marketingOrderDetail.getSku());
		cell8.setCellValue(marketingOrderDetail.getProductName());
		cell9.setCellValue(marketingOrderDetail.getQty());
		cell10.setCellValue(marketingOrderDetail.getDisPrice());
		cell11.setCellValue(marketingOrderDetail.getDisPrice()*marketingOrderDetail.getQty());
		cell12.setCellValue(moDto.getBusinessRemark()==null ? "" : moDto.getBusinessRemark());
	}
	
}
