package services.sales.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import dto.sales.AsyncExportDto;
import dto.sales.ExportSaleOrderInfo;
import dto.sales.ShopDto;
import entity.sales.OrderExportTimeConfig;
import entity.sales.SaleBase;
import entity.sales.SaleDetail;
import entity.sales.SaleMain;
import events.sales.AsyncExportEvent;
import mapper.sales.OrderExportTimeConfigMapper;
import mapper.sales.SaleBaseMapper;
import mapper.sales.SaleDetailMapper;
import mapper.sales.SaleMainMapper;
import play.Logger;
import services.sales.IAsyncExportService;
import util.sales.CollectionUtils;
import util.sales.Constant;
import util.sales.DateUtils;
import util.sales.ExportUtil;
import util.sales.HttpUtil;
import util.sales.PriceFormatUtil;
import util.sales.StringUtils;

public class AsyncExportService  implements IAsyncExportService{
	
	
	@Inject private SaleDetailMapper saleDetailMapper;
	
	@Inject private SaleBaseMapper saleBaseMapper;
	
	@Inject private SaleMainMapper saleMainMapper;
	
	@Inject private OrderExportTimeConfigMapper cfgMapper;
	
	@Inject private EventBus ebus;	
	
	@Override
	public String createFile(Map<String, String[]> map,AsyncExportDto asyncExportDto,String[] headerString, Map<String, String> fieldsMap,List<String> accounts) {
		//TODO 校验文件是否存在 如果存在则提示用户下载
		if(isSync(asyncExportDto.getAdmin(), asyncExportDto.getExportType())){
			return "文件正在创建中...";
		}
		ebus.post(new AsyncExportEvent(map, asyncExportDto, headerString, fieldsMap));
		return "执行创建文件!";
	}
	
	@Override
	public  void insert(Map<String, String[]> map,AsyncExportDto asyncExportDto,String[] headerString, Map<String, String> fieldsMap){
		long start = System.currentTimeMillis();
		try {
			SXSSFWorkbook workbook = initWorkbook(asyncExportDto.getFilename(), headerString, fieldsMap);
			getExportOrderListOfBackstage(map,asyncExportDto.getAccounts(),headerString,
					workbook);
			ExportUtil.writeExcel(asyncExportDto, workbook);
		} catch (Exception e) {
			compelte(asyncExportDto);
			Logger.info("中断锁.....");
			e.printStackTrace();
		}finally {
			compelte(asyncExportDto);
			Logger.info("释放了锁.....");
		}
		Logger.info("填充数据耗时:{}",System.currentTimeMillis()-start);
	}
	
	public void getExportOrderListOfBackstage(Map<String, String[]> map,List<String> accounts, String[] headerString, SXSSFWorkbook workbook) throws InvalidFormatException, IOException {
		Map<String,Object> paramMap = new HashMap<String,Object>();
        if(accounts != null) {
        	paramMap.put("accounts", accounts);
        }
		String status = StringUtils.getStringParam(map, "status", "");
		String orderStartDate = StringUtils.getStringParam(map, "orderStartDate", "");
		String orderEndDate = StringUtils.getStringParam(map, "orderEndDate","");
		String warehouseId = StringUtils.getStringParam(map, "warehouseId","");
		String seachSpan = StringUtils.getStringParam(map, "seachSpan","");
		String distributorType = StringUtils.getStringParam(map, "distributorType","0");
		String source = StringUtils.getStringParam(map, "source","");
		paramMap.put("statusList", splitStatusStr(StringUtils.getStringBlank(status, true)));
		paramMap.put("warehouseId", warehouseId);
		paramMap.put("orderStartDate", orderStartDate);
		paramMap.put("orderEndDate", orderEndDate);
		paramMap.put("seachSpan", seachSpan);
		paramMap.put("distributorType", distributorType);
		paramMap.put("source", source);
		Logger.info("getExportOrderListOfBackstage in service，参数：{}",paramMap);
		Map<Integer,ShopDto> shopMap = getShop();
		int pageSize = 5000;
		//循环查询
		int size = 1;
		paramMap.put("pageSize", pageSize);
		int currPage = 1;
		List<SaleMain> currSaleMain = null;
		long startTime;
		int rowNum = 0;
		while (true) {
			paramMap.put("currPage",currPage);
			startTime = System.currentTimeMillis();
			currSaleMain = saleMainMapper.selectAllSaleMain(paramMap);
			Logger.info("查询时间:{}",System.currentTimeMillis()- startTime);
			size = currSaleMain.size();
			if(size == 0 ){
				break;
			}
			rowNum = getExportOrderInfo(currSaleMain,shopMap,workbook,headerString,rowNum);
			currPage++; 
		}
	}

