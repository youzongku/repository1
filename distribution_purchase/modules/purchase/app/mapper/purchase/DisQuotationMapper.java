package mapper.purchase;


import java.util.List;
import java.util.Map;

import entity.purchase.DisQuotation;
/**
 * 分销商报价单mapper
 * @author huangjc
 * @date 2016年11月22日
 */
public interface DisQuotationMapper {

    DisQuotation selectByPrimaryKey(Integer id);

    int insert(DisQuotation record);

    int insertSelective(DisQuotation record);

    int updateByPrimaryKeySelective(DisQuotation record);

    int updateByPrimaryKey(DisQuotation record);

    List<DisQuotation> getRecord(Map<String,Object> map);

    int getRecordCount(Map<String,Object> map);
}