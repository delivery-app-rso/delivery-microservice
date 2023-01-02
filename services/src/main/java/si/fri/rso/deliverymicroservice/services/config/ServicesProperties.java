package si.fri.rso.deliverymicroservice.services.config;

import javax.enterprise.context.ApplicationScoped;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

@ApplicationScoped
@ConfigBundle("microservices")
public class ServicesProperties {

    @ConfigValue(watch = true)
    private String mailingServiceHost;

    @ConfigValue(watch = true)
    private String usersServiceHost;

    @ConfigValue(watch = true)
    private String itemsServiceHost;

    @ConfigValue(watch = true)
    private String invoiceServiceHost;

    @ConfigValue(watch = true)
    private String navigationServiceHost;

    public String getNavigationServiceHost() {
        return navigationServiceHost;
    }

    public void setNavigationServiceHost(String navigationServiceHost) {
        this.navigationServiceHost = navigationServiceHost;
    }

    public String getMailingServiceHost() {
        return mailingServiceHost;
    }

    public void setMailingServiceHost(String mailingServiceHost) {
        this.mailingServiceHost = mailingServiceHost;
    }

    public String getUsersServiceHost() {
        return usersServiceHost;
    }

    public void setUsersServiceHost(String usersServiceHost) {
        this.usersServiceHost = usersServiceHost;
    }

    public String getItemsServiceHost() {
        return itemsServiceHost;
    }

    public void setItemsServiceHost(String itemsServiceHost) {
        this.itemsServiceHost = itemsServiceHost;
    }

    public String getInvoiceServiceHost() {
        return invoiceServiceHost;
    }

    public void setInvoiceServiceHost(String invoiceServiceHost) {
        this.invoiceServiceHost = invoiceServiceHost;
    }
}
