package mapper.product;

import java.util.List;

import entity.product.TypeBase;

public interface TypeBaseMapper{
    int deleteByPrimaryKey(Integer id);

    int insert(TypeBase record);

    int insertSelective(TypeBase record);

    TypeBase selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TypeBase record);

    int updateByPrimaryKey(TypeBase record);

	List<TypeBase> selectAll();
}