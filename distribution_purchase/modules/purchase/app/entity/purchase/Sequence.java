package entity.purchase;

/**
 * --（采购单、退款单等）单号表
 * luwj
 */
public class Sequence implements java.io.Serializable{

	private static final long serialVersionUID = 1L;

	private Integer id;

    private String seqName;//标识

    private Integer currentValue;//当前值

    private Integer increment;//增量

    private String remarks;//备注

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    public Integer getIncrement() {
        return increment;
    }

    public void setIncrement(Integer increment) {
        this.increment = increment;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}