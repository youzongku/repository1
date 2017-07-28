package services.product;

import java.util.List;
import java.util.Map;

import entity.product.store.BbcAttribute;
import entity.product.store.StoreBase;

/**
 * 目前商品分类由erp推送，CRUD接口暂时不需要实现，只需要实现维护分类与属性集关系
 * @author xuse
 * 2016年11月30日
 */
public abstract class ICategoryService implements IStoreBaseService{

	/**
	 * 维护属性集与分类关系
	 * xuse
	 * 2016年11月30日
	 * @param entity
	 */
	public abstract void maintenanceCateSetRelation(StoreBase entity);
	
	/**
	 * 根据类目Id查询BBC属性
	 * xuse
	 * 2016年11月30日
	 * @param categoryId
	 * @return
	 */
	public abstract List<BbcAttribute> searchBbcAttribute(Integer categoryId);
	
	@Override
	public Map<String, Object> create(StoreBase entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> delete(StoreBase entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> update(StoreBase entity) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
