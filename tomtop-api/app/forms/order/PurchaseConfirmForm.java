package forms.order;

import play.data.validation.Constraints;

import java.util.List;

/**
 * @author ye_ziran
 * @since 2016/3/23 14:59
 */
public class PurchaseConfirmForm {

    @Constraints.Required
    private String memberEmail;
    @Constraints.Required
    private List<PurchaseConfirmDetailForm> orderDetails;

    public List<PurchaseConfirmDetailForm> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<PurchaseConfirmDetailForm> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

}
