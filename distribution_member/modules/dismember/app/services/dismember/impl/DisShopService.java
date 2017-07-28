package services.dismember.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;
import dto.dismember.ShopDto;
import entity.dismember.DisShop;
import entity.dismember.DisShopDpLog;
import entity.dismember.OrderConfig;
import entity.dismember.ShopCategory;
import entity.dismember.ShopPlatform;
import mapper.dismember.DisShopDpLogMapper;
import mapper.dismember.DisShopMapper;
import mapper.dismember.OrderConfigMapper;
import mapper.dismember.ShopCategoryMapper;
import mapper.dismember.ShopPlatformMapper;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import services.dismember.IDisShopService;
import services.dismember.ISequenceService;
import utils.dismember.DateUtils;
import utils.dismember.HttpUtil;
import utils.dismember.IDUtils;


/**
 * Created by LSL on 2015/12/16.
 */
public class DisShopService implements IDisShopService {

	@Inject
	private ISequenceService sequenceService;
    @Inject
    private DisShopMapper disShopMapper;
    @Inject
    private ShopPlatformMapper shopPlatformMapper;
    @Inject
    private ShopCategoryMapper shopCategoryMapper;
    @Inject
    private OrderConfigMapper configMapper;
    @Inject
    private DisShopDpLogMapper disShopDpLogMapper;
    
    @Override
    public List<DisShopDpLog> getDisShopDpLogs(int shopId, String email){
    	List<DisShopDpLog> logs = disShopDpLogMapper.select(shopId, email);
    	return logs;
    }
    
    @Override
    public Map<String,Object> setShopDeductionPoints(String email, Map<Integer, Double> shopId2Dp, final String createUser){
    	Map<String,Object> result = Maps.newHashMap();
    	Set<Integer> ids = shopId2Dp.keySet();
    	List<DisShop> shopList = disShopMapper.selectByIds(Lists.newArrayList(ids));
    	if(CollectionUtils.isEmpty(shopList)){
    		result.put("suc", false);
    		result.put("msg", "要设置店铺扣点的店铺无效");
    		return result;
    	}
    	long invalidatedShopCount = shopList.stream().filter(shop -> !shop.getEmail().equals(email)).count();
    	if(invalidatedShopCount>0){
    		result.put("suc", false);
    		result.put("msg", "部分要设置店铺扣点的店铺无效");
    		return result;
    	}
    	
    	final Date now = new Date();// 设置时间
    	// 设置店铺扣点
    	for(DisShop shop : shopList){
    		shop.setDeductionPoints(shopId2Dp.get(shop.getId()));
    	}
    	
    	int count = disShopMapper.batchUpdateDeductionPointsById(shopList);

		Logger.info("---------------->count:{}, size:{}, shopList:{}", count, shopId2Dp.size(), shopList.size());
    	if(count < 1){
    		result.put("suc", false);
    		result.put("msg", "设置店铺扣点失败");
    		return result;
    	}
    	
    	// 添加日志
		List<DisShopDpLog> dpLogs = shopList.stream().map(shop -> {
			return new DisShopDpLog(shop.getId(), shop.getShopName(), shop.getEmail(), shop.getDeductionPoints(), createUser, now);
		}).collect(Collectors.toList());
    	disShopDpLogMapper.batchInsert(dpLogs);
    	
    	result.put("suc", true);
		result.put("msg", "设置店铺扣点成功");
		return result;
    }

    @Override
    public int getCountByCondition(Map<String, Object> params) {
        return disShopMapper.getCountByCondition(params);
    }

