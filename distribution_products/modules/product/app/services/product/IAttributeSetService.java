package services.product;

import entity.product.store.StoreBase;

/**
 * 维护商品属性集抽象类
 * @author xuse
 * 2016年11月30日
 */
public abstract class IAttributeSetService implements IStoreBaseService{
	
	/**
	 * 维护属性集与属性之间的关系
	 * xuse
	 * 2016年11月30日
	 * @param entity
	 */
	public abstract void maintenanceAttributeRelation(StoreBase entity);

}
