package controllers.inventory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import controllers.annotation.Login;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.inventory.IInventoryRecordService;

/**
 * 物理库存变更记录
 * @author Alvin Du
 */
public class InventoryRecord extends Controller {

    @Inject
    private IInventoryRecordService inventoryRecordService;
    
//    @Inject
//    ISessionService sessionService;

//    /**
//     * 获取当前用户默认条件下的微仓库存数据
//     * @return
//     */
//    public Result toInven() {
//        Map<String, Object> result = Maps.newHashMap();
//        Cookie ULS = Context.current().request().cookie("USER_LOGIN_STATE");
//        String token = ULS == null ? null : ULS.value();
//        if (Strings.isNullOrEmpty(token)) {
//            result.put("success", "false");
//            result.put("errorCode", "2");
//            Logger.debug("当前用户未登录");
//        } else {
//            Cookie CLU = Context.current().request().cookie("CURRENT_LOGIN_USER");
//            String email = CLU == null ? null : CLU.value();
//            Map<String, Object> map = Maps.newHashMap();
//            map.put("email", email);
//            map.put("startNum", startNum);
//            map.put("pageSize", pageSize);
//            Integer rows = disInventoryService.getCountsByPage(map);
//            List<DisInventoryDto> invs = disInventoryService.getDisInventorysByPage(map);
//            if (invs.isEmpty()) {
//                result.put("success", "false");
//                result.put("message", "当前用户微仓产品数据为空");
//            } else {
//                result.put("success", "true");
//                result.put("page", new Page(1, pageSize, rows, invs));
//            }
//        }
//        return ok(Json.toJson(result));
//    }
//
//    /**
//     * 获取当前用户指定条件下的微仓库存数据
//     * @return
//     */
//    public Result getInven() {
//        Map<String, Object> result = Maps.newHashMap();
//        Cookie ULS = Context.current().request().cookie("USER_LOGIN_STATE");
//        String token = ULS == null ? null : ULS.value();
//        if (Strings.isNullOrEmpty(token)) {
//            result.put("success", "false");
//            result.put("errorCode", "2");
//            Logger.debug("当前用户未登录");
//        } else {
//            Cookie CLU = Context.current().request().cookie("CURRENT_LOGIN_USER");
//            String email = CLU == null ? null : CLU.value();
//            Map<String, String> params = Form.form().bindFromRequest().data();
//            if (params == null || params.size() == 0) {
//                result.put("success", "false");
//                result.put("message", "参数不存在");
//                return ok(Json.toJson(result));
//            }
//            Map<String, Object> map = Maps.newHashMap();
//            map.put("email", email);
//            map.put("pageSize", pageSize);
//            String currPage = params.get("currPage");
//            //3个未定查询条件
//            String state = params.get("state");
//            String category = params.get("cate");
//            String search = params.get("search");
//            if (!Strings.isNullOrEmpty(state)) {
//                map.put("state", state);
//            }
//            if (!Strings.isNullOrEmpty(category)) {
//                map.put("category", category);
//            }
//            if (!Strings.isNullOrEmpty(search)) {
//                map.put("search", search);
//            }
//            if (Strings.isNullOrEmpty(currPage)) {
//                currPage = "1";
//                map.put("startNum", startNum);
//            } else {
//                map.put("startNum", (Integer.valueOf(currPage) - 1) * pageSize);
//            }
//            Integer rows = disInventoryService.getCountsByPage(map);
//            List<DisInventoryDto> invs = disInventoryService.getDisInventorysByPage(map);
//            if (invs.isEmpty()) {
//                result.put("success", "false");
//                result.put("message", "当前用户微仓产品数据为空");
//            } else {
//                result.put("success", "true");
//                result.put("page", new Page(Integer.valueOf(currPage), pageSize, rows, invs));
//            }
//        }
//        return ok(Json.toJson(result));
//    }
    
    /**
     * 物理库存变更记录操作，以下情况可能会使用到
     * <ul>
     *     <li>采购订单生成之后</li>
     *     <li>采购订单支付之后</li>
     *     <li>采购订单关闭之后</li>
     * </ul>
     * 
     * 参数数据格式
     * {
     *     "email":"xxx",
     *     "purchaseOrderId":"xxx",
     *     "status":"xxx",
     *     "pros":[
     *     			{
     *     				"sku":"xxx",
     *     				"qty":"xxx",
     *     				"warehouseId":"xxx"
     *     			},...
     *     		  ]
     * }
     * 
     * @return
     */
    @Login
    @BodyParser.Of(BodyParser.Json.class)
    public Result stockChangeRecord(){
     	JsonNode main = request().body().asJson();
    	
    	if (main.size() == 0) {
			return internalServerError("Expecting Json data");
		}
    	
    	return ok(Json.toJson(inventoryRecordService.updateInventoryRecord(main)));
    }
    
}