    @Override
    public List<ShopDto> getShopsByCondition(Map<String, Object> params) {
        Logger.debug("getShopsByCondition params-->" + Json.toJson(params).toString());
        List<DisShop> disShops = disShopMapper.getDisShopsByCondition(params);
        List<ShopDto> shopDtos = Lists.newArrayList();
        if (disShops != null && disShops.size() > 0) {
            for (DisShop disShop : disShops) {
                ShopDto shopDto = new ShopDto();
                shopDto.setId(disShop.getId());
                shopDto.setName(disShop.getShopName() == null ? "" : disShop.getShopName());
                shopDto.setUri(disShop.getShopUrl() == null ? "" : disShop.getShopUrl());
                shopDto.setPfid(disShop.getPlatformId());
                
                //都云涛添加下列五行
                shopDto.setProvinceName(disShop.getProvinceName());
                shopDto.setCityName(disShop.getCityName());
                shopDto.setAreaName(disShop.getAreaName());
                shopDto.setProvinceId(disShop.getProvinceId());
                shopDto.setCityId(disShop.getCityId());
                shopDto.setAreaId(disShop.getAreaId());
                
                shopDto.setAddr(disShop.getAddr());
                shopDto.setKeeperName(disShop.getKeeperName());
                shopDto.setShroffAccountNumber(disShop.getShroffAccountNumber());
                shopDto.setCreateDate(disShop.getCreateDate());
                shopDto.setCreateDateStr(DateUtils.date2string(disShop.getCreateDate(), DateUtils.FORMAT_FULL_DATETIME));
                if (disShop.getPlatformId() == 10) {
                    shopDto.setType(disShop.getOtherPlatform());
                } else {
                    shopDto.setType(shopPlatformMapper.selectByPrimaryKey(disShop.getPlatformId()).getShopPlatform());
                }
                ShopCategory sc = shopCategoryMapper.selectByPrimaryKey(disShop.getCategoryId());
                shopDto.setCate(sc == null ? "" : sc.getShopCategory());
                shopDto.setTel(disShop.getTelphone() == null ? "" : disShop.getTelphone());
                
                shopDto.setIdcard(disShop.getIdcard());//身份证
                shopDto.setZipCode(disShop.getZipCode());//邮编
                shopDto.setParentId(disShop.getParentId());
                shopDto.setClientid(disShop.getClientid());
                shopDto.setClientsecret(disShop.getClientsecret());
                shopDto.setShopAccount(disShop.getShopAccount());
				shopDto.setShopDeductionPoints(disShop.getDeductionPoints());
                shopDtos.add(shopDto);
            }
        }
        return shopDtos;
    }

    @Override
    public DisShop addNewShop(Map<String, String> params,Integer distributionType) {
        Logger.debug("addNewShop params-->" + params.toString());
        DisShop shop = new DisShop();
        Integer platformId = params.containsKey("type") && !Strings.isNullOrEmpty(params.get("type")) ? Integer.valueOf(params.get("type")) : null;
        shop.setEmail(params.containsKey("email") && !Strings.isNullOrEmpty(params.get("email")) ? params.get("email") : null);
        shop.setShopName(params.containsKey("name") && !Strings.isNullOrEmpty(params.get("name")) ? params.get("name") : null);
        shop.setShopUrl(params.containsKey("uri") && !Strings.isNullOrEmpty(params.get("uri")) ? params.get("uri") : null);
        shop.setTelphone(params.containsKey("tel") && !Strings.isNullOrEmpty(params.get("tel")) ? params.get("tel") : null);
        shop.setPlatformId(platformId);
        shop.setCategoryId(1);
        shop.setOtherPlatform(params.containsKey("other") && !Strings.isNullOrEmpty(params.get("other")) ? params.get("other") : null);
        
        //都云涛新增字段
        shop.setProvinceId(params.containsKey("provinceId") && !Strings.isNullOrEmpty(params.get("provinceId")) ? Integer.parseInt(params.get("provinceId")) : null);
        shop.setCityId(params.containsKey("cityId") && !Strings.isNullOrEmpty(params.get("cityId")) ? Integer.parseInt(params.get("cityId")) : null);
        shop.setAreaId(params.containsKey("areaId") && !Strings.isNullOrEmpty(params.get("areaId")) ? Integer.parseInt(params.get("areaId")) : null);
        shop.setProvinceName(params.containsKey("provinceName") && !Strings.isNullOrEmpty(params.get("provinceName")) ? params.get("provinceName") : null);
        shop.setCityName(params.containsKey("cityName") && !Strings.isNullOrEmpty(params.get("cityName")) ? params.get("cityName") : null);
        shop.setAreaName(params.containsKey("areaName") && !Strings.isNullOrEmpty(params.get("areaName")) ? params.get("areaName") : null);
        shop.setAddr(params.containsKey("addr") && !Strings.isNullOrEmpty(params.get("addr")) ? params.get("addr") : null);
        shop.setKeeperName(params.containsKey("keeperName") && !Strings.isNullOrEmpty(params.get("keeperName")) ? params.get("keeperName") : null);
        //添加收款账号
        shop.setShroffAccountNumber(params.containsKey("shroffAccountNumber") && !Strings.isNullOrEmpty(params.get("shroffAccountNumber")) ? params.get("shroffAccountNumber") : null);
        //身份证和邮编
        shop.setIdcard(params.containsKey("idcard") && !Strings.isNullOrEmpty(params.get("idcard")) ? params.get("idcard") : null);
        shop.setZipCode(params.containsKey("zipCode") && !Strings.isNullOrEmpty(params.get("zipCode")) ? params.get("zipCode") : null);
        shop.setParentId(params.containsKey("parentId") && !Strings.isNullOrEmpty(params.get("parentId")) ? Integer.valueOf(params.get("parentId")) : null);
        shop.setClientid(params.containsKey("clientid") && !Strings.isNullOrEmpty(params.get("clientid")) ? params.get("clientid") : null);
        shop.setClientsecret(params.containsKey("clientsecret") && !Strings.isNullOrEmpty(params.get("clientsecret")) ? params.get("clientsecret") : null);
        shop.setShopAccount(params.containsKey("shopAccount") && !Strings.isNullOrEmpty(params.get("shopAccount")) ? params.get("shopAccount") : null);
        if(platformId != null && platformId == 12) {
        	shop.setShopNo(generateShopNo(distributionType));        	
        }
        int line = disShopMapper.insertSelective(shop);
        Logger.debug("addNewShop line-->" + line);
        //如果线下店铺有增加旧推送到门店系统
        if (shop != null && shop.getPlatformId() !=null && shop.getPlatformId() == 12) {
        	//推送到门店系统
        	pushOfflineShop(shop,1);
        }
        return line == 1 ? shop : null;
    }
    
