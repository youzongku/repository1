package services.product;


import entity.product.store.StorePage;

/**
 * 商品刊登
 * @author xuse
 * 2016年11月30日
 */
public abstract class IPulishProductService implements IStoreBaseService{
	
	/* 
	 * 商品查询目前不要实现，已有
	 * (non-Javadoc)
	 * @see services.product.IStoreBaseService#search(entity.product.store.StoreBase)
	 */
	@Override
	public StorePage search(String entity) {
		return null;
	}
}
