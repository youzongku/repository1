package handlers.inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.elasticsearch.common.collect.Maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import dto.product_inventory.CheckInventoryResult;
import dto.product_inventory.InventoryCommonResult;
import dto.product_inventory.InventoryPhysicalResult;
import dto.product_inventory.ProductMicroInventoyResult;
import entity.product_inventory.ProductInventoryDetail;
import entity.product_inventory.ProductInventoryTotal;
import events.product_inventory.CheckInventoryEvent;
import events.product_inventory.PhysicalInventoryEvent;
import mapper.product_inventory.ProductInventoryDetailMapper;
import mapper.product_inventory.ProductInventoryTotalMapper;
import mapper.product_inventory.ProductMicroInventoryInRecordMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.product_inventory.IinventorySyncRecordService;
import utils.inventory.DateUtils;

/**
 * 每日统计云仓库存数据
 * 输出相关不合理的sku库存信息
 * @author lenovo
 *
 */
public class CheckInventoryHandler {
	
	@Inject
	private IinventorySyncRecordService inventorySyncRecordService;
	
	@Inject
	private ProductInventoryDetailMapper cloudInventoryDetailMapper;
	
	@Inject
	private ProductMicroInventoryInRecordMapper microInventoryInRecordMapper;
	
	@Inject
	private ProductInventoryTotalMapper inventoryTotalMapper;
	
	/**
	 * 
	 * @param event
	 */
	@Subscribe
	public void checkAllBBCInventory(CheckInventoryEvent event){
		try {
			//erp缺少数据集合
			List<CheckInventoryResult> erpLackDataLists=Lists.newArrayList();
			
			//BBC售出总量大于erp总量
			List<CheckInventoryResult> bbcTotalSalesMoreThanErpLists=Lists.newArrayList();
			
			//BBC明细售出数量大于erp明细数量
			List<CheckInventoryResult> bbcDetailSalesMoreThanErpLists=Lists.newArrayList();
			
			//获取BBC所有有效sku数据
			List<ProductInventoryTotal> inventoryLists = inventoryTotalMapper.getAll();
			
			//遍历
			for(ProductInventoryTotal tempInventoryTotal:inventoryLists){
				Integer warehouseId=tempInventoryTotal.getWarehouseId();
				String sku=tempInventoryTotal.getSku();
				try{
					//获取erp数据
					InventoryCommonResult<JsonNode> erpInventoryDataResult = inventorySyncRecordService.getErpExpirationInventoryData(sku, warehouseId, 0);
					if(erpInventoryDataResult.getResultCode().intValue()==1){
						CheckInventoryResult tempResult=new CheckInventoryResult(sku,warehouseId,erpInventoryDataResult.getMsg());
						tempResult.setCloudInventory(tempInventoryTotal.getStock());
						erpLackDataLists.add(tempResult);
						continue;
					}
					JsonNode erpInventoryData = erpInventoryDataResult.getData();
					//解析erp返回数据
					String tempErpDataStr = erpInventoryData.toString();
					InventoryCommonResult<List<ProductInventoryDetail>> erpInventoryDetailResult=inventorySyncRecordService.formatErpInventoryData(tempErpDataStr,sku,warehouseId);
					if(erpInventoryDetailResult.getResultCode().intValue()==1){
						CheckInventoryResult tempResult=new CheckInventoryResult(sku,warehouseId,erpInventoryDetailResult.getMsg());
						tempResult.setCloudInventory(tempInventoryTotal.getStock());
						erpLackDataLists.add(tempResult);
						continue;
					}
					List<ProductInventoryDetail> erpInventoryDetailLists=erpInventoryDetailResult.getData();
					//校验总数量
					int erpTotal=0;
					for (ProductInventoryDetail productInventoryDetail : erpInventoryDetailLists) {
						erpTotal+=productInventoryDetail.getStock();
					}
					
					ProductInventoryDetail paramDetail=new ProductInventoryDetail(sku,warehouseId,null);
					//微仓囤货总数
					int microTotal=microInventoryInRecordMapper.getTotalMicroInventoryStockpileByExpiration(paramDetail);
					//订单占用
					ProductMicroInventoyResult orderOccupyResult= inventorySyncRecordService.getOrderOccupyNum(paramDetail);
					if(!orderOccupyResult.getResult()){
						
						CheckInventoryResult tempResult=new CheckInventoryResult(sku, warehouseId, erpTotal, microTotal, 0, tempInventoryTotal.getStock(),
																					Lists.newArrayList(), null, orderOccupyResult.getMsg());
						bbcTotalSalesMoreThanErpLists.add(tempResult);
						continue;
					}
					int orderOccupyTotal= (int) orderOccupyResult.getEntity();
					List<String> orderNoList= orderOccupyResult.getEntityList();
					if(orderOccupyTotal+microTotal>erpTotal){
						CheckInventoryResult tempResult=new CheckInventoryResult(sku, warehouseId, erpTotal, microTotal, orderOccupyTotal, tempInventoryTotal.getStock(),
								orderNoList, null, "bbc售出总数量大于erp总库存");
						bbcTotalSalesMoreThanErpLists.add(tempResult);
						continue;
					}
					//校验明细数量
					//云仓所有到期日器明细数据
					List<ProductInventoryDetail> bbcInventoryDetail=cloudInventoryDetailMapper.list(paramDetail);
					for (ProductInventoryDetail productInventoryDetail : bbcInventoryDetail) {
						InventoryCommonResult<CheckInventoryResult> detailCheckResult=this.checkInventoryDetail(productInventoryDetail,erpInventoryDetailLists);
						if(detailCheckResult.getResultCode()==1){
							CheckInventoryResult tempCheckResult = detailCheckResult.getData();
							bbcDetailSalesMoreThanErpLists.add(tempCheckResult);
							continue;
						}
					}
				}catch (Exception e) {
					Logger.info("库存清点sku={}，warehouseId={},发生异常{}",sku,warehouseId,e);
					continue;
				}
			}
			//将校验结果写入excel
			Map<String,List<CheckInventoryResult>> resultParam=Maps.newHashMap();
			resultParam.put("erpLackDataLists", erpLackDataLists);
			resultParam.put("total", bbcTotalSalesMoreThanErpLists);
			resultParam.put("detail", bbcDetailSalesMoreThanErpLists);
			try {
				this.writeResult(resultParam);
			} catch (IOException e) {
				Logger.info("库存清点结果{}", resultParam.toString());
				Logger.info("输出库存盘点文件发生异常{}", e);
			}
		} catch (Exception e) {
			Logger.info("库存清点发生异常{}", e);
		}
	}

