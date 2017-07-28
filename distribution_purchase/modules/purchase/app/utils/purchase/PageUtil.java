package utils.purchase;

/**
 * 分页工具类
 * @author huangjc
 * @date 2016年12月17日
 */
public final class PageUtil {
	private PageUtil(){}
	
	/**
	 * 计算总页数
	 * @param totalCount
	 * @param pageSize
	 * @return
	 */
	public static int calculateTotalPage(int totalCount, int pageSize){
		int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
		return totalPage;
	}
}
