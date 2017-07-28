package services.purchase.impl;

import com.google.inject.Inject;

import mapper.purchase.SequenceMapper;

import org.apache.commons.lang3.StringUtils;

import services.purchase.ISequenceService;
import utils.purchase.IDUtils;

/**
 * Created by luwj on 2015/12/11.
 */
public class SequenceService implements ISequenceService {

    /**
     * 采购单号标识
     */
    public static final String PURCHASE_NO = "PURCHASE_NO";
    /**
     * 退货单号标识
     */
    public static final String RETURN_ORDER_NO = "RETURN_ORDER_NO";
    
    public static final String BATCH_NO = "BATCH_NO";

    /**
     * 采购单号前缀
     */
    public static final String PURCASE_PREFIX = "TTCG_";

    @Inject
    ISequenceService iSequenceService;

    @Inject
    SequenceMapper sequenceMapper;

    @Override
    public int selectNextval(String seqName) {
        synchronized (new byte[0]) {
            sequenceMapper.updateSequence(seqName);
            return sequenceMapper.selectCurrentval(seqName);
        }
    }

    /**
     * 获取采购订单号
     * @return
     */
    @Override
    public String getPurchaseNo(String flag){
        int nextval = iSequenceService.selectNextval(PURCHASE_NO);
        String created = IDUtils.getCode(nextval, "0000000000");
        if(StringUtils.isBlank(flag))
            return PURCASE_PREFIX + created;
        else
            return flag + created;
    }

    @Override
    public String getReturnOrderNo(){
    	int nextval = iSequenceService.selectNextval(RETURN_ORDER_NO);
        String created = IDUtils.getCode(nextval, "0000000000");
        return "WCTH" + created;
    }
	
	@Override
	public String getBatchNo() {
		int nextval = iSequenceService.selectNextval(BATCH_NO);
		String created = IDUtils.getCode(nextval, "0000000000");
		return "BN_"+created; 
		} 
	}