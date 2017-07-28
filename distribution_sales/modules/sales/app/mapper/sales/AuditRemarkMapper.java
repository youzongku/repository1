package mapper.sales;

import entity.sales.AuditRemark;

/**
 * @author zbc
 * 2016年12月22日 下午12:08:58
 */
public interface AuditRemarkMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AuditRemark record);

    int insertSelective(AuditRemark record);

    AuditRemark selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AuditRemark record);

    int updateByPrimaryKey(AuditRemark record);
    
    AuditRemark select(AuditRemark record);
}