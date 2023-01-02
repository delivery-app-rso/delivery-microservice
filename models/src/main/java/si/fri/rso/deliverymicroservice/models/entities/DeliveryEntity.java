package si.fri.rso.deliverymicroservice.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "deliveries")
@NamedQueries(value = {
        @NamedQuery(name = "DeliveryEntity.getAll", query = "SELECT im FROM DeliveryEntity im")
})
public class DeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "delivererId")
    private Integer delivererId;

    @Column(name = "itemId")
    private Integer itemId;

    @Column(name = "address")
    private String address;

    @Column(name = "delivered")
    private Boolean delivered;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDelivererId() {
        return delivererId;
    }

    public void setDelivererId(Integer delivererId) {
        this.delivererId = delivererId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }
}