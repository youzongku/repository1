package services.product;

import entity.contract.ContractFeetype;
import util.product.Page;

import java.util.List;
import java.util.Map;

/**
 * @author longhuashen
 * @since 2017/5/11
 */
public interface IContractFeetypeService {

    Map<String, Object> addContractFeetype(Map<String, String> map, String applyUser);

    Page<ContractFeetype> getContractFeetypesByPage(Map<String, Object> map);

    List<ContractFeetype> getAllContractFeetype();
}
