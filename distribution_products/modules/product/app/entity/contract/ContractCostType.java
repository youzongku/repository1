package entity.contract;

/**
 * 合同费用类型
 * @author zbc
 * 2017年3月25日 上午11:40:04
 */
public class ContractCostType {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 类型名称
     */
    private String type;

    /**
     * 计算公式
     */
    private String formula;

    /**
     * 公式描述
     */
    private String formulaDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getFormulaDesc() {
        return formulaDesc;
    }

    public void setFormulaDesc(String formulaDesc) {
        this.formulaDesc = formulaDesc;
    }
}