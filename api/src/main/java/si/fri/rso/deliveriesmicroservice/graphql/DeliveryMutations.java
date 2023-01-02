package si.fri.rso.deliveriesmicroservice.graphql;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import si.fri.rso.deliverymicroservice.lib.Delivery;
import si.fri.rso.deliverymicroservice.lib.DeliveryDto;
import si.fri.rso.deliverymicroservice.lib.DeliveryStatusDto;
import si.fri.rso.deliverymicroservice.services.beans.DeliveryBean;

@GraphQLClass
@ApplicationScoped
public class DeliveryMutations {
    @Inject
    private DeliveryBean deliveryBean;

    @GraphQLMutation
    public Delivery startDelivery(@GraphQLArgument(name = "delivery") DeliveryDto deliveryDto) {
        return deliveryBean.startDelivery(deliveryDto);
    }

    @GraphQLMutation
    public Delivery setDeliveryStatus(@GraphQLArgument(name = "deliveryId") Integer deliveryId,
            @GraphQLArgument(name = "status") Boolean status) {
        return deliveryBean.setDeliveryStatus(deliveryId, new DeliveryStatusDto(status));
    }
}
