package controllers.product;

import java.util.Date;
import java.util.HashMap;

import org.elasticsearch.common.collect.Maps;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.product.IEsProductService;
import session.ISessionService;
import util.product.DataUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import dto.product.ProductSearchParamDto;

public class EsProductController extends Controller {
	
	@Inject
	private IEsProductService esService;
	
	@Inject
	private ISessionService session;
	
	/**
	 * 创建索引
	 * @return
	 */
	public Result createProductIndex(){
		boolean result = esService.createProductIndex();
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", result);
		map.put("msg", result?"创建索引成功":"创建索引失败");
		return ok(Json.toJson(map));
	}
	
	/**
	 * 初始化数据
	 * @return
	 */
	public Result initProductDatas(){
		esService.initProductDatas();
		HashMap<Object,Object> map = Maps.newHashMap();
		map.put("suc", true);
		map.put("msg", "添加数据成功");
		return ok(Json.toJson(map));
	}
	
	/**
	 * 从es查询商品接口
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月8日 下午2:33:11
	 */
	public Result productFromEs() {
		JsonNode json = request().body().asJson();
		Logger.info("调用es查询商品接口 : " + json);
		Logger.info("start:" + new Date());
		
		ProductSearchParamDto searchDto = JsonFormatUtils.jsonToBean(json.get("data").toString(),
				ProductSearchParamDto.class);
		Logger.info("查询商品的参数："+searchDto);
		if(searchDto.getModel()==null){
			searchDto.setModel(session.get("model") != null ? Integer.parseInt(session.get("model").toString()) : null);
		}
		Result result = ok(DataUtil.formatData(esService.products(searchDto), null));
		Logger.info("end:" + new Date());
		return result;
	}
	
	public Result delete(String id){
		Result result = ok(DataUtil.formatData(esService.delete(id), null));
		return result;
	}
	
}
