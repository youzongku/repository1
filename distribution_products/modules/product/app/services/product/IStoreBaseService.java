package services.product;

import java.util.Map;

import entity.product.store.StoreBase;
import entity.product.store.StorePage;

/**
 * 商品库base接口，仅提供增删改查，具体针对某个类型，则使用抽象类增加
 * @author xuse
 * 2016年11月28日
 */
public interface IStoreBaseService {
	
	
	/**
	 * xuse
	 * 2016年11月30日
	 * @param entity
	 * @return
	 */
	public Map<String, Object> create(StoreBase entity);
	/**
	 * xuse
	 * 2016年11月30日
	 * @param entity
	 * @return
	 */
	public Map<String, Object> delete(StoreBase entity);
	/**
	 * xuse
	 * 2016年11月30日
	 * @param entity
	 * @return
	 */
	public Map<String, Object> update(StoreBase entity);
	/**
	 * xuse
	 * 2016年11月30日
	 * @param entity
	 * @return
	 */
	public StorePage search(String entity);

}
