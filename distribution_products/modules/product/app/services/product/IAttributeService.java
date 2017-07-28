package services.product;

import java.util.List;

import entity.product.store.AttrMultivalue;
import entity.product.store.AttributeType;
import entity.product.store.StoreBase;

/**
 * 维护商品属性抽象类
 * @author xuse
 * 2016年11月28日
 */
public interface IAttributeService extends IStoreBaseService{

	
	
	/**
	 * 查询属性可选值
	 * xuse
	 * 2016年11月28日
	 */
	public abstract List<AttrMultivalue> selectMaintenanceOptions(StoreBase entity);
	/**
	 * 添加属性可选值
	 * xuse
	 * 2016年11月30日
	 * @param entity
	 */
	public abstract void addMaintenanceOptions(StoreBase entity);
	/**
	 * 删除属性可选值
	 * xuse
	 * 2016年11月30日
	 * @param entity
	 */
	public abstract void deleteMaintenanceOptions(StoreBase entity);
	/**
	 * 维护BBC属性与erp属性关系，目前手动数据库维护
	 * xuse
	 * 2016年11月30日
	 * @param entity
	 * public abstract void maintenanceBbcRelation(StoreBase entity);
	 */
	
	/**
	 * 查询所有属性类型
	 * @author zbc
	 * @since 2016年12月1日 下午5:06:27
	 */
	public abstract List<AttributeType> selectAttrTypes();
	
	
}
