package si.fri.rso.deliverymicroservice.lib;

import java.util.HashMap;
import java.util.Map;

public class InvoiceDto {

    private Map<String, String> delieryData;

    private Map<String, String> userData;

    public InvoiceDto(String userId, String name, String email, String address, String itemId, String item,
            String amount) {
        userData = new HashMap<>();
        userData.put("id", userId);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("address", address);

        delieryData = new HashMap<>();
        delieryData.put("itemId", itemId);
        delieryData.put("item", item);
        delieryData.put("amount", amount);
    }

    public Map<String, String> getDelieryData() {
        return delieryData;
    }

    public void setDelieryData(Map<String, String> delieryData) {
        this.delieryData = delieryData;
    }

    public Map<String, String> getUserData() {
        return userData;
    }

    public void setUserData(Map<String, String> userData) {
        this.userData = userData;
    }

}
