package dto.dismember;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entity.dismember.DisCredit;
import entity.dismember.DisMember;

/**
 * @author hanfs
 * 描述：用户信用额度业务实体类
 *2016年4月20日
 */
public class CreditDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private DisMember member;//用户信息
    
    //用户对应的所有信息用额度信息
    private List<DisCredit> credits = new ArrayList<DisCredit>();

	public DisMember getMember() {
		return member;
	}

	public void setMember(DisMember member) {
		this.member = member;
	}

	public List<DisCredit> getCredits() {
		return credits;
	}

	public void setCredits(List<DisCredit> credits) {
		this.credits = credits;
	}
	
}
