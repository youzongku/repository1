package controllers.marketing.promotion;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import annotation.ALogin;
import annotation.Login;
import dto.marketing.promotion.ConditionMatchResult;
import dto.marketing.promotion.FullActInstDto;
import dto.marketing.promotion.FullCondtInstDto;
import dto.marketing.promotion.FullProActDto;
import dto.marketing.promotion.OrderPromotionActivityDto;
import dto.marketing.promotion.ReturnResultDto;
import entity.marketing.promotion.ActivityInformationLog;
import entity.marketing.promotion.ConditionInstance;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.marketing.promotion.IPromotionService;
import util.marketing.promotion.ActivityCheckUtil;
import util.marketing.promotion.ResponseObject;

/**
 * 促销活动对外接口
 * 
 * @author lenovo
 *
 */
public class PromotionActivityController extends Controller {
	@Inject
	private IPromotionService promotionService;
	

	// 推送所有满足条件的促销中活动和优惠
	@BodyParser.Of(BodyParser.Json.class)
	public Result getExecutoryActivity() {
		Form<OrderPromotionActivityDto> form;
		try {
			form = Form.form(OrderPromotionActivityDto.class).bindFromRequest();
			Logger.info("" + form);
		} catch (Exception e) {
			Logger.error("" + e.toString());
			return ok(Json.toJson("参数有误！"));
		}
		OrderPromotionActivityDto dtoArg = form.get();

		// 得到所有促销中的活动以及条件和优惠
		List<FullProActDto> activeActivityList;
		if (dtoArg.getPaymentTime() == null) {
			activeActivityList = promotionService
					.getExecutoryActivity(dtoArg.getUserMode());
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(dtoArg.getPaymentTime());
			activeActivityList = promotionService
					.getPaymentTimeParticipateActivaty(dtoArg.getUserMode(),
							calendar.getTime());
		}
		Logger.info("进行中的活动数量===" + activeActivityList.size());
		Logger.info("活动详细信息===" + activeActivityList);

		// 获取匹配的活动
		List<ReturnResultDto> matchedActivityCheckDtoList = getMatchedActivityCheckDtoList(
				dtoArg, activeActivityList);

		Logger.info("满足的活动数量========" + matchedActivityCheckDtoList.size());
		Logger.info("满足的活动========" + matchedActivityCheckDtoList);
		return ok(Json.toJson(matchedActivityCheckDtoList));
	}

	/**
	 * 获取匹配的活动
	 * 
	 * @param dtoArg
	 *            外部传过来的购物数据
	 * @param activeActivityList
	 *            所有促销中的活动以及条件和优惠
	 * @return 返回匹配的活动
	 */
	private List<ReturnResultDto> getMatchedActivityCheckDtoList(
			OrderPromotionActivityDto dtoArg,
			List<FullProActDto> activeActivityList) {
		List<ReturnResultDto> result = new ArrayList<ReturnResultDto>();
		// 遍历活动
		for (FullProActDto eachFullProActDto : activeActivityList) {
			addReturnResultDto(result, eachFullProActDto,
					processOneActivityCheckDto(dtoArg, eachFullProActDto));
		}
		return result;
	}

