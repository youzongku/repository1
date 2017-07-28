package mapper.payment.yijipay;

import entity.payment.yijipay.MergeResult;

public interface MergeResultMapper {

    int saveResult(MergeResult record);

    MergeResult getResult(MergeResult record);

    int upResult(MergeResult record);

}