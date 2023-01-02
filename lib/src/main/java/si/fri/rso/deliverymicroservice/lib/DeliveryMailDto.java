package si.fri.rso.deliverymicroservice.lib;

import java.util.HashMap;
import java.util.Map;

public class DeliveryMailDto {

    private String type;

    private Map<String, String> userData;

    private Map<String, String> deliveryData;

    public DeliveryMailDto(String type, Map<String, String> userData, HashMap<String, String> deliveryData) {
        this.type = type;
        this.userData = userData;
        this.deliveryData = deliveryData;
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

    public Map<String, String> getDeliveryData() {
        return deliveryData;
    }

    public void setDeliveryData(Map<String, String> deliveryData) {
        this.deliveryData = deliveryData;
    }

}
