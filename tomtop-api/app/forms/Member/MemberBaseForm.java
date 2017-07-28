package forms.Member;

/**
 * @author ye_ziran
 * @since 2016/3/24 12:02
 */
public class MemberBaseForm {

    private String memberEmail;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }
}
