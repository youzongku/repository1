package services.product.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import mapper.marketing.DisSpriceActivityMapper;
import mapper.marketing.DisSpriceGoodsMapper;
import mapper.marketing.DisSpricePosterMapper;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.beans.BeanUtils;

import play.Logger;
import play.libs.Json;
import services.product.IProductBaseService;
import services.product.ISpriceService;
import valueobjects.product.Pager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.marketing.ActProDTO;
import dto.marketing.ActivityDTO;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;
import entity.marketing.DisSpriceActivity;
import entity.marketing.DisSpriceGoods;
import entity.marketing.DisSpricePoster;

/**
 * Created by LSL on 2016/7/4.
 */
public class SpriceService implements ISpriceService {

    @Inject
    private DisSpriceActivityMapper activityMapper;
    @Inject
    private DisSpriceGoodsMapper goodsMapper;
    @Inject
    private DisSpricePosterMapper posterMapper;
    @Inject
    private IProductBaseService productBaseService;

    @Override
    public String findOpenedActivities() {
        ObjectNode res = Json.newObject();
        ArrayNode items = JsonNodeFactory.instance.arrayNode();
        ActivityDTO dto = new ActivityDTO();
        dto.setActState(2);
        try {
            List<DisSpriceActivity> list = activityMapper.findActivityByCondition(dto);
            if (CollectionUtils.isNotEmpty(list)) {
                JsonNode temp;
                JsonNode node;
                for (DisSpriceActivity item : list) {
                    temp = Json.parse(this.getActInfo(item.getId()));
                    node = temp.get("pros");
                    if (node != null && node.isArray() && node.size() > 0) {
                        items.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.debug("findOpenedActivities    Exception----->", e);
        }
        res.set("acts", items);
        Logger.debug("findOpenedActivities    [Opened activity number]----->" + items.size());
        return res.toString();
    }

    @Override
    public Pager<DisSpriceActivity> findActivityByCondition(ActivityDTO dto) {
        Pager<DisSpriceActivity> pager = new Pager<DisSpriceActivity>(true, "");
        List<DisSpriceActivity> list = activityMapper.findActivityByCondition(dto);
        int count = activityMapper.findActivityCount(dto);
        pager.setCurrPage(dto.getCurrPage());
        pager.setPageSize(dto.getPageSize());
        pager.setList(list);
        pager.setTotalCount(count);
        Logger.debug("findActivityByCondition    pager----->" + Json.toJson(pager));
        return pager;
    }

    @Override
    public String saveActProduct(String paramsStr) {
        JsonNode params = Json.parse(paramsStr);
        ObjectNode res = Json.newObject();
        res.put("suc", false);
        try {
            String user = params.get("user").asText();
            List<DisSpriceGoods> list = new ObjectMapper().readValue(
                    params.get("list").toString(), new TypeReference<List<DisSpriceGoods>>() {});
            Logger.debug("saveActProduct    list----->" + params.get("list").size());
            if (list == null || list.size() == 0) {
            	res.put("msg", "关联商品为空");
            	return res.toString();
            }
            
            Integer actId = params.get("actId").asInt();
            DisSpriceActivity act = activityMapper.selectByPrimaryKey(actId);
            int line = 0, number = 0;
            DisSpriceGoods goods;
            String message = "";
            Map<String,Object> queryMap = Maps.newHashMap();
            for (DisSpriceGoods item : list) {
                queryMap.clear();
                queryMap.put("startTime", act.getStartTime());
                queryMap.put("endTime", act.getEndTime());
                queryMap.put("activityId", actId);
                queryMap.put("sku", item.getSku());
                queryMap.put("warehouseId", item.getWarehouseId());
                number = goodsMapper.getRelatedActGoodsCount(queryMap);
                Logger.debug("saveActProduct    number----->" + number);
                if (number > 0) {
                    message += "商品【 " + item.getSku() + "】已参加其他特价活动。<br/>";
                } else {
                    goods = goodsMapper.getGoodsByCondition(queryMap);
                    if (goods == null) {
                        item.setCreateUser(user);
                        item.setLimitedPurchase(false);
                        line = goodsMapper.insertSelective(item);
                        Logger.debug("saveActProduct    [insert DisSpriceGoods]line----->" + line);
                    } else {
                        item.setId(goods.getId());
                        line = goodsMapper.updateByPrimaryKeySelective(item);
                        Logger.debug("saveActProduct    [update DisSpriceGoods]line----->" + line);
                    }
                }
            }
            //更新活动商品数量
            queryMap.clear();
            Integer activityId = params.get("actId").asInt();
            queryMap.put("activityId", activityId);
            List<DisSpriceGoods> items = goodsMapper.findGoodsByCondition(queryMap);
            DisSpriceActivity activity = new DisSpriceActivity();
            activity.setId(activityId);
            activity.setActivityPnum(items.size());
            line = activityMapper.updateByPrimaryKeySelective(activity);
            Logger.debug("saveActProduct    [update DisSpriceActivity]line----->" + line);
            res.put("suc", true);
            if (Strings.isNullOrEmpty(message)) {
                res.put("msg", "选中的关联商品保存成功。");
            } else {
                res.put("msg", message + "因此上述商品未关联当前活动。");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.debug("saveActProduct    Exception----->", e);
            res.put("msg", "保存关联商品发生异常");
        }
        return res.toString();
    }

    @Override
    public String openActivity(Integer id) {
        ObjectNode res = Json.newObject();
        res.put("suc", false);
        DisSpriceActivity activity = activityMapper.selectByPrimaryKey(id);
        if (activity == null) {
        	res.put("msg", "当前活动不存在。");
            return res.toString();
        }
        
        if (activity.getActivityStatus() == 2) {
            res.put("msg", "当前活动已经处于启用状态。");
            return res.toString();
        } 
        if (activity.getActivityStatus() == 3) {
            res.put("msg", "当前活动已结束。");
            return res.toString();
        } 
        if (activity.getActivityStatus() == 4) {
            res.put("msg", "当前活动已禁止。");
            return res.toString();
        }

        if (activity.getActivityStatus() == 1) {
            long now = DateTimeUtils.currentTimeMillis();
            if (activity.getStartTime().getTime() > now) {
                res.put("msg", "当前活动开始时间未到，无法开启。");
            } else if (activity.getStartTime().getTime() <= now &&
                    activity.getEndTime().getTime() > now) {
                Map<String,Object> queryMap = Maps.newHashMap();
                queryMap.put("activityId", activity.getId());
                List<DisSpriceGoods> goods = goodsMapper.findGoodsByCondition(queryMap);
                if (CollectionUtils.isNotEmpty(goods)) {
                    int number = 0;
                    for (DisSpriceGoods item : goods) {
                        queryMap.clear();
                        queryMap.put("sku", item.getSku());
                        queryMap.put("warehouseId", item.getWarehouseId());
                        number += goodsMapper.getOpendActGoodsCount(queryMap);
                    }
                    if (number == 0) {
                        activity.setActivityStatus(2);//启用中
                        int line = activityMapper.updateByPrimaryKeySelective(activity);
                        Logger.debug("openActivity    line----->" + line);
                        res.put("suc", true);
                    } else {
                        res.put("msg", "当前活动所关联的商品已在现有开启的活动中，因此当前活动无法开启。");
                    }
                } else {
                    res.put("msg", "当前活动未关联商品，无法开启。");
                }
            } else if (activity.getEndTime().getTime() <= now) {
                activity.setActivityStatus(3);//已结束
                int line = activityMapper.updateByPrimaryKeySelective(activity);
                Logger.debug("openActivity    line----->" + line);
                res.put("msg", "当前活动已结束。");
            }
        }
        return res.toString();
    }

    @Override
    public String getActInfo(Integer actId) {
        Logger.debug("====================getActInfo start====================");
        ObjectNode res = Json.newObject();
        ObjectNode info = Json.newObject();
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        try {
            DisSpriceActivity activity = activityMapper.selectByPrimaryKey(actId);
            if (activity == null) {
                info.set("act", array);
            } else {
                info.set("act", Json.toJson(activity));
            }
            Logger.debug("getActInfo    act----->" + info.get("act").toString());

            Map<String,Object> queryMap = Maps.newHashMap();
            queryMap.put("activityId", actId);
            List<DisSpricePoster> poster = posterMapper.findPosterByCondition(queryMap);
            if (CollectionUtils.isEmpty(poster)) {
                info.set("imgs", array);
            } else {
                info.set("imgs", Json.toJson(poster));
            }
            Logger.debug("getActInfo    imgs----->" + info.get("imgs").size());

            List<DisSpriceGoods> goods = goodsMapper.findGoodsByCondition(queryMap);
            if (CollectionUtils.isEmpty(goods)) {
                info.set("pros", array);
            } else {
                List<ActProDTO> pros = Lists.newArrayList();
                ActProDTO pro;
                ProductSearchParamDto dto;
                List<ProductLite> lites;
                ProductLite lite;
                for (DisSpriceGoods item : goods) {
                    pro = new ActProDTO();
                    BeanUtils.copyProperties(item, pro);
                    dto = new ProductSearchParamDto();
                    dto.setWarehouseId(item.getWarehouseId());
                    dto.setSku(item.getSku());
                    dto.setIstatus(1);
                    lites = productBaseService.getProducts(dto, 1, 1);
                    if (CollectionUtils.isNotEmpty(lites)) {
                        lite = lites.get(0);
                        BeanUtils.copyProperties(lite, pro);
                    }
                    pros.add(pro);
                }
                info.set("pros", Json.toJson(pros));
            }
            res.put("suc", true);
            Logger.debug("getActInfo    pros----->" + info.get("pros").size());
        } catch (Exception e) {
            e.printStackTrace();
            res.put("suc", false);
            res.put("msg", "系统异常");
            Logger.error("getActInfo    Exception----->", e);
        }
        Logger.debug("====================getActInfo end====================");
        res.set("info", info);
        return res.toString();
    }
    
	@Override
	public int insertActivity(DisSpriceActivity activity) {
		return activityMapper.insertSelective(activity);
	}

	@Override
	public int updateActivity(DisSpriceActivity activity) {
		return activityMapper.updateByPrimaryKeySelective(activity);
	}

	@Override
	public DisSpriceActivity selectActivity(Integer id) {
		return activityMapper.selectByPrimaryKey(id);
	}

	@Override
	public int insertPoster(DisSpricePoster poster) {
		return posterMapper.insertSelective(poster);
	}

	@Override
	public DisSpricePoster selectPoster(Integer id) {
		return posterMapper.selectByPrimaryKey(id);
	}

	@Override
	public int deleteSpriceGoods(Integer id) {
		return goodsMapper.deleteByPrimaryKey(id);
	}

    @Override
    public String deletePoster(Integer id) {
        ObjectNode res = Json.newObject();
        DisSpricePoster poster = posterMapper.selectByPrimaryKey(id);
        if (poster != null) {
            File file = new File(poster.getImageUrl());
            if (file.exists()) {
                boolean mark = file.delete();
                Logger.debug("deletePoster    [delete poster file]mark----->" + mark);
            }
            int line = posterMapper.deleteByPrimaryKey(id);
            Logger.debug("deletePoster    [delete DisSpricePoster]line----->" + line);
        } else {
            Logger.debug("deletePoster    [DisSpricePoster(id=" + id + ") is not exist.]");
        }
        res.put("suc", true);
        return res.toString();
    }
}