	@Override
	public File dowloadFile(AsyncExportDto dto) {
		OrderExportTimeConfig cfg  = cfgMapper.selectByParmas(dto.getAdmin(),dto.getExportType());
		if(cfg == null || cfg.getIsSync() || cfg.getPath() == null){
			return null;
		}
		File file = ExportUtil.readExcel(cfg.getPath(), cfg.getFileName());
		new Thread(){
			@Override
			public void run() {
				try {
					remove(Thread.currentThread(),file,cfg);
				} catch (Exception e) {
					// TODO: handle exception
				}finally {
					//中断线程
					interrupt();
				}
			}
		}.start();
		return file;
		
	}
	
	private void remove(Thread t, File file, OrderExportTimeConfig cfg) {
		try {
			Thread.sleep(5000);
			//删除文件
			if(file != null){
				if(file.delete()){
					Logger.info("--删除文件成功--");
					cfgMapper.deleteByPrimaryKey(cfg.getId());
				}else{
					Logger.info("--删除文件失败--");	
				}
			}
			
		} catch (Exception e) {
			
		}
		finally {
			Logger.info("释放了锁.....");
		}
	}
	
	public  SXSSFWorkbook initWorkbook(String fileName, String[] header,
			Map<String, String> headerMapping) {
		Logger.info("导出时间start：" + new Date());
		SXSSFWorkbook workBook = new SXSSFWorkbook();// 创建一个工作簿
		Sheet sheet = workBook.createSheet(fileName.substring(0, fileName.indexOf(".")));// 创建工作表
		Row headRow = sheet.createRow(0); // 创建第一栏，抬头栏
		Cell cell = null;
		for (int j = 0; j < header.length; j++) {
			cell = headRow.createCell(j);// 创建抬头栏单元格
			// 设置单元格格式
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			sheet.setColumnWidth(j, 6000);
			CellStyle style = workBook.createCellStyle();
			Font font = workBook.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			short color = HSSFColor.RED.index;
			font.setColor(color);
			style.setFont(font);
			// 将数据填入单元格
			cell.setCellStyle(style);
			if (header[j] != null) {
				cell.setCellValue(headerMapping.get(header[j]));
			} else {
				cell.setCellValue("-");
			}
		}
		return workBook;
	}
	
	/**
	 * 获取分销渠道
	 *
	 * @param disModel
	 * @return
	 */
	private String getDistributionModelStr(Integer disModel) {
		//1、电商，2、经销商，3、KA直营，4、进口专营
		switch (disModel) {
			case  1:
				return "电商";
			case 2:
				return "经销商";
			case 3:
				return "KA直营";
			case 4:
				return "进口专营";
			default:
				return "";
		}
	}
	
	private static List<Integer> splitStatusStr(String statusStr){
		List<Integer> statusList = new ArrayList<>();
		if(statusStr!=null && statusStr.trim().length()>0){
			// 过滤掉status为0
			statusList = Stream.of(statusStr.split(","))
					.map(Integer::valueOf).filter(status -> status.intValue() != 0)
					.collect(Collectors.toList());
		}
		return statusList;
	}
	
