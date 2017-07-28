package entity.timer;

import java.util.List;

/**
 * @author longhuashen
 * @since 2017/1/4
 */
public class CloudAndMicroInventoryDto {

    private String account;

    private List<String> skus;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<String> getSkus() {
        return skus;
    }

    public void setSkus(List<String> skus) {
        this.skus = skus;
    }
}
