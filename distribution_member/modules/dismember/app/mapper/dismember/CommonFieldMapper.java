package mapper.dismember;

import org.apache.ibatis.annotations.Param;

import entity.dismember.CommonField;

/**
 * 
 *全局变量映射类
 *
 */
public interface CommonFieldMapper extends BaseMapper<CommonField> {

public CommonField selectByName(@Param("name")String name);
}