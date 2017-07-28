package controllers.warehousing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import dto.warehousing.GoodsInventoryListDto;
import dto.warehousing.GoodsInventorySearchDto;
import forms.warehousing.InventoryChangeForm;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.base.utils.JsonFormatUtils;
import services.warehousing.IGoodsInventoryService;
import services.warehousing.IMicroGoodsInventoryService;
import util.warehousing.Page;

/**
 * zbc 搬迁 b2c 代码
 * 云仓库存操作相关接口控制器
 * 
 * @author ye_ziran
 * @since 2016年3月2日 下午12:10:27
 */
public class CloudInventoryController extends Controller {

	@Inject
	IMicroGoodsInventoryService mInventoryService;
	
	@Inject
	IGoodsInventoryService goodsInventoryService;
	

	public Result create() {
		return update();
	}

	/**
	 * 查询接口
	 * 
	 * @param data
	 * @return
	 * @author ye_ziran
	 * @since 2015年11月4日 下午3:47:40
	 */
	public Result read() {
		Form<GoodsInventorySearchDto> goodsForm = Form.form(GoodsInventorySearchDto.class).bindFromRequest();
		GoodsInventorySearchDto dto = goodsForm.get();
		Page<GoodsInventoryListDto> page = goodsInventoryService.cloudInventoryQuery(dto);
		return ok(Json.toJson(page));
	}

	/**
	 * 更新
	 * <p>
	 * PUT协议
	 * </p>
	 * <ul>
	 * <li>用户传入订单数据，判断订单的出入库类型
	 * <li>根据出入库类型，记录需要的数据
	 * <li>根据数据，修改库存记录
	 * <li>需要数据：
	 * <p>
	 * {
	 *		 "orderType":0,
	 *		 "detailList":[{"sku":"xxx","num":x,"warehouseId":x,"warehouseName":"xx仓"}
	 *                      ,{...},...
	 *                    ]
	 * }
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月3日 下午2:30:12
	 */
	public Result update() {
		JsonNode json = request().body().asJson();
		Logger.info("api.inventory.update : {}", json);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (json.isNull()) {// 错误
			resultMap.put("result", false);
			resultMap.put("msg", "传输数据错误");
			Logger.info("InventoryController.update()--------传输数据错误");
		} else {
			InventoryChangeForm inventoryForm = JsonFormatUtils.jsonToBean(json.toString(), InventoryChangeForm.class);
			//更新库存
			List<Map<String, Object>> resList = goodsInventoryService.updateStock(inventoryForm);
			resultMap.put("result", true);
			resultMap.put("msg", resList);
		}

		return ok(Json.toJson(resultMap));
	}

	public Result delete() {
		Form<GoodsInventorySearchDto> goodsForm = Form.form(GoodsInventorySearchDto.class).bindFromRequest();
		GoodsInventorySearchDto dto = goodsForm.get();
		return ok(Json.toJson(goodsInventoryService.delete(dto)));
	}

}
