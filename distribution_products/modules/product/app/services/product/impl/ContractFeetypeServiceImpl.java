package services.product.impl;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import entity.contract.ContractFeetype;
import mapper.contract.ContractFeetypeMapper;
import mapper.contract.ContractMapper;
import org.apache.commons.lang3.StringUtils;
import services.product.IContractFeetypeService;
import util.product.Page;
import valueobjects.product.Pager;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author longhuashen
 * @since 2017/5/11
 */
public class ContractFeetypeServiceImpl implements IContractFeetypeService {

    @Inject
    private ContractFeetypeMapper contractFeetypeMapper;

    @Inject
    private ContractMapper contractMapper;

    @Override
    public Map<String, Object> addContractFeetype(Map<String, String> map, String operator) {
        Map<String, Object> result = Maps.newHashMap();

        if (!StringUtils.isNumeric(map.get("type")) || StringUtils.isBlank(map.get("name"))) {
            result.put("suc", false);
            result.put("msg", "参数错误！");
            return result;
        }

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("name", map.get("name").trim());
        paramMap.put("type", Integer.valueOf(map.get("type")));
        int count = contractFeetypeMapper.countByParam(paramMap);
        if (count > 0) {
            result.put("suc", false);
            result.put("msg", "同一费用类型，不能有相同的名称！");
            return result;
        }

        ContractFeetype contractFeetype = new ContractFeetype();
        contractFeetype.setName(map.get("name").trim());
        contractFeetype.setType(Integer.valueOf(map.get("type")));
        contractFeetype.setCreateUser(operator);
        contractFeetype.setCreateTime(new Date());
        contractFeetype.setDesc(map.get("desc"));

        int num = contractFeetypeMapper.insertSelective(contractFeetype);
        if (num < 0) {
            result.put("suc", false);
            result.put("msg", "添加合同费用项失败！");
            return result;
        }
        result.put("suc", true);
        result.put("msg", "添加合同费用项成功！");
        return result;
    }

    @Override
    public Page<ContractFeetype> getContractFeetypesByPage(Map<String, Object> params) {
        List<ContractFeetype> contractFeetypes = contractFeetypeMapper.getContractFeetypesByPage(params);
        int rows = contractFeetypeMapper.getCountByPage(params);
        return new Page<>(contractFeetypes, rows, (Integer) params.get("currPage"), (Integer) params.get("pageSize"));
    }

    @Override
    public List<ContractFeetype> getAllContractFeetype() {
        return contractFeetypeMapper.getAllContractFeetype();
    }
}
