package dto.dismember;

import java.io.Serializable;

/**
 * Created by LSL on 2016/2/17.
 */
public class UserRankHistoryDto implements Serializable {

    private static final long serialVersionUID = -3538006728871384399L;

    private Integer id;

    private String email;

    private String operator;

    private String operateDesc;

    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperateDesc() {
        return operateDesc;
    }

    public void setOperateDesc(String operateDesc) {
        this.operateDesc = operateDesc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
