package si.fri.rso.deliverymicroservice.services.clients;

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.Dependent;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/mailing")
@RegisterRestClient(configKey = "mail-process-api")
@Dependent
public interface MailProcessApi {
    @POST
    CompletionStage<String> sendMailAsynch(Object dto);
}