package si.fri.rso.deliverymicroservice.lib;

public class NavigationDto {
    private Integer deliveryId;

    private String origin;

    private String destination;

    public NavigationDto(Integer deliveryId, String origin, String destination) {
        this.deliveryId = deliveryId;
        this.origin = origin;
        this.destination = destination;
    }

    public Integer getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
