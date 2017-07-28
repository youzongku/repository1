package mapper.category;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.category.CategorySearchParamDto;
import dto.category.VirCategoryDto;
import entity.category.VirtualCategory;

/**
 * 虚拟分类
 * <p>
 * t_virtual_category
 * 
 * @author ye_ziran
 * @since 2015年12月8日 下午4:28:11
 */
public interface VirtualCategoryMapper {
	
	public final static String SEQ_NAME = "t_virtual_category_id_seq";
	
	/**
	 * 根据查询条件返回结果集
	 * 
	 * <ul>
	 * 	<li>1,主要是根据父id，拿到子类id，所以不作分页
	 * 	<li>2,虚拟类目树应该做客户端缓存，减少网络传输；
	 * 	<li>3,考虑到类目树不会经常有变化，服务端缓存整树在redis服务
	 * <ul>
	 * 
	 * 
	 * @param paramDto				查询条件
	 * @return
	 * @author ye_ziran
	 * @since 2015年12月8日 下午4:35:09
	 */
	List<VirtualCategory> queryByParam(@Param("paramDto")CategorySearchParamDto paramDto);
	
	/**
	 * 根据查询条件，获得总记录数
	 * 
	 * @param paramDto
	 * @return
	 * @author ye_ziran
	 * @since 2015年12月8日 下午4:49:26
	 */
	Integer getTotalByParam(@Param("paramDto")CategorySearchParamDto paramDto);
	
	/**
	 * 批量插入
	 * 
	 * @param virCatList
	 * @return
	 * @author ye_ziran
	 * @since 2015年11月5日 上午10:05:11
	 */
	int insertBatch(List<VirtualCategory> virCatList);
	
	/**
	 * 批量更新
	 * 
	 * @param virCatList
	 * @return
	 * @author ye_ziran
	 * @since 2015年12月8日 下午4:50:53
	 */
	int updateBatch(List<VirtualCategory> virCatList);
	
	/**
	 * 删除
	 * 
	 * @param virCat
	 * @return
	 * @author ye_ziran
	 * @since 2015年12月8日 下午4:51:40
	 */
	int delete(VirtualCategory virCat);

	List<Integer> queryChild(@Param("list")List<Integer> catIds);

	VirtualCategory select(Integer vcId);
	
	/**
	 * 查询所有虚拟类目
	 * @author zbc
	 * @since 2016年9月5日 下午4:15:17
	 */
	List<VirtualCategory> queryAll();
	
	/**
	 * 根据parentId找到所有子节点，并从下一级开始算1级
	 * 
	 * @param parentId
	 * @return
	 * @author ye_ziran
	 * @since 2017年3月28日 下午5:03:48
	 */
	List<VirCategoryDto> allSubsByParentId(Integer parentId);
}