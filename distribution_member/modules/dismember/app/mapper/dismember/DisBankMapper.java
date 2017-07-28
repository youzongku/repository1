package mapper.dismember;

import entity.dismember.DisBank;

import java.util.List;

public interface DisBankMapper extends BaseMapper<DisBank> {

    /**
     * 查询所有银行
     * @return
     */
    List<DisBank> getAllBanks();

}