package mapper.dismember;

import entity.dismember.PhoneVerify;

import java.util.List;
import java.util.Map;

/**
 * Created by luwj on 2016/9/28.
 */
public interface PhoneVerifyMapper {

    /**
     * 保存
     * @param phoneVerify :实体
     * @return : int
     */
    public int insert(PhoneVerify phoneVerify);

    /**
     * 通过id查询
     * @param id : id
     * @return : PhoneVerify
     */
    public PhoneVerify selectByPrimaryKey(Integer id);

    /**
     * 通用查询
     * @param map : 条件
     * @return : list
     */
    public List<PhoneVerify> getRecords(Map map);

    /**
     * 查询最近一条记录
     * @param map : 条件
     * @return : PhoneVerify
     */
    public PhoneVerify getRecord(Map map);

    /**
     * 更新
     * @param phoneVerify ： 实体
     * @return ： int
     */
    public int updateByPrimaryKey(PhoneVerify phoneVerify);
}
