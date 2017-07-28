package util.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.category.CategoryBase;
import entity.product.Warehouse;

/**
 * 缓存工具类 用具缓存一些数据
 * @author zbc
 * 2016年9月8日 下午4:27:31
 */
public class BufferUtils {
	// 缓存类目ID，Map<Integer,String> 类目Id，类目名称
	//真实类目名称map
	public static Map<Integer, CategoryBase> categoryName = new HashMap<Integer, CategoryBase>();
	//类目 对应sku map
	public static Map<Integer, List<String>> category_sku_mapping = new HashMap<Integer, List<String>>();
	//仓库Map
	public static Map<String, Warehouse> warehouse_id_mapping = new HashMap<String, Warehouse>();

	public static void clearCategorySkuMapping(){
		category_sku_mapping = new HashMap<Integer, List<String>>();
	}
	
	public static void clearWarehouseIdMapping(){
		warehouse_id_mapping = new HashMap<String, Warehouse>();
	}
	
	public static void clearCategoryName(){
		categoryName = new HashMap<Integer, CategoryBase>();
	}
}
