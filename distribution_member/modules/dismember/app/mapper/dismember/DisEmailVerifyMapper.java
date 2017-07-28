package mapper.dismember;

import entity.dismember.DisEmailVerify;

/**
 *
 * @author luwj
 *
 */
public interface DisEmailVerifyMapper extends BaseMapper<DisEmailVerify> {

    DisEmailVerify getVerify(DisEmailVerify disEmailVerify);

    int getVerifyCount(DisEmailVerify disEmailVerify);
    
    /**
     * 获取最新的邮件记录
     * @param disEmailVerify
     * @return
     * @author huchuyin
     * @date 2016年9月23日 下午6:11:36
     */
    DisEmailVerify getLastVerifyByEmail(DisEmailVerify disEmailVerify);
}