    private String generateShopNo(Integer distributionType) {
		return IDUtils.getShopNo("MD" + Constant.STORETYPE.get(distributionType),sequenceService.selectNextValue(Constant.SHOP_NO_SEQ));
	}

	/**
     * 判断店铺应用ID是否重复
     * @return
     */
    @Override
	public boolean isRepeatClientid(Map<String, String> params) {
		if (params.containsKey("sid")) {
			if(params.containsKey("clientid")){
				Integer sid = Integer.valueOf(params.get("sid"));
				Map<String, Object> map = Maps.newHashMap();
				map.put("clientid", params.get("clientid"));
				List<DisShop> list = disShopMapper.getDisShopsByCondition(map);
				return list.size() == 0?false:
					list.size() == 1?!list.get(0).getId().equals(sid):true;
			}
		} else {
			if (params.containsKey("clientid")) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("clientid", params.get("clientid"));
				if (disShopMapper.getCountByCondition(map) > 0) {
					return true;
				}
			}
		}
		return false;
	}
    
    @Override
    public boolean updateShop(Map<String, String> params,Integer distributionType) {
        Logger.debug("updateShop params-->" + params.toString());
        String email = params.containsKey("email") && !Strings.isNullOrEmpty(params.get("email")) ? params.get("email") : null;
        Integer sid = params.containsKey("sid") && !Strings.isNullOrEmpty(params.get("sid")) ? Integer.valueOf(params.get("sid")) : null;
        DisShop shop = disShopMapper.selectByPrimaryKey(sid);
        if (shop == null) {
            Logger.debug("指定店铺不存在");
            return false;
        }
        if (!email.equals(shop.getEmail())) {
            Logger.debug("要更新的指定店铺与当前登录用户不匹配");
            return false;
        }
        
        shop.setShopName(params.containsKey("name") && !Strings.isNullOrEmpty(params.get("name")) ? params.get("name") : null);
        shop.setShopUrl(params.containsKey("uri") && !Strings.isNullOrEmpty(params.get("uri")) ? params.get("uri") : "");
        shop.setTelphone(params.containsKey("tel") && !Strings.isNullOrEmpty(params.get("tel")) ? params.get("tel") : null);
        shop.setPlatformId(params.containsKey("type") && !Strings.isNullOrEmpty(params.get("type")) ? Integer.valueOf(params.get("type")) : null);
        shop.setOtherPlatform(params.containsKey("other") && !Strings.isNullOrEmpty(params.get("other")) ? params.get("other") : null);
        
        //都云涛新增字段
        shop.setProvinceId(params.containsKey("provinceId") && !Strings.isNullOrEmpty(params.get("provinceId")) ? Integer.parseInt(params.get("provinceId")) : null);
        shop.setCityId(params.containsKey("cityId") && !Strings.isNullOrEmpty(params.get("cityId")) ? Integer.parseInt(params.get("cityId")) : null);
        shop.setAreaId(params.containsKey("areaId") && !Strings.isNullOrEmpty(params.get("areaId")) ? Integer.parseInt(params.get("areaId")) : null);
        shop.setProvinceName(params.containsKey("provinceName") && !Strings.isNullOrEmpty(params.get("provinceName")) ? params.get("provinceName") : null);
        shop.setCityName(params.containsKey("cityName") && !Strings.isNullOrEmpty(params.get("cityName")) ? params.get("cityName") : null);
        shop.setAreaName(params.containsKey("areaName") && !Strings.isNullOrEmpty(params.get("areaName")) ? params.get("areaName") : null);
        shop.setAddr(params.containsKey("addr") && !Strings.isNullOrEmpty(params.get("addr")) ? params.get("addr") : null);
        shop.setKeeperName(params.containsKey("keeperName") && !Strings.isNullOrEmpty(params.get("keeperName")) ? params.get("keeperName") : null);
        //添加收款账号
        shop.setShroffAccountNumber(params.containsKey("shroffAccountNumber") ? params.get("shroffAccountNumber") : null);
        
        //身份证和邮编
        shop.setIdcard(params.containsKey("idcard") ? params.get("idcard") : null);
        shop.setZipCode(params.containsKey("zipCode") ? params.get("zipCode") : null);

        //店铺授权应用参数
        shop.setClientid(params.containsKey("clientid") && !Strings.isNullOrEmpty(params.get("clientid"))?params.get("clientid"): null);
        shop.setClientsecret(params.containsKey("clientsecret") && !Strings.isNullOrEmpty(params.get("clientsecret"))?params.get("clientsecret"): null);
        shop.setRedirecturi(params.containsKey("redirecturi") && !Strings.isNullOrEmpty(params.get("redirecturi"))?params.get("redirecturi"): null);
        shop.setShopAccount(params.containsKey("shopAccount") && !Strings.isNullOrEmpty(params.get("shopAccount"))?params.get("shopAccount"): null);
        boolean flag = disShopMapper.updateByPrimaryKeySelective(shop)>0;
        Logger.debug("updateShop line-->" + flag);
        if(flag){
        	 //如果线下店铺有修改推送到门店系统
            if (shop != null && shop.getPlatformId() !=null && shop.getPlatformId() == 12) {
            	if(StringUtils.isEmpty(shop.getShopNo())) {
            		shop.setShopNo(generateShopNo(distributionType));
            		disShopMapper.updateByPrimaryKeySelective(shop);
            	}
            	//推送到门店系统
            	if (params.containsKey("oldType") && shop.getPlatformId() != Integer.parseInt(params.get("oldType"))) {//说明是由其他类型转为线下
            		pushOfflineShop(shop,1);
            	} else {
            		pushOfflineShop(shop,2);
            	}
            }
        }
        return flag;
    }

    @Override
    public boolean deleteShop(Map<String, String> params) {
        Logger.debug("deleteShop params-->" + params.toString());
        String email = params.containsKey("email") && !Strings.isNullOrEmpty(params.get("email")) ? params.get("email") : null;
        Integer sid = params.containsKey("sid") && !Strings.isNullOrEmpty(params.get("sid")) ? Integer.valueOf(params.get("sid")) : null;
        DisShop shop = disShopMapper.selectByPrimaryKey(sid);
        if (shop == null) {
            Logger.debug("指定店铺不存在");
            return false;
        }
        if (!email.equals(shop.getEmail())) {
            Logger.debug("要删除的指定店铺与当前登录用户不匹配");
            return false;
        }
        boolean flag = disShopMapper.deleteByPrimaryKey(sid)>0;
        if (flag){//删除此店下的分店
        	disShopMapper.deleteByParentid(sid);
        }
        Logger.debug("deleteShop line-->" + flag);
        return flag;
    }
    
    @Override
	public DisShop getShop(Map<String, String> params) {
    	Integer sid = params.containsKey("sid") && !Strings.isNullOrEmpty(params.get("sid")) ? Integer.valueOf(params.get("sid")) : null;
        return disShopMapper.selectByPrimaryKey(sid);
	}

    @Override
    public List<ShopPlatform> getAllShopPlatforms() {
        return shopPlatformMapper.getAllShopPlatforms();
    }

    @Override
    public List<ShopCategory> getAllShopCategorys() {
        return shopCategoryMapper.getAllShopCategorys();
    }

	@Override
	public Map<String, Object> shop(String name,String email) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("shopName", name);
		map.put("email", email);
		map.put("pageSize", 1);
		map.put("startNum", 0);
		DisShop shop = null;
		List<DisShop> list = disShopMapper.getDisShopsByCondition(map);
		if(null != list && list.size() > 0) {
			shop = list.get(0);
		}
		map.put("suc", true);
		map.put("shop", shop == null ? new DisShop() : shop);
		return map;
	}

	@Override
	public Map<String, Object> checkShopName(Map<String, Object> params) {
		Map<String, Object> result = Maps.newHashMap();
		params.put("type", Integer.parseInt((String) params.get("type")));
		params.put("pageSize", 1);
		params.put("startNum", 0);
		Logger.info("params------->" + Json.toJson(params));
		List<DisShop> shops = disShopMapper.getDisShopsByCondition(params);
		if (shops != null && shops.size() > 0) {
			result.put("suc", true);
			result.put("data", shops.get(0));
			return result;
		}
		result.put("suc", false);
		result.put("code", 1);//没有查到店铺信息
		return result;
	}

	@Override
	/**
	 * @author Administrator
	 * @param  shop : 店铺实体
	 *         type ： 1，增加操作     2，修改操作
	 */
	public void pushOfflineShop(DisShop shop,Integer type) {
		try {
			JsonNode params = Json.toJson(shop);
			Configuration config = null;
			String url = "";
			String res = "";
			if (type == 1) {
				config = Play.application().configuration().getConfig("store");
				url = config.getString("changeStore") + "/store/addr/add";
				res = utils.dismember.HttpUtil.httpPost(params.toString(), url);
				Logger.info("线下店铺增加推送结果---------》" + Json.toJson(res));
			} else {
				config = Play.application().configuration().getConfig("store");
				url = config.getString("changeStore") + "/store/addr/modify";
				res = utils.dismember.HttpUtil.httpPost(params.toString(), url);
				Logger.info("线下店铺修改推送结果---------》" + Json.toJson(res));
			}
		} catch (Exception e) {
			Logger.error("更新门店数据异常", e);	
		}
	}

	@Override
	public Map<String, Object> query(JsonNode node) {
		Map<String, Object> res = Maps.newHashMap();
		DisShop record = new DisShop();
		record.setId(node.get("shopId").asInt());
		record.setClientid(node.get("clientid").asText());
		record.setClientsecret(node.get("clientsecret").asText());
		disShopMapper.updateByPrimaryKeySelective(record);
		Map<String, Object> params = Maps.newHashMap();
		params.put("shopId", record.getId());
		params.put("pageSize", 1);
		params.put("startNum", 0);
		OrderConfig config = new OrderConfig();
		config.setConfigType("code");
		config.setType(node.get("platform").asInt());//平台
		OrderConfig codeConfig = configMapper.select(config);
		String url = codeConfig.getTokenUrl();
		String redirect = HttpUtil.getHostUrl() + "/member/callbackSuccess";
		url += "?response_type=code&client_id=" + record.getClientid() + ""
				+ "&redirect_uri=" + redirect + "&state=" + record.getId();
		Logger.info("获取授权URL：" + url);
		res.put("success", true);
		res.put("msg", url);
		return res;
	}

	@Override
	public boolean getAccessToken(String code, String state) {
		try {
			ObjectMapper obj = new ObjectMapper();
			Logger.info("店铺授权回调参数code【"+code+"】,【"+state+"】");
			Integer shopId = Integer.valueOf(state);
			DisShop record = disShopMapper.selectByPrimaryKey(shopId);

			OrderConfig config = new OrderConfig();
			config.setConfigType("access_token");
			config.setType(record.getPlatformId());// 平台
			OrderConfig tokenConfig = configMapper.select(config);
			
			Map<String, String> params = Maps.newHashMap();
			params.put("grant_type", tokenConfig.getGrantType());
			params.put("client_id", record.getClientid());
			params.put("client_secret", record.getClientsecret());
			params.put("redirect_uri", HttpUtil.getHostUrl() + "/member/callbackSuccess");
			params.put("code", code);
			params.put("state", state);
			Logger.info(state + "获取Token参数:" + Json.toJson(params).toString());
			String response = HttpUtil.get(params,tokenConfig.getTokenUrl());
			Logger.info("返回值：" + response);
			JsonNode objNode = obj.readTree(response);
			//授权码
			record.setCode(code);
			//令牌
			record.setAccesstoken(objNode.get("access_token").asText());
			record.setRefreshtoken(objNode.get("refresh_token").asText());
			Date date = new Date();
			//生成时间
			record.setCreatetime(date);
			record.setUpdatetime(date);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.YEAR, 1);
			//失效时间
			record.setEndtime(c.getTime());
			disShopMapper.updateByPrimaryKeySelective(record);
			return true;
		} catch (Exception e) {
			Logger.error("", e);
			return false;
		}
	}

	@Override
	public List<DisShop> getAllShop(String email) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("email", email);
		Logger.info("--------------->email:{}", email);
		return disShopMapper.getDisShopsByCondition(params);
	}

}
