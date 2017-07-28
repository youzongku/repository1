package mapper.timer;

public interface BaseMapper<T> {

    T selectByPrimaryKey(Integer id);

    int insert(T record);

    int insertSelective(T record);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
	
	int deleteByPrimaryKey(Integer id);

}