	private void writeResult(Map<String, List<CheckInventoryResult>> resultParam) throws IOException {
		List<CheckInventoryResult> total = resultParam.get("total");
		List<CheckInventoryResult> detail = resultParam.get("detail");
		List<CheckInventoryResult> erpLackData = resultParam.get("erpLackDataLists");
		
		HSSFWorkbook workBook = new HSSFWorkbook();// 创建一个工作簿
		String[] handers={"sku", "warehouseId", "到期日期", "erp库存数量", "bbc云仓库存数量", "微仓囤货数量", "订单占用数量", "订单占用信息","清点信息"};
		this.createInventorySheet(workBook,total,handers,"erp库存总量不足");
		this.createInventorySheet(workBook,detail,handers,"erp明细库存不足");
		this.createInventorySheet(workBook,erpLackData,handers,"erp库存数据不完整");
		
		Configuration conf = Play.application().configuration()
				.getConfig("inventory");
		LocalDate today=LocalDate.now();
		String fileName=today+"库存清点结果.xls";
		String path = conf.getString("stockCheckFilePath");
		String filePath=path+File.separator+fileName;
		File file = new File(filePath); 
		OutputStream os = new FileOutputStream(file);
		workBook.write(os);
		os.flush();
		os.close();
	}

	private void createInventorySheet(HSSFWorkbook workBook, List<CheckInventoryResult> total, String[] handers, String sheetName) {
		HSSFSheet sheet = workBook.createSheet(sheetName);
		int[] width={4000,2500,3000,2500,2500,2500,2500,8000,10000};
		HSSFRow rowFirst = sheet.createRow(0);//标题
		for (int i = 0; i < handers.length; i++) {
			//设置表格宽度
			sheet.setColumnWidth(i, width[i]);
		    //获取第一行的每一个单元格
		    HSSFCell cell = rowFirst.createCell(i);
		    //往单元格里面写入值
		    cell.setCellValue(handers[i]);
		}
		if(total.size()<=0){
			return;
		}
		for (int i = 0;i < total.size(); i++) {
		    //获取list里面存在是数据集对象
			CheckInventoryResult checkInventoryResult = total.get(i);
		    //创建数据行
		    HSSFRow row = sheet.createRow(i+1);
		    //设置对应单元格的值
		    row.createCell(0).setCellValue(checkInventoryResult.getSku()==null?"":checkInventoryResult.getSku());
		    row.createCell(1).setCellValue(checkInventoryResult.getWarehouseId()==null?"":checkInventoryResult.getWarehouseId()+"");
		    row.createCell(2).setCellValue(checkInventoryResult.getExpirationDate()==null?"":checkInventoryResult.getExpirationDate());
		    row.createCell(3).setCellValue(checkInventoryResult.getErpStock()==null?"":checkInventoryResult.getErpStock()+"");
		    row.createCell(4).setCellValue(checkInventoryResult.getCloudInventory()==null?"":checkInventoryResult.getCloudInventory()+"");
		    row.createCell(5).setCellValue(checkInventoryResult.getMicroStockpile()==null?"":checkInventoryResult.getMicroStockpile()+"");
		    row.createCell(6).setCellValue(checkInventoryResult.getOrderOccupy()==null?"":checkInventoryResult.getOrderOccupy()+"");
		    row.createCell(7).setCellValue(checkInventoryResult.getOrderNoList()==null?"":checkInventoryResult.getOrderNoList().toString());
		    row.createCell(8).setCellValue(checkInventoryResult.getMsg()==null?"":checkInventoryResult.getMsg());
		}
	}

