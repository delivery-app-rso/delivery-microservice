package si.fri.rso.deliverymicroservice.lib;

import java.util.HashMap;
import java.util.Map;

public class InvoiceMailDto {
    private String type;

    private Map<String, String> invoiceData;

    private Map<String, String> userData;

    public InvoiceMailDto(String type, Map<String, String> userData, HashMap<String, String> invoiceData) {
        this.type = type;
        this.userData = userData;
        this.invoiceData = invoiceData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getUserData() {
        return userData;
    }

    public void setUserData(Map<String, String> userData) {
        this.userData = userData;
    }

    public Map<String, String> getInvoiceData() {
        return invoiceData;
    }

    public void setInvoiceData(HashMap<String, String> invoiceData) {
        this.invoiceData = invoiceData;
    }
}
