package events.sales;

/**
 * @author longhuashen
 * @since 2017/6/17
 */
public class ImportOrderSyncEvent {

    private String email;

    public ImportOrderSyncEvent(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