	private  Map<Integer,ShopDto> getShop(){
		ObjectMapper obj = new ObjectMapper();
        //获取所有店铺信息
        Map<Integer,ShopDto> shopMap = Maps.newHashMap();
        try {
        	 String shopRes = HttpUtil.get(Maps.newHashMap(),HttpUtil.B2BBASEURL+"/member/getallstore");
        	 List<ShopDto> shops =  obj.readValue(shopRes, new TypeReference<List<ShopDto>>(){});
        	 shopMap = shops.stream().collect(
     	            Collectors.toMap(ShopDto::getId, (p) -> p)); 
        } catch (Exception e) {
			Logger.error("获取店铺信息错误",e);
		}
        return shopMap;
	}
	
	/**
	 * 校验任务是否正在执行
	 * @author zbc
	 * @since 2017年6月26日 下午4:46:23
	 * @param operator
	 * @param exportType
	 * @return
	 */
	private boolean isSync(String operator,String exportType){
		OrderExportTimeConfig cfg  = cfgMapper.selectByParmas(operator,exportType);
		if(cfg == null){
			cfg = new OrderExportTimeConfig(operator,exportType);
			cfgMapper.insertSelective(cfg);
		}else{
			if(cfg.getIsSync()){
				return true;
			}else{
				cfg.setIsSync(true);
				cfg.setUpdateTime(new Date());
				cfgMapper.updateByPrimaryKeySelective(cfg);
			}
		}
		return false;
	}
	
	/**
	 * 终结任务
	 * @author zbc
	 * @since 2017年6月26日 下午4:46:42
	 */
	private void compelte(AsyncExportDto asyncExportDto){
		OrderExportTimeConfig cfg  = cfgMapper.selectByParmas(asyncExportDto.getAdmin(),asyncExportDto.getExportType());
		if(cfg != null){
			cfg.setPath(asyncExportDto.getPath());
			cfg.setFileName(asyncExportDto.getFilename());
			cfg.setIsSync(false);
			cfg.setUpdateTime(new Date());
			cfgMapper.updateByPrimaryKeySelective(cfg);
		}
	}
	