	/**
	 * 查看库存明细数据是否存在异常
	 * @param productInventoryDetail
	 * @param erpInventoryDetailLists
	 * @return
	 */
	private InventoryCommonResult<CheckInventoryResult> checkInventoryDetail(
			ProductInventoryDetail productInventoryDetail, List<ProductInventoryDetail> erpInventoryDetailLists) {
		InventoryCommonResult<CheckInventoryResult> result=new InventoryCommonResult<>();
		//erp明细库存数量
		int erpDetailStock=0;
		for (ProductInventoryDetail tempErpDetail : erpInventoryDetailLists) {
			if(tempErpDetail.getExpirationDate().getTime()==productInventoryDetail.getExpirationDate().getTime()){
				erpDetailStock=tempErpDetail.getStock();
			}
		}
		Date expirationDate = productInventoryDetail.getExpirationDate();
		String sku = productInventoryDetail.getSku();
		Integer warehouseId = productInventoryDetail.getWarehouseId();
		
		CheckInventoryResult checkResult=new CheckInventoryResult();
		checkResult.setSku(sku);
		checkResult.setWarehouseId(warehouseId);
		checkResult.setExpirationDate(DateUtils.date2string(expirationDate, DateUtils.FORMAT_DATE_PAGE));
		checkResult.setErpStock(erpDetailStock);
		checkResult.setCloudInventory(productInventoryDetail.getStock());
		checkResult.setOrderNoList(Lists.newArrayList());//防止list.toString()空指针
		//明细数据微仓囤货
		ProductInventoryDetail paramDetail=new ProductInventoryDetail(sku, warehouseId, expirationDate);
		int microStockpileWithExpiation = microInventoryInRecordMapper.getTotalMicroInventoryStockpileByExpiration(paramDetail);
		checkResult.setMicroStockpile(microStockpileWithExpiation);
		//明细订单占用
		ProductMicroInventoyResult orderOccupyNumResult = inventorySyncRecordService.getOrderOccupyNum(paramDetail);
		if(!orderOccupyNumResult.getResult()){//查询订单占用失败按照有问题处理
			checkResult.setOrderOccupy(0);
			checkResult.setMsg(orderOccupyNumResult.getMsg());
			result.setResultCode(1);//结果有误按失败处理
			result.setData(checkResult);
			return result;
		}
		int orderOccupyNum = (int) orderOccupyNumResult.getEntity();
		checkResult.setOrderOccupy(orderOccupyNum);
		List<String> orderNoList=orderOccupyNumResult.getEntityList();
		if(orderOccupyNum+microStockpileWithExpiation>erpDetailStock){//明细已售数量大于erp库存数量
			checkResult.setOrderNoList(orderNoList);
			checkResult.setMsg("bbc已销售数量大于erp库存");
			result.setResultCode(1);//结果有误按失败处理
			result.setData(checkResult);
			return result;
		}
		result.setResultCode(0);
		result.setData(checkResult);
		return result;
	}

}
