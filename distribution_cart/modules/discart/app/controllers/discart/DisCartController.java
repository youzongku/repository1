package controllers.discart;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.Login;
import dto.discart.ProcessResultDto;
import dto.discart.DeliveryInfoResult;
import dto.discart.JsonResult;
import dto.discart.ProductSelectedStateParam;
import entity.discart.DisCart;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.discart.IDisCartItemService;
import service.discart.IDisCartService;
import service.discart.IUserService;
import utils.discart.JsonCaseUtil;
import utils.discart.StringUtils;

/**
 * Created by LSL on 2015/12/1.
 */
@Api(value="/cart",description="商品模块")
public class DisCartController extends Controller {
	@Inject
    private IDisCartService disCartService;
    @Inject
    private IDisCartItemService disCartItemService;
    @Inject
    private IUserService userService;
    
    /**
     *描述：用户注册成功后创建购物车
     *@param {"email":"email"}
     *@return 购物车添加结果
     */
    public Result addDisCart(){
    	Map<String, String> dataMap = Form.form().bindFromRequest().data();
    	Map<String,Object> resultMap = new HashMap<String,Object>();
    	String email = dataMap.get("email");
    	resultMap.put("email", email);
    	if(StringUtils.isBlankOrNull(email)){
    		resultMap.put("msg", "无法添加购物车，传入邮箱为空");
        	resultMap.put("result", false);
        	resultMap.put("email", email);
        	return ok(Json.toJson(resultMap));
    	}
    	DisCart disCart = disCartService.getUsableDisCart(email);
		if (disCart == null) {
			resultMap.put("msg", "添加购物车失败");
			resultMap.put("result", false);
			return ok(Json.toJson(resultMap));
		}

		resultMap.put("result", true);
		resultMap.put("msg", "添加购物车成功");
		return ok(Json.toJson(resultMap));
    }
    
    /**
     * 添加商品至购物车
     * 2016年12月14日
     * @return
     */
    @ApiOperation(value = "添加商品至购物车", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParams({
	@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
		,defaultValue = "{\"proArray\":[{\"sku\":\"IF942-2\",\"warehouseId\":2024,\"pQty\":1}]}"
			) })
    @Login
	public Result pushCart() {
    	/**
    	 {
    	 	proArray:[
    	 		{sku:xxx, warehouseId:xxx, pQty:1}
    	 	]
    	 }
    	 */
    	JsonNode json = request().body().asJson();
    	Logger.info("添加商品进购物车，参数：{}", json);
		if(json == null || !json.has("proArray")) {
			Map<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("result", false);
			resultMap.put("msg", "参数错误");
			return ok(Json.toJson(resultMap));
		}
		String email = userService.getDisAccount();
		if (StringUtils.isBlankOrNull(email)) {
			Logger.info("添加商品至购物车：获取当前登录用户失败");
			Map<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("suc", false);
			resultMap.put("msg", "添加商品失败");
			return ok(Json.toJson(resultMap));
		}
		
		JsonNode login = Json.parse(userService.getDismember());
		Integer distributionMode = login.get("distributionMode").asInt();
		return ok(Json.toJson(disCartService.pushCart(email, distributionMode, json)));
	}
    
    /**
     * 获取当前用户可用购物车列表商品数据
     * @return
     */
    @Login
    public Result getDisCartData() {
        return ok(Json.toJson(disCartItemService.getDisCartData(userService.getDismember())));
    }

