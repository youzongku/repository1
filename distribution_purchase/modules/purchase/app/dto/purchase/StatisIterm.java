package dto.purchase;

/**
 * Created by luwj on 2015/12/28.
 */
public class StatisIterm {

    private Integer amount;//采购单总数

    private ReturnMess returnMess;

    public StatisIterm(){}

    public StatisIterm(Integer amount) {
        this.amount = amount;
    }

    public StatisIterm(Integer amount, ReturnMess returnMess) {
        this.amount = amount;
        this.returnMess = returnMess;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public ReturnMess getReturnMess() {
        return returnMess;
    }

    public void setReturnMess(ReturnMess returnMess) {
        this.returnMess = returnMess;
    }
}
