package dto.payment.alipay;

import dto.payment.ReturnMess;

/**
 * Created by luwj on 2015/12/22.
 */
public class PayIterm {

    private PayParam payParam;

    private ReturnMess returnMess;

    public PayIterm(){}

    public PayIterm(PayParam payParam, ReturnMess returnMess) {
        this.payParam = payParam;
        this.returnMess = returnMess;
    }

    public PayParam getPayParam() {
        return payParam;
    }

    public void setPayParam(PayParam payParam) {
        this.payParam = payParam;
    }

    public ReturnMess getReturnMess() {
        return returnMess;
    }

    public void setReturnMess(ReturnMess returnMess) {
        this.returnMess = returnMess;
    }
}
