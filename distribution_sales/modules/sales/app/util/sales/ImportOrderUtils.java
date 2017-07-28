package util.sales;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.google.common.base.Strings;

import dto.sales.ImportResultInfo;
import entity.sales.ImportOrderTemplateField;
import play.Logger;

/**
 * @author hanfs
 * 描述：导入订单工具类
 *2016年5月16日
 */
public class ImportOrderUtils {
	
	
	/**
	 * 描述：csv校验空值是否通过
	 * 2016年5月16日
	 * @param rowNumber 行号(从0开始)
	 * @param fieldValues 值列表
	 * @param resultInfo 返回信息实体
	 * @param templateFields 模板属性字典表
	 * @param fieldValueMap 属性及值映射map
	 * @return
	 */
	public static boolean validateNullForCsv(int rowNumber,String[] fieldValues,List<ImportOrderTemplateField> templateFields,ImportResultInfo resultInfo,Map<String,String> fieldValueMap){
		//该行空值是否通过校验(默认通过)
		boolean isRightForNull = true;
		for (ImportOrderTemplateField templateField : templateFields) {
			//列号（从0开始）
			int cellNumber = templateField.getPosition();
			//列值
			String cellValue = fieldValues[cellNumber];
			//判断是否为科学计数法
			String regx = "[+-]?[0-9]+.[0-9]+[Ee][+-]?\\d+";
			Pattern pattern = Pattern.compile(regx);
			if (pattern.matcher(cellValue).matches()) {
				BigDecimal db = new BigDecimal(cellValue);
				cellValue = db.toPlainString();
			}
			//填充数据到map
			fieldValueMap.put(templateField.getPropertyName(), cellValue);
			if(!templateField.isRightForNullValue(cellValue)){
				resultInfo.getMessages().add("第["+(rowNumber+1)+"]行["+(cellNumber+1)+"]列"+templateField.getTemplateName()+"为空");
				isRightForNull = false;
				continue;
			}
		}
		//针对淘宝订单修改后的收货地址（address）为空就取收货地址（receiverAddress）
		if (Strings.isNullOrEmpty(fieldValueMap.get("address")) && !Strings.isNullOrEmpty(fieldValueMap.get("receiverAddress"))){
			fieldValueMap.put("address", fieldValueMap.get("receiverAddress"));
		}
		Logger.info("CSV校验空值结果："+(isRightForNull?"通过":"不通过"));
		return isRightForNull;
	}
	
	/**
	 * 描述：excel校验空值是否通过
	 * 2016年5月16日
	 * @param rowNumber 行号(从0开始)
	 * @param row excel行
	 * @param templateFields
	 * @param resultInfo 返回信息实体
	 * @param fieldValueMap 属性及值映射map
	 * @return
	 */
	public static boolean validateNullForExcel(int rowNumber,Row row,List<ImportOrderTemplateField> templateFields,ImportResultInfo resultInfo,Map<String,String> fieldValueMap){
		//该行空值是否通过校验(默认通过)
		boolean isRightForNull = true;
		for (ImportOrderTemplateField templateField:templateFields) {
			//列号（从0开始）
			int cellNumber = templateField.getPosition();
			//列值
			String cellValue = ExcelImportUtils.readCellByType(row.getCell(cellNumber));
			//填充数据到map
			fieldValueMap.put(templateField.getPropertyName(), cellValue);
			if(!templateField.isRightForNullValue(cellValue)){
				resultInfo.getMessages().add("第["+(rowNumber+1)+"]行["+(cellNumber+1)+"]列"+templateField.getTemplateName()+"为空");
				isRightForNull=false;
				continue;
			}
		}
		//针对淘宝订单修改后的收货地址（address）为空就取收货地址（receiverAddress）
		if (Strings.isNullOrEmpty(fieldValueMap.get("address")) && !Strings.isNullOrEmpty(fieldValueMap.get("receiverAddress"))){
			fieldValueMap.put("address", fieldValueMap.get("receiverAddress"));
		}
		Logger.info("excel校验空值结果："+(isRightForNull?"通过":"不通过"));
		return isRightForNull;
	}
	
	/**
	 * 描述：校验邮箱
	 * 2016年5月16日
	 * @param orderOrGoods 订单或商品
	 * @param isOrder 是否为订单
	 * @return
	 */
	public static boolean isEmail(Object orderOrGoods,Boolean isOrder){
		return false;
	}
	
	/**
	 * 描述：校验电话号码
	 * 2016年5月16日
	 * @param orderOrGoods 订单或商品
	 * @param isOrder 是否为订单
	 * @return
	 */
	public static boolean isTelephone(Object orderOrGoods,Boolean isOrder){
		return false;
	}
	
	/**
	 * 描述：校验手机号码
	 * 2016年5月16日
	 * @param orderOrGoods 订单或商品
	 * @param isOrder 是否为订单
	 * @return
	 */
	public static boolean isMobilePhone(Object orderOrGoods,Boolean isOrder){
		return false;
	}
	
	/**
	 * 描述：校验sheet的标题的正确性
	 * 2016年5月17日
	 * @param sheet
	 * @param templateFields 模板属性字典
	 * @param isOrder 校验的是否为订单
	 * @return
	 */
	public static boolean isRightSheetByFirstRow(Sheet sheet,List<ImportOrderTemplateField> templateFields,boolean isOrder){
		Row firstRow = sheet.getRow(0);
		Logger.info("校验"+(isOrder?"订单":"商品")+"模板标题开始，校验信息如下：");
		boolean isRightSheet = true;
		if (firstRow == null) {
			isRightSheet = false;
		}else{
			// 校验每个模板字段在excel中是否存在
			for (ImportOrderTemplateField templateField : templateFields) {
				int position = templateField.getPosition();
				// 拿到对应的列的值
				String title = ExcelImportUtils.readCellByType(firstRow.getCell(position));
				if(!templateField.isRightForTemplateName(title)){
					Logger.info("校验模板标题["+title+"]失败");
					isRightSheet = false;
				}
			}
		}
		Logger.info("校验["+sheet.getSheetName()+"]sheet"+(isOrder?"订单":"商品")+"标题正确性 结果："+(isRightSheet?"正确":"不正确"));
		return isRightSheet;
	}
	public static String[] getTemplateTitleByTemplateField(List<ImportOrderTemplateField> templateFields){
		String[] titleArray = new String[]{};
		if (templateFields!= null && !templateFields.isEmpty()) {
			//获取最大的位置编号（查询获取的模板字典按位置升序排列）
			Integer maxIndex = templateFields.get(templateFields.size()-1).getPosition();
			titleArray = new String[maxIndex+1];
			//默认填充为空
			for (int i = 0,len=titleArray.length; i < len; i++) {
				titleArray[i] = "";
			}
			//模板标题依据位置编号对号入座
			for (ImportOrderTemplateField templateField : templateFields) {
				titleArray[templateField.getPosition()] = templateField.getTemplateName();
			}
		}
		return titleArray;
	}
	public static String getStringNotZeroForCellValue(String cellValue){
		return StringUtils.isBlank(cellValue) ? null : cellValue.replace(".00", "");
	}
	public static boolean isNullRow(Row row){
		boolean isNullRow = true;
		//当前行总列数
		int cellCount = row.getPhysicalNumberOfCells();
		if (cellCount >0) {
			for(int i=0;i<cellCount;i++){
				if (!Strings.isNullOrEmpty(ExcelImportUtils.readCellByType(row.getCell(i)))) {
					isNullRow = false;
					break;
				}
			}
		}
		return isNullRow;
	}
}
