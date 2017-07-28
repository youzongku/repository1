package mapper.discart;

/**
 * Created by luwj on 2015/12/01.
 */
public interface BaseMapper<T> {

	T selectByPrimaryKey(Integer id);

	int insert(T record);

	int insertSelective(T record);

	int updateByPrimaryKeySelective(T record);

	int updateByPrimaryKey(T record);

	int deleteByPrimaryKey(Integer id);

}
