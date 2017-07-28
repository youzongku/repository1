package handler;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTimeUtils;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.marketing.DisSpriceActivity;
import entity.marketing.DisSpriceGoods;
import event.ActOpenEvent;
import event.ContractEvent;
import event.EsRefreshEvent;
import mapper.marketing.DisSpriceActivityMapper;
import mapper.marketing.DisSpriceGoodsMapper;
import play.Logger;
import play.api.mvc.Call;
import play.libs.Json;
import services.product.IEsProductService;
import services.product.IProductBaseService;
import services.product.IQuotedService;
import util.product.HttpUtil;

/**
 * Created by LSL on 2016/7/7.
 */
public class ProductHandler {

	@Inject
	private DisSpriceActivityMapper activityMapper;

    @Inject
    private DisSpriceGoodsMapper goodsMapper;
    
    @Inject
    private IProductBaseService baseService;
    
    @Inject
    IEsProductService esService;
    
	@Inject
	private IQuotedService quotedService;
    
    /**
     * 活动开启自动任务
     */
	@Subscribe
    public void openAct(ActOpenEvent event) {
        Logger.debug("====================open activity start====================");
        List<DisSpriceActivity> list = activityMapper.findUnusedOrOpenedActivity();
        if (CollectionUtils.isNotEmpty(list)) {
            long now;
            int line;
            Map<String,Object> queryMap = Maps.newHashMap();
            for (DisSpriceActivity item : list) {
                now = DateTimeUtils.currentTimeMillis();
                if (item.getStartTime().getTime() <= now &&
                        item.getEndTime().getTime() > now) {
                    item.setActivityStatus(2);//启用中
                } else if (item.getEndTime().getTime() <= now) {
                    item.setActivityStatus(3);//已结束
                }
                queryMap.put("activityId", item.getId());
                List<DisSpriceGoods> goods = goodsMapper.findGoodsByCondition(queryMap);
                Logger.debug("openAct    [update activity status]id----->" + item.getId());
                if (CollectionUtils.isNotEmpty(goods)) {
                    line = activityMapper.updateByPrimaryKeySelective(item);
                    Logger.debug("openAct    [update activity status]status----->" + item.getActivityStatus());
                    Logger.debug("openAct    [update activity status]line----->" + line);
                } else {
                    Logger.debug("openAct    [current activity has not goods.]");
                }
            }
        }
        baseService.reloadCategory(true,true,true);
        Logger.debug("====================open activity end====================");
    }
    
    @Subscribe
    public void openEsRefresh(EsRefreshEvent event) {
    	long currTime = System.currentTimeMillis();
    	esService.initProductDatas();
    	Logger.debug("elasticsearch本次更新文档花费:{}毫秒", System.currentTimeMillis() - currTime);
    	
//    	Call call = controllers.product.routes.ProductController.indexPreRender();
//		String res = HttpUtil.get(Maps.newConcurrentMap(), HttpUtil.B2BBASEURL+call.url());
//		Logger.info("定时更新静态化首页结果：{}", Json.toJson(res));
    }
	
	@Subscribe
    public void autoOpenContract(ContractEvent event) {
		Logger.info("自动开启报价。");
		quotedService.autoOpenNotStartQuoted();
	}

}