	/**
	 * 处理一个活动
	 * 
	 * @param dtoArg
	 *            外部传过来的购物数据
	 * @param eachActivityCheckDto
	 *            单个活动
	 * @return 返回有效的活动实例
	 */
	private List<FullActInstDto> processOneActivityCheckDto(
			OrderPromotionActivityDto dtoArg, FullProActDto fullProActDto) {
		List<FullActInstDto> matchedActInstList4AProAct = new ArrayList<FullActInstDto>();
		ConditionMatchResult result = null;
		// 遍历活动的活动实例
		List<FullCondtInstDto> isMeetList = null;
		for (FullActInstDto eachFullActInstDto : fullProActDto
				.getFullActInstDtoList()) {
			//根据优先级分组
			if(eachFullActInstDto == null){
				continue;
			}
			boolean isMeet = false;
			Map<Short,List<FullCondtInstDto>> condtMap = eachFullActInstDto.getFullCondtInstDtoList().
					stream().collect(Collectors.groupingBy(e->e.getPriority()));
			Short priority = Short.MAX_VALUE;
			for(Map.Entry<Short,List<FullCondtInstDto>> entry:condtMap.entrySet()){
				Short  key = entry.getKey(); 
				result = processMatchAny(dtoArg, condtMap.get(key));
				if(result.isMatched()){
					if(key < priority){
						priority = key;
						isMeetList =  Lists.newArrayList();
						isMeetList.add(result.getFullCondtInstDto());
						eachFullActInstDto.setFullCondtInstDtoList(isMeetList);
						//change by zbc 将匹配商品存入集合中
						eachFullActInstDto.setCommodits(result.getCommoditys());
						isMeet = true;
					}
				}
			}
			if(isMeet){
				matchedActInstList4AProAct.add(eachFullActInstDto);
			}
		}
		return matchedActInstList4AProAct;
	}
	
	/**
	 * 处理满足任意条件
	 * 
	 * @param dtoArg
	 *            外部传过来的购物数据
	 * @param activeCondtInstList
	 *            条件实例集合
	 * @return 是否满足任意，true满足，false不满足
	 */
	private ConditionMatchResult processMatchAny(
			OrderPromotionActivityDto dtoArg,
			List<FullCondtInstDto> fullCondtInstDtoList) {
		//打乱顺序 即随机
		Collections.shuffle(fullCondtInstDtoList);
		List<FullCondtInstDto> workingFullCondtInstDtoList = fullCondtInstDtoList;
		ConditionMatchResult dto = null;
		int multiple = 1;
		boolean isMeetConditions = false;
		ConditionInstance condtInst = null;
		FullCondtInstDto isMeetDto = null;
		// 遍历活动实例的条件实例，每次拿到一个条件与传过来的数据进行比较
		for (FullCondtInstDto eachFullCondtInstDto : workingFullCondtInstDtoList) {
			// 判断条件实例是否满足，如果条件校验类只要有任意一个条件返回为true，跳出循环不在执行后续
			condtInst =  new ConditionInstance();
			try {
				BeanUtils.copyProperties(condtInst, eachFullCondtInstDto);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
			dto = ActivityCheckUtil.match(condtInst,eachFullCondtInstDto.getCondtInstExt(), dtoArg);
			if (dto.isMatched()) {
				isMeetConditions = true;
				multiple = dto.getMultiple();
				isMeetDto = eachFullCondtInstDto;
				break;
			}
		}
		if (dto != null) {
			dto.setMatched(isMeetConditions);
			dto.setMultiple(multiple);
			if(isMeetDto != null){
				isMeetDto.getFullPvlgInstDto().setMultiple(multiple);
			}
			dto.setFullCondtInstDto(isMeetDto);
		} else {
			dto = new ConditionMatchResult();
			dto.setMatched(false);
		}
		return dto;
	}

	/**
	 * 添加结果到result
	 * 
	 * @param result
	 *            结果集合
	 * @param activityCheckDto
	 *            满足的活动
	 * @param matchedActivityInstList
	 *            满足的活动实例集合
	 */
	private void addReturnResultDto(List<ReturnResultDto> result,
			FullProActDto activityCheckDto,
			List<FullActInstDto> matchedActivityInstList) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (matchedActivityInstList != null
				&& matchedActivityInstList.size() != 0) {
			ReturnResultDto re = new ReturnResultDto();
			re.setId(activityCheckDto.getId());
			re.setName(activityCheckDto.getName());
			re.setDescription(activityCheckDto.getDescription());
			re.setStartTime(format.format(activityCheckDto.getStartTime()));
			re.setEndTime(format.format(activityCheckDto.getEndTime()));
			re.setFullActInstDtoList(matchedActivityInstList);
			result.add(re);
		}
	}

