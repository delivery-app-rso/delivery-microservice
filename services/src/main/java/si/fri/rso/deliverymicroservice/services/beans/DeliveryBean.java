package si.fri.rso.deliverymicroservice.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.json.JSONObject;

import si.fri.rso.deliverymicroservice.lib.Delivery;
import si.fri.rso.deliverymicroservice.lib.DeliveryDto;
import si.fri.rso.deliverymicroservice.lib.DeliveryStatusDto;
import si.fri.rso.deliverymicroservice.lib.InvoiceDto;
import si.fri.rso.deliverymicroservice.lib.NavigationDto;
import si.fri.rso.deliverymicroservice.models.converters.DeliveryConverter;
import si.fri.rso.deliverymicroservice.models.entities.DeliveryEntity;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

@RequestScoped
public class DeliveryBean {

    private Logger log = Logger.getLogger(DeliveryBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private ServicesBean servicesBean;

    private final Float PRICE_PER_KM = (float) 0.23;

    public List<Delivery> getDeliveries() {

        TypedQuery<DeliveryEntity> query = em.createNamedQuery(
                "DeliveryEntity.getAll", DeliveryEntity.class);

        List<DeliveryEntity> resultList = query.getResultList();

        return resultList.stream().map(DeliveryConverter::toDto).collect(Collectors.toList());
    }

    public Delivery getDelivery(Integer id) {
        DeliveryEntity deliveryEntity = em.find(DeliveryEntity.class, id);

        return DeliveryConverter.toDto(deliveryEntity);
    }

    @Timed
    public List<Delivery> getDeliveryFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, DeliveryEntity.class, queryParameters).stream()
                .map(DeliveryConverter::toDto).collect(Collectors.toList());
    }

    public List<Delivery> getUserDeliveries(Integer userId) {
        List<DeliveryEntity> resultsList = (List<DeliveryEntity>) em
                .createQuery("SELECT d FROM DeliveryEntity d WHERE d.userId=:userId ")
                .setParameter("userId", userId)
                .getResultList();

        return resultsList.stream().map(DeliveryConverter::toDto).collect(Collectors.toList());
    }

    public List<Delivery> getDelivererDeliveries(Integer userId) {
        List<DeliveryEntity> resultsList = (List<DeliveryEntity>) em
                .createQuery("SELECT d FROM DeliveryEntity d WHERE d.delivererId=:userId ")
                .setParameter("userId", userId)
                .getResultList();

        return resultsList.stream().map(DeliveryConverter::toDto).collect(Collectors.toList());
    }

    public Delivery startDelivery(DeliveryDto deliveryDto) {
        JSONObject delivererJsonObject = servicesBean.getDeliverer();
        System.out.println(delivererJsonObject);
        Delivery delivery = new Delivery();
        delivery.setUserId(deliveryDto.getUserId());
        delivery.setDelivered(false);
        delivery.setDelivererId(delivererJsonObject.getInt("id"));
        delivery.setAddress(deliveryDto.getAddress());
        delivery.setItemId(deliveryDto.getItemId());

        delivery = this.creatDelivery(delivery);

        if (delivery == null) {
            throw new RuntimeException("Failed to create delivery");
        }

        JSONObject userJsonObject = servicesBean.getUser(deliveryDto.getUserId());
        JSONObject itemJsonObject = servicesBean.getItem(deliveryDto.getItemId());

        JSONObject navigationJsonObject = servicesBean.createNavigation(new NavigationDto(delivery.getId(),
                userJsonObject.getString("address"), deliveryDto.getAddress()));

        if (userJsonObject == null || itemJsonObject == null || navigationJsonObject == null) {
            System.out.println("failed to fetch data!");
            return null;
        }

        Float tripCost = this.getDeliveryCost(navigationJsonObject);

        InvoiceDto invoiceDto = new InvoiceDto(String.valueOf(deliveryDto.getUserId()),
                userJsonObject.getString("name"),
                userJsonObject.getString("email"),
                userJsonObject.getString("address"),
                String.valueOf(deliveryDto.getItemId()),
                itemJsonObject.getString("name"), String.valueOf(tripCost));
        JSONObject invoiceJsonObject = servicesBean.generateInvoice(invoiceDto);

        servicesBean.sendInvoiceEmail(itemJsonObject.getString("name"), invoiceJsonObject, userJsonObject);
        servicesBean.sendDeliveryStartedEmail(userJsonObject, itemJsonObject);

        return delivery;
    }

    public Delivery startDeliveryFallback(DeliveryDto deliveryDto) {
        System.out.println("notr w fallbacku");
        return null;
    }

    public Delivery creatDelivery(Delivery delivery) {

        DeliveryEntity deliveryEntity = DeliveryConverter.toEntity(delivery);

        try {
            beginTx();
            em.persist(deliveryEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (deliveryEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return DeliveryConverter.toDto(deliveryEntity);
    }

    public Delivery setDeliveryStatus(Integer id, DeliveryStatusDto deliveryStatusDto) {
        DeliveryEntity deliveryEntity = em.find(DeliveryEntity.class, id);

        if (deliveryEntity == null) {
            return null;
        }

        try {
            beginTx();
            deliveryEntity.setDelivered(deliveryStatusDto.getDelivered());
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            return null;
        }

        JSONObject userJsonObject = servicesBean.getUser(deliveryEntity.getUserId());
        JSONObject itemJsonObject = servicesBean.getItem(deliveryEntity.getItemId());

        servicesBean.sendDeliveredEmail(userJsonObject, itemJsonObject);

        return DeliveryConverter.toDto(deliveryEntity);
    }

    public boolean deleteDelivery(Integer id) {

        DeliveryEntity deliveryEntity = em.find(DeliveryEntity.class, id);

        if (deliveryEntity != null) {
            try {
                beginTx();
                em.remove(deliveryEntity);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }

        return true;
    }

    private Float getDeliveryCost(JSONObject navigationJsonObject) {
        String[] distance = navigationJsonObject.getString("distance").split(" ");

        if (distance[1].equals("km")) {

            return Float.parseFloat(distance[0].replace(",", ".")) * PRICE_PER_KM;
        }

        return Float.parseFloat(distance[0].replace(",", ".")) * PRICE_PER_KM * (float) 0.001;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
