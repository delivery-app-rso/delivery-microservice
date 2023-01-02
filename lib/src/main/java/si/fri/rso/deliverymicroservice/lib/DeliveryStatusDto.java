package si.fri.rso.deliverymicroservice.lib;

public class DeliveryStatusDto {
    private Boolean delivered;

    public DeliveryStatusDto(Boolean status) {
        this.delivered = status;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

}