    /**
     * 从购物车中删除指定商品
     * @return
     */
    @Login
    public Result removeDisCartItem() {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, String> params = Form.form().bindFromRequest().data();
        if (params == null || !params.containsKey("itemId")) {
            result.put("result", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
        
        Logger.debug("removeDisCartItem params-->{}", params.toString());
        String email = userService.getDisAccount();
        String itemId = params.get("itemId");
        //直接物理删除
        return ok(Json.toJson(disCartItemService.deleteDisCartItemById(email, itemId)));
    }

    /**
     * 更新购物车中指定商品的购买数量
     * @return
     */
    @Login
    public Result updateDisCartItemQty() {
        JsonNode node = request().body().asJson();
        Logger.debug("updateDisCartItemQty params-->[{}]",node);
        if (node == null || !node.has("itemId") || !node.has("qty")) {
        	Map<String, Object> result = Maps.newHashMap();
        	result.put("result", false);
            result.put("msg", "请求参数不存在或格式错误");
            return ok(Json.toJson(result));
        }
        
        int itemId = node.get("itemId").asInt();
        int qty = node.get("qty").asInt();
        String email = userService.getDisAccount();
        return ok(Json.toJson(disCartItemService.updatePurchaseQties(email, itemId, qty)));
    }

    /**
     * 删除购物车关联的优惠
     * @return
     */
    @Login
    public Result deleteActiveInfo() {
    	String email = userService.getDisAccount();
        return ok(Json.toJson(disCartService.deleteActiveInfo(email)));
    }

    /**
     * 更新购物车中商品的选中状态
     * @return
     */
    @Login
    public Result selectCartItems2BeOrdered() {
        JsonNode node = request().body().asJson();
        Logger.info("勾选购物车，参数：{}", node);
        
        /*
         {
         	allInCart: true/false,
         	selected: true/false,
         	itemId: 1
         }
         */
		if (node == null || !node.has("allInCart") || !node.has("selected")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		boolean allInCart = node.get("allInCart").asBoolean();
		if (!allInCart && !node.has("itemId")) {// 为false，是更新单个的
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		ProductSelectedStateParam param = new ProductSelectedStateParam(allInCart, node.get("selected").asBoolean());
		if (!allInCart) {// 更新单个商品
			param.setItemId(node.get("itemId").asInt());
		}
		JsonNode dismember = Json.parse(userService.getDismember());
		param.setEmail(dismember.get("email").asText());
		param.setDistributionMode(dismember.get("distributionMode").asInt());
        
        Map<String, Object> updateResult = disCartItemService.batchUpdateSelectStateOfCartItem(param);
        return ok(Json.toJson(updateResult));
    }
    
    /**
     * 保存购物车中选择的活动信息
     * @return
     */
    @Login
    public Result saveActiveInfo(){
    	JsonNode node = request().body().asJson();
    	if(null == node) {
    		Map<String, Object> result = Maps.newHashMap();
    		result.put("suc", false);
    		return ok(Json.toJson(result));
    	}
    	
    	String email = userService.getDisAccount();
    	return ok(Json.toJson(disCartService.saveActiveInfo(node,email)));
    }
    
    /**
     * 后台修改商家模式，删除购物车中所有商品（member模块使用到了）
     * @return
     */
    public Result deleteAll() {
    	JsonNode node = request().body().asJson();
    	if(node==null||!node.has("email")){
    		Map<String, Object> result = Maps.newHashMap();
    		result.put("success", false);
    		result.put("msg", "数据有误");
    		return ok(Json.toJson(result));
    	}
    	
    	String email = node.get("email").asText();
    	Logger.info("根据邮箱删除信息{}",email);
    	String retStr= disCartItemService.deleteDisCartItemByEmail(email);
    	return ok(Json.parse(retStr));
    }
    
    /**
     * 后台购物车采购单下单
     * @author zbc
     * @since 2017年3月23日 上午10:06:03
     */
    @Login
    public Result order(){
    	JsonNode json = request().body().asJson();
    	Logger.info("购物车下单参数:[{}]",json);
    	// 参数检查
		if (json == null) {
    		Map<String,Object> res = Maps.newHashMap();
    		res.put("suc",false);
    		res.put("msg","参数错误");
    		return ok(Json.toJson(res));
    	}
    	
    	return ok(Json.toJson(disCartService.order(json.toString(),userService.getDismember())));
    }
    

	/**
	 * 获取登陆用户的购物车内SKU数
	 * @return
	 */
	@Login
	public Result getItemsCount() {

		ProcessResultDto dto = new ProcessResultDto();
		Map<String,Integer> result = new HashMap<String,Integer>();

		try {
			JsonNode login = Json.parse(userService.getDismember());
			String email = login.get("email").asText();
			Integer count = disCartItemService.getItemsCount(email);

			result.put("count",count);
			dto.setDatas(result);
			dto.setSuccess(true);

		}catch (Exception e){
			dto.setSuccess(false);
		}

		return ok(Json.toJson(dto));
	}

	/**
     * 
     * 商品数据
     * @author zbc
     * @since 2017年5月25日 下午8:02:12
     * @return
     */
    @ApiOperation(value="购物车立即发货",notes="购物车立即发货接口",httpMethod="POST",response=JsonResult.class)
    @ApiImplicitParams(
		{
			@ApiImplicitParam(name="body",required=true,dataType="dto.discart.CartSaleDto",paramType="body",value="随便传试试",
					defaultValue="")
		}
	)
    @Login
    public Result delivery(){
    	JsonNode json = request().body().asJson();
    	Logger.info("{}购物车立即发货接口参数:{}",userService.getDisAccount(),json);
    	if(JsonCaseUtil.checkParam(json, new String[]{"uid"})){
    		return ok(Json.toJson(disCartService.delivery(json.toString(), userService.getDismember())));
    	}
    	return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
    }
    
    /**
     * 提交前保存优惠信息，这里不做优惠信息处理
     * 
     * 立即结算发货
     * 是否同仓库 ？ 是： 往下 否：提示不能跨仓下单
     * 获取商品数据（包含赠品）构造商品数据
     * 返回发货单 仓库id 便于 获取物流方式 计算运费
     * 计算总价格（若包含优惠码才计算）
     * 是否包含优惠码 ? 是:减掉优惠金额 否：不做计算
     * 是否传 省id 市id 物流方式?是：计算运费 否：不做计算
     * 
     * 商品，获取运费，促销，优惠，支付金额等信息
     * @author zbc
     * @since 2017年5月25日 下午8:36:00
     * @return
     */
    @ApiOperation(value="结算发货信息",httpMethod="GET",notes="<b>前台登录校验<b/><br/>点击结算发货，初始化商品信息,无需参数",response=DeliveryInfoResult.class)
    @ApiImplicitParams({})
    @Login
    public Result getDeliveryInfo(){
    	return ok(Json.toJson(disCartService.getDeliveryInfo(userService.getDismember())));
    }
    
    /**
     * 获取发货费用信息
     * @author zbc
     * @since 2017年5月26日 上午11:58:58
     * @return
     */
    @ApiOperation(value="获取费用信息",httpMethod="POST",notes="选择省市地区，更换物流方式，应用优惠码",response=DeliveryInfoResult.class)
    @ApiImplicitParams(
		{
			@ApiImplicitParam(name="body",value="uid:发货信息uid，必传<br/>"
					+ "logisticsMode:物流名称,必传<br/>"
					+ "logisticsTypeCode:物流代码,必传<br/>"
					+ "provinceId:省id,必传<br/>"
					+ "cityId:城市id，必传<br/>"
					+ "couponsNo:优惠码,非必填",paramType="body",dataType="string",required=true,
					defaultValue="{\n"
							+ "\"uid\":\"359e192db5fe4833a0bcacfadc8c430b\",\n"
							+ "\"logisticsMode\":\"圆通快递\",\n"
							+ "\"logisticsTypeCode\":\"JYT\",\n"
							+ "\"provinceId\":1,\n"
							+ "\"cityId\":1\n"
							+ "}")
		}
	)
    @Login
    public Result getDeliveryFee(){
    	JsonNode json = request().body().asJson();
    	String account = userService.getDisAccount();
    	Logger.info("{}获取发货费用信息:{}",account,json);
    	if(JsonCaseUtil.checkParam(json, new String[]{"uid","logisticsMode","logisticsTypeCode","provinceId","cityId"})){
    		return ok(Json.toJson(disCartService.getDeliveryFee(json.toString())));
    	}
    	return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
    }
    
    /**
     * 删除已选商品
     * @author zbc
     * @since 2017年6月3日 下午4:29:27
     * @return
     */
    @ApiOperation(value="删除已选商品",httpMethod="GET",response=JsonResult.class,notes="<b>该接口有前台登录校验</b><br/>"
    		+ "删除已选商品")
    @ApiImplicitParams({})
    @Login
    public Result delSelected(){
    	return ok(Json.toJson(disCartService.delSelected(userService.getDisAccount())));
    }
}
