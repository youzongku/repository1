package mapper.dismember;

/**
 * 通用数据库操作方法（增、删、改、查）
 * @author luwj
 *
 * @param <T>
 */
public interface BaseMapper<T> {

    T selectByPrimaryKey(Integer id);

    int insert(T record);

    int insertSelective(T record);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
	
	int deleteByPrimaryKey(Integer id);

}