	private int getExportOrderInfo(List<SaleMain> allSaleMain,Map<Integer,ShopDto> shopMap, SXSSFWorkbook workbook, String[] headerString,int rowNum) throws InvalidFormatException, IOException{
		ArrayList<ExportSaleOrderInfo> exportSaleOrderInfoList = Lists.newArrayList();
		Logger.info("查询时间Start：" + new Date());
        List<Integer> mainIds = Lists.transform(allSaleMain, main -> main.getId());
        if(mainIds == null || mainIds.size() <= 0) {
        	return rowNum;
        }
        Map<Integer, SaleMain> id2SaleMain = Maps.uniqueIndex(allSaleMain, saleMain -> saleMain.getId());
        
        List<List<Integer>> midsList = util.sales.CollectionUtils.createList(mainIds, 100);
        //base信息  分段取值
        List<SaleBase> bases = Lists.newArrayList();
		List<SaleDetail> saleDetails = Lists.newArrayList();
		List<SaleDetail> effectiveDetails = null;
		List<SaleBase> subBases = null;
        for (List<Integer> ids : midsList) {
        	effectiveDetails = saleDetailMapper.selectEffectiveDetails(ids);
			subBases = saleBaseMapper.selectBases(ids);
			saleDetails.addAll(effectiveDetails);
        	bases.addAll(subBases);
		}
        Map<Integer, SaleBase> mainId2SaleBase = Maps.uniqueIndex(bases, b -> b.getSalesOrderId());
        ShopDto shop  = null;
        //base信息  分段取值
		SaleBase saleBase = null;
        SaleMain saleMain = null;
        ExportSaleOrderInfo exportOrder = null;
        for (SaleDetail sd : saleDetails) {
        	saleBase = mainId2SaleBase.get(sd.getSalesOrderId());
        	saleMain = id2SaleMain.get(sd.getSalesOrderId());
        	if(saleBase == null || saleMain == null) {
        		 continue; 		
        	}
        	saleMain.setFrontOrManager(SaleMain.STATUS_MANAGER);
        	exportOrder = new ExportSaleOrderInfo();
        	exportOrder.setEmail(saleMain.getEmail());
        	exportOrder.setErpOrderNo(saleMain.getErpOrderNo());
        	exportOrder.setNickName(saleMain.getNickName());
			exportOrder.setBbcPostage(saleBase.getBbcPostage());
			exportOrder.setSaleOrderNO(saleMain.getSalesOrderNo());
			exportOrder.setOrderId(saleMain.getId());
			exportOrder.setPlatformOrderNo(saleBase.getPlatformOrderNo());
			exportOrder.setBuyerId(saleBase.getBuyerID());
			exportOrder.setReceiver(saleBase.getReceiver());
			exportOrder.setTel(saleBase.getTel());
			exportOrder.setAddress(saleBase.getAddress());
			exportOrder.setSku(sd.getSku());
			exportOrder.setProductName(sd.getProductName());
			exportOrder.setQty(sd.getQty());
			exportOrder.setStatus(saleMain.getStatusDesc());
			exportOrder.setOrderActualAmount(saleBase.getOrderActualAmount());
//			exportOrder.setPayAccount("");付款账户暂时没有 后期补充
			exportOrder.setOrderActualPayAmount(saleMain.getOrderActualPayment());
			exportOrder.setPlatformAmount(saleMain.getPlatformAmount());
//			exportOrder.setCost(sd.getCost());
			exportOrder.setReceiverIDcard(saleBase.getIdcard());
//			exportOrder.setDisAccount(saleMain.getEmail());
			exportOrder.setDistributorType(Constant.DIS_TYPE_AMP.get(saleMain.getDistributorType()));
			exportOrder.setWarehouseName(saleMain.getWarehouseName());
			exportOrder.setTradeNo(saleMain.getTradeNo());
			exportOrder.setOrderDateStr(saleMain.getOrderingDate() + "");
			exportOrder.setFinalSellingPrice(
					sd.getFinalSellingPrice() != null ? PriceFormatUtil.toFix2(sd.getFinalSellingPrice())
							: PriceFormatUtil.toFix2(sd.getPurchasePrice()));
			exportOrder.setExpirationDate(DateUtils.date2string(sd.getExpirationDate(), "yyyy-MM-dd"));
			//获得店铺id
			Integer shopId = saleBase.getShopId();
			shop = shopMap.get(shopId);
			exportOrder.setShopName(shop != null ? shopMap.get(shopId).getShopName() : "");
			exportOrder.setRemark(saleBase.getRemark());
			exportOrder.setErp(saleBase.getCustomerservice());
			exportOrder.setDistributionModelStr(saleMain.getDisMode() == null ? "" : getDistributionModelStr(saleMain.getDisMode()));
			exportOrder.setCapfee(sd.getCapFee() == null ? PriceFormatUtil.toFix2(sd.getPurchasePrice())
					: PriceFormatUtil.toFix2(sd.getCapFee()));
			exportSaleOrderInfoList.add(exportOrder);
		}
		List<List<ExportSaleOrderInfo>> createList = CollectionUtils.createList(exportSaleOrderInfoList, 5000);
		for(List<ExportSaleOrderInfo> datas:createList){
			Logger.info("插入数据条数:{}",datas.size());
			rowNum = insertData(headerString, datas, workbook, rowNum);
		}
	    Logger.info("查询时间END：" + new Date());
		return rowNum;
	}
	
	
	private  int insertData(String[] header, List<?> data, SXSSFWorkbook workbook,int rowNum) throws IOException {
		Cell cell;
		Row row;
		Sheet sheet = workbook.getSheetAt(0);
		for (int i = 0; i < data.size(); i++) {
			rowNum++;
			row = sheet.createRow(rowNum);
			// 单元格
			for (int j = 0; j < header.length; j++) {
				cell = row.createCell(j);
				Object value = ExportUtil.parseData(header[j], data.get(i));
				if (null == value) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(value + "");
				}
			}
		}
		return rowNum;
	}
	
}



