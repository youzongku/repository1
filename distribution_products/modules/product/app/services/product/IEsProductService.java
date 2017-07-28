package services.product;

import java.util.List;

import component.elasticsearch.ProductLiteDoc;
import dto.product.PageResultDto;
import dto.product.ProductLite;
import dto.product.ProductSearchParamDto;

/**
 * 索引操作定义
 * @author huangjc
 * @date 2016年11月15日
 */
public interface IEsProductService{
	/**
	 * 创建索引时，会同时设置type的mapping
	 * @return
	 */
	public boolean createProductIndex();

	/**
	 * 初始化数据
	 */
	public void initProductDatas();
	
	/**
	 * es文档批量更新接口
	 * 
	 * @param prodLites
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月2日 下午6:15:35
	 */
	public boolean update(List<ProductLiteDoc> prodLites);
	
	/**
	 * es文档更新接口
	 * 
	 * @param prodLites
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月2日 下午6:15:35
	 */
	public boolean update(ProductLiteDoc prodLite);
	
	/**
	 * 从es拿到实体
	 * 
	 * @param lite
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月3日 上午10:09:18
	 */
	public ProductLiteDoc getProdDocFromEs(ProductLite lite);
	
	/**
	 * 从es查询商品数据
	 * 
	 * @param searchDto
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月8日 下午2:41:33
	 */
	PageResultDto products(ProductSearchParamDto searchDto);
	
	/**
	 * 删除文档
	 * 
	 * @param doc
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月8日 下午4:17:09
	 */
	public boolean delete(String id);
	
}
