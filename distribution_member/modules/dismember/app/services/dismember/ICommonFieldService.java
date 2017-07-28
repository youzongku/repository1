package services.dismember;

import entity.dismember.CommonField;

/**
 * 全局变量服务层操作接口，实现全局变量的增删改查。
 *
 */
public interface ICommonFieldService {
	
/**
 * 对实例变量进行保存更新操作
 * @param commonField 全局变量实例对象（包含变量信息）
 * @return
 */
public boolean saveOrUpdateField(CommonField commonField);

/**
 * 根据主键id获得全局变量实体对象
 * @param id 主键id
 * @return
 */
public CommonField getCommonFieldById(Integer id);

/**
 * 通过主键id删除全局变量
 * @param id 主键id
 * @return
 */
public boolean deleteCommonFieldById(Integer id);

/**
 * 通过变量名称获得全局变量
 * @param name
 * @return
 */
public CommonField getCommonFieldByName(String name);
}
