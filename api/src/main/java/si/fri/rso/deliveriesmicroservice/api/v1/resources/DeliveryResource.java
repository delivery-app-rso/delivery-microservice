package si.fri.rso.deliveriesmicroservice.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import si.fri.rso.deliverymicroservice.lib.Delivery;
import si.fri.rso.deliverymicroservice.lib.DeliveryDto;
import si.fri.rso.deliverymicroservice.lib.DeliveryStatusDto;
import si.fri.rso.deliverymicroservice.services.beans.DeliveryBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.kumuluz.ee.cors.annotations.CrossOrigin;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/deliveries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CrossOrigin(allowOrigin = "*")
public class DeliveryResource {

        private Logger log = Logger.getLogger(DeliveryResource.class.getName());

        @Inject
        private DeliveryBean deliveryBean;

        @Context
        protected UriInfo uriInfo;

        @Operation(description = "Get all delivieries.", summary = "Get all deliveries")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "List of deliveries", content = @Content(schema = @Schema(implementation = Delivery.class, type = SchemaType.ARRAY)), headers = {
                                        @Header(name = "X-Total-Count", description = "Number of objects in list") }) })
        @GET
        public Response getDeliveries() {

                List<Delivery> deliveriesMetadata = deliveryBean.getDeliveryFilter(uriInfo);

                return Response.status(Response.Status.OK).entity(deliveriesMetadata).build();
        }

        @Operation(description = "Get status", summary = "Get status")
        @APIResponses({ @APIResponse(responseCode = "200", description = "status") })
        @GET
        @Path("/status")
        public Response getStatus() {
                return Response.status(Response.Status.OK).build();
        }

        @Operation(description = "Get deliveries for user.", summary = "Get deliveries")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "Deliveries data for user", content = @Content(schema = @Schema(implementation = Delivery.class, type = SchemaType.ARRAY))) })
        @GET
        @Path("/{userId}")
        public Response getUserDeliveries(
                        @Parameter(description = "user ID.", required = true) @PathParam("userId") Integer userId) {

                List<Delivery> deliveries = deliveryBean.getUserDeliveries(userId);

                return Response.status(Response.Status.OK).entity(deliveries).build();
        }

        @Operation(description = "Start delivery.", summary = "Start delivery")
        @APIResponses({
                        @APIResponse(responseCode = "201", description = "Delivery successfully started."),
                        @APIResponse(responseCode = "400", description = "Bad request error .")
        })
        @POST
        public Response startDelivery(
                        @RequestBody(description = "DTO object with delivery data.", required = true, content = @Content(schema = @Schema(implementation = DeliveryDto.class))) DeliveryDto deliveryDto) {

                if (deliveryDto.getUserId() == null || deliveryDto.getAddress() == null
                                || deliveryDto.getItemId() == null) {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                }

                Delivery delivery = deliveryBean.startDelivery(deliveryDto);

                if (delivery == null) {
                        return Response.status(Response.Status.CONFLICT).build();
                }

                return Response.status(Response.Status.CREATED).entity(delivery).build();

        }

        @Operation(description = "Update delivery.", summary = "Update delivery")
        @APIResponses({
                        @APIResponse(responseCode = "200", description = "delivery successfully updated."),
                        @APIResponse(responseCode = "404", description = "Delivery not found.")
        })
        @POST
        @Path("/{deliveryId}/set-status")
        public Response setDeliveryStatus(
                        @Parameter(description = "Delivery ID.", required = true) @PathParam("deliveryId") Integer deliveryId,
                        @RequestBody(description = "DTO object with delivery status.", required = true, content = @Content(schema = @Schema(implementation = DeliveryStatusDto.class))) DeliveryStatusDto deliveryStatusDto) {

                Delivery delivery = deliveryBean.setDeliveryStatus(deliveryId, deliveryStatusDto);

                if (delivery == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.status(Response.Status.OK).entity(delivery).build();

        }

        @Operation(description = "Delete delivery.", summary = "Delete delivery")
        @APIResponses({
                        @APIResponse(responseCode = "204", description = "Delivery successfully deleted."),
                        @APIResponse(responseCode = "404", description = "Not found.")
        })
        @DELETE
        @Path("{deliveryId}")
        public Response deleteUser(
                        @Parameter(description = "Delivery ID.", required = true) @PathParam("deliveryId") Integer deliveryId) {

                boolean deleted = deliveryBean.deleteDelivery(deliveryId);

                if (deleted) {
                        return Response.status(Response.Status.NO_CONTENT).build();
                } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }
        }

}
