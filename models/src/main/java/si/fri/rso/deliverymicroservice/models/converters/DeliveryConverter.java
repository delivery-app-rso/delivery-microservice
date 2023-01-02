package si.fri.rso.deliverymicroservice.models.converters;

import si.fri.rso.deliverymicroservice.lib.Delivery;
import si.fri.rso.deliverymicroservice.models.entities.DeliveryEntity;

public class DeliveryConverter {

    public static Delivery toDto(DeliveryEntity entity) {

        Delivery dto = new Delivery();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setItemId(entity.getItemId());
        dto.setDelivererId(entity.getDelivererId());
        dto.setAddress(entity.getAddress());
        dto.setDelivered(entity.getDelivered());

        return dto;

    }

    public static DeliveryEntity toEntity(Delivery dto) {

        DeliveryEntity entity = new DeliveryEntity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setItemId(dto.getItemId());
        entity.setDelivererId(dto.getDelivererId());
        entity.setAddress(dto.getAddress());
        entity.setDelivered(dto.getDelivered());

        return entity;

    }

}
