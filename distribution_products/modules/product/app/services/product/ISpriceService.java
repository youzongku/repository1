package services.product;

import dto.marketing.ActivityDTO;
import entity.marketing.DisSpriceActivity;
import entity.marketing.DisSpricePoster;
import valueobjects.product.Pager;

/**
 * Created by LSL on 2016/7/4.
 */
public interface ISpriceService {

    /**
     * 获取所有已开启的特价活动
     */
    String findOpenedActivities();

    /**
     * 多条件分页获取特价活动
     */
    Pager<DisSpriceActivity> findActivityByCondition(ActivityDTO dto);

    /**
     * 保存特价活动商品
     */
    String saveActProduct(String paramsStr);

    /**
     * 开启指定特价活动
     */
    String openActivity(Integer id);

    /**
     * 获取指定活动所有信息
     */
    String getActInfo(Integer actId);

	int  insertActivity(DisSpriceActivity activity);

	int updateActivity(DisSpriceActivity activity);

	DisSpriceActivity selectActivity(Integer id);

	int insertPoster(DisSpricePoster poster);

	DisSpricePoster selectPoster(Integer id);

	int deleteSpriceGoods(Integer id);

    /**
     * 删除指定海报图片
     * @Author LSL on 2016-09-26 15:08:07
     */
    String deletePoster(Integer id);

}
