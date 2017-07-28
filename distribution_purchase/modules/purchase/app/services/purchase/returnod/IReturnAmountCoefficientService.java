package services.purchase.returnod;

import java.util.List;
import java.util.Map;

import dto.purchase.returnod.ReturnAmountCoefficientDto;
import entity.purchase.returnod.ReturnAmountCoefficient;
import entity.purchase.returnod.ReturnAmountCoefficientLog;
import forms.purchase.Page;

/**
 * 商品退款系数service
 * @author huangjc
 * @date 2017年2月14日
 */
public interface IReturnAmountCoefficientService {
	/**
	 * 封装了getProducts接口返回的数据
	 * @return 商品退款系数，有可能为null
	 */
	ReturnAmountCoefficient getProductCoefficients(String sku, Integer warehouseId);
	
	/**
	 * 获取商品退款系数
	 * @param params
	 */
	Page<ReturnAmountCoefficientDto> getCoefficientsOfProducts(Map<String,Object> params); 
	
	/**
	 * 批量设置商品退款系数
	 * @param coefficientList
	 * @return
	 */
	Map<String,Object> setProductsCoefficients(List<ReturnAmountCoefficient> coefficientList);

	List<ReturnAmountCoefficientLog> getSetCoefficientLogs(String sku, int warehouseId);
	
}
