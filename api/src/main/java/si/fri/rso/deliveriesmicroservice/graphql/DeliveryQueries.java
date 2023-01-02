package si.fri.rso.deliveriesmicroservice.graphql;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.graphql.classes.Filter;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.classes.Sort;
import com.kumuluz.ee.graphql.utils.GraphQLUtils;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import si.fri.rso.deliverymicroservice.lib.Delivery;
import si.fri.rso.deliverymicroservice.services.beans.DeliveryBean;

@GraphQLClass
@ApplicationScoped
public class DeliveryQueries {
    @Inject
    private DeliveryBean deliveryBean;

    @GraphQLQuery
    public PaginationWrapper<Delivery> getAllItems(@GraphQLArgument(name = "pagination") Pagination pagination,
            @GraphQLArgument(name = "sort") Sort sort,
            @GraphQLArgument(name = "filter") Filter filter) {

        return GraphQLUtils.process(deliveryBean.getDeliveries(), pagination, sort, filter);
    }

    @GraphQLQuery
    public Delivery getDelivery(@GraphQLArgument(name = "deliveryId") Integer deliveryId) {
        return deliveryBean.getDelivery(deliveryId);
    }

    @GraphQLQuery
    public List<Delivery> getUserDeliveries(@GraphQLArgument(name = "userId") Integer userId) {
        return deliveryBean.getUserDeliveries(userId);
    }
}
