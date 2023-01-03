package si.fri.rso.deliverymicroservice.services.beans;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.JSONObject;

import si.fri.rso.deliverymicroservice.lib.DeliveryMailDto;
import si.fri.rso.deliverymicroservice.lib.InvoiceDto;
import si.fri.rso.deliverymicroservice.lib.InvoiceMailDto;
import si.fri.rso.deliverymicroservice.lib.NavigationDto;
import si.fri.rso.deliverymicroservice.services.clients.MailProcessApi;
import si.fri.rso.deliverymicroservice.services.config.ServicesProperties;

@ApplicationScoped
public class ServicesBean {
    @Inject
    private ServicesProperties servicesProperties;

    @Inject
    @RestClient
    private MailProcessApi mailProcessApi;

    private Client httpClient;
    private HttpClient client;

    @PostConstruct
    public void init() {
        this.httpClient = ClientBuilder.newClient();
        this.client = HttpClient.newHttpClient();
    }

    public void sendInvoiceEmail(String item, JSONObject invoiceJsonObject, JSONObject userJsonObject) {
        HashMap<String, String> invoiceData = new HashMap<>();
        invoiceData.put("filename", invoiceJsonObject.getString("filename"));
        invoiceData.put("amount", String.valueOf(invoiceJsonObject.getDouble("amount")));
        invoiceData.put("item", item);

        HashMap<String, String> userData = new HashMap<>();
        userData.put("name", userJsonObject.getString("name"));
        userData.put("email", userJsonObject.getString("email"));

        CompletionStage<String> stringCompletionStage = mailProcessApi
                .sendMailAsynch(new InvoiceMailDto("invoice", userData, invoiceData));

        stringCompletionStage.whenComplete((s, throwable) -> System.out.println("Finished sending mail: " + s));
    }

    public void sendDeliveredEmail(JSONObject userJsonObject, JSONObject itemsJsonObject) {
        HashMap<String, String> userData = new HashMap<>();
        userData.put("name", userJsonObject.getString("name"));
        userData.put("email", userJsonObject.getString("email"));

        HashMap<String, String> deliveryData = new HashMap<>();
        deliveryData.put("item", itemsJsonObject.getString("name"));

        CompletionStage<String> stringCompletionStage = mailProcessApi
                .sendMailAsynch(new DeliveryMailDto("delivered", userData, deliveryData));

        stringCompletionStage.whenComplete((s, throwable) -> System.out.println("Finished sending mail: " + s));
    }

    public void sendDeliveryStartedEmail(JSONObject userJsonObject, JSONObject itemsJsonObject) {
        HashMap<String, String> userData = new HashMap<>();
        userData.put("name", userJsonObject.getString("name"));
        userData.put("email", userJsonObject.getString("email"));

        HashMap<String, String> deliveryData = new HashMap<>();
        deliveryData.put("item", itemsJsonObject.getString("name"));

        CompletionStage<String> stringCompletionStage = mailProcessApi
                .sendMailAsynch(new DeliveryMailDto("delivery_start", userData, deliveryData));

        stringCompletionStage.whenComplete((s, throwable) -> System.out.println("Finished sending mail: " + s));
    }

    public JSONObject createNavigation(NavigationDto navigationDto) {
        JSONObject body = new JSONObject();
        body.put("deliveryId", navigationDto.getDeliveryId());
        body.put("origin", navigationDto.getOrigin());
        body.put("destination", navigationDto.getDestination());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.servicesProperties.getNavigationServiceHost() + "/v1/navigation"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject getItem(Integer itemId) {
        try {
            String itemStringObject = this.httpClient
                    .target(servicesProperties.getItemsServiceHost() + "/v1/items/" + itemId)
                    .request().get(new GenericType<String>() {
                    });

            JSONObject itemJsonObject = new JSONObject(itemStringObject);

            return itemJsonObject;
        } catch (WebApplicationException | ProcessingException e) {
            System.out.println(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public JSONObject getUser(Integer userId) {
        try {
            String userStringObject = this.httpClient
                    .target(servicesProperties.getUsersServiceHost() + "/v1/users/" + userId)
                    .request().get(new GenericType<String>() {
                    });

            JSONObject userJsonObject = new JSONObject(userStringObject);
            return userJsonObject;
        } catch (WebApplicationException | ProcessingException e) {
            System.out.println(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public JSONObject getDeliverer() {
        try {
            String userStringObject = this.httpClient
                    .target(servicesProperties.getUsersServiceHost() + "/v1/users/deliverer")
                    .request().get(new GenericType<String>() {
                    });

            JSONObject userJsonObject = new JSONObject(userStringObject);
            return userJsonObject;
        } catch (WebApplicationException | ProcessingException e) {
            System.out.println(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public JSONObject generateInvoice(InvoiceDto invoiceDto) {
        JSONObject body = new JSONObject();
        JSONObject deliveryData = new JSONObject(invoiceDto.getDelieryData().toString().replaceAll("=", ":"));
        JSONObject userData = new JSONObject(invoiceDto.getUserData().toString().replaceAll("=", ":"));

        body.put("userData", userData);
        body.put("deliveryData", deliveryData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.servicesProperties.getInvoiceServiceHost() + "/v1/invoices"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
