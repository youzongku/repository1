package mapper.dismember;

import org.apache.ibatis.annotations.Param;

import entity.dismember.Invoice;

/**
 * 分销商发票信息mapper 
 * @author zbc
 * 2017年2月8日 下午3:38:29
 */
public interface InvoiceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Invoice record);

    int insertSelective(Invoice record);

    Invoice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Invoice record);

    int updateByPrimaryKey(Invoice record);
    
    /**
     * 根据分销商账号发票信息
     * @author zbc
     * @since 2017年2月8日 下午4:13:05
     */
    int deleteByEmail(String email);
    
    /**
     * 根据分销商账号 查询 发票信息
     * @author zbc
     * @since 2017年2月8日 下午5:18:55
     */
    Invoice selectByEmail(String email);
}