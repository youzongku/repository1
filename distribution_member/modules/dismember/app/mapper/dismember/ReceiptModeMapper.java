package mapper.dismember;

import entity.dismember.ReceiptMode;

import java.util.List;

public interface ReceiptModeMapper extends BaseMapper<ReceiptMode> {

    /**
     * 查询所有收款方式
     * @return
     */
    List<ReceiptMode> getAllReceiptModes();

}