	/**
	 * 得到推送回来的信息保存
	 * 
	 * @return
	 */
	public Result saveActivityInformationLog() {
		Form<ActivityInformationLog> form = Form.form(
				ActivityInformationLog.class).bindFromRequest();
		ActivityInformationLog dto = form.get();
		promotionService.saveActivityInformationLog(dto);

		return ok(Json.toJson(ResponseObject.newSuccessResponseObject(true)));
	}

	/**
	 * delete by zbc 
	 * 得到根据优惠ID得到活动和优惠
	 * @return
	 */
	@Deprecated
	public Result getActivityAndPvlg() {
		JsonNode node = request().body().asJson();
		Iterator<JsonNode> pglvId = node.findPath("pglvId").iterator();
		Logger.info("pglvId:" + node);
		List<Integer> idList = new ArrayList<Integer>();
		while (pglvId.hasNext()) {
			idList.add(pglvId.next().asInt());
		}
		return ok(Json.toJson(promotionService
				.getActivityByPvlg(idList)));
	}
	
	/**
	 * 前台 获取优惠
	 * @author zbc
	 *  actId                      活动ID 
	 *  OrderPromotionActivityDto  商品数据
	 * @since 2016年10月24日 下午8:28:08
	 */
	@Login
	public Result getPvlg(){
		JsonNode node = request().body().asJson();
		Map<String,Object> res = Maps.newHashMap();
		if(!node.has("actId")||!node.has("pros")){
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res).toString());
		}
		OrderPromotionActivityDto dtoArg = Json.fromJson(node.get("pros"),OrderPromotionActivityDto.class);
		// 得到所有促销中的活动以及条件和优惠
		List<FullProActDto> activeActivityList = promotionService
		.getExecutoryActivity(dtoArg.getUserMode());
		Map<Boolean,List<FullProActDto>>  meetMap = activeActivityList.stream().collect(Collectors.partitioningBy(e->e.getId() == node.get("actId").asInt()));
		activeActivityList = meetMap.get(true);
		if(activeActivityList.size() == 0){
			res.put("suc", false);
			res.put("msg", "未匹配到活动");
			return ok(Json.toJson(res).toString());
		}
		List<FullActInstDto> actInstList = processOneActivityCheckDto(dtoArg, activeActivityList.get(0));
		return ok(Json.toJson(promotionService.executePvlgInst(actInstList,dtoArg)));
	}
	
	/**
	 * 后台 选增
	 * @author zbc
	 * @since 2016年10月26日 上午10:46:37
	 */
	@ALogin
	public Result getBackPvlg(){
		JsonNode node = request().body().asJson();
		Map<String,Object> res = Maps.newHashMap();
		if(!node.has("actId")||!node.has("pros")){
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res).toString());
		}
		OrderPromotionActivityDto dtoArg = Json.fromJson(node.get("pros"),OrderPromotionActivityDto.class);
		// 得到所有促销中的活动以及条件和优惠
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(dtoArg.getPaymentTime());
		List<FullProActDto> activeActivityList = promotionService
				.getPaymentTimeParticipateActivaty(dtoArg.getUserMode(),
						calendar.getTime());
		Map<Boolean,List<FullProActDto>>  meetMap = activeActivityList.stream().collect(Collectors.partitioningBy(e->e.getId() == node.get("actId").asInt()));
		activeActivityList = meetMap.get(true);
		if(activeActivityList.size() == 0){
			res.put("suc", false);
			res.put("msg", "未匹配到活动");
			return ok(Json.toJson(res).toString());
		}
		List<FullActInstDto> actInstList = processOneActivityCheckDto(dtoArg, activeActivityList.get(0));
		return ok(Json.toJson(promotionService.executePvlgInst(actInstList,dtoArg)));
	}
	
}
