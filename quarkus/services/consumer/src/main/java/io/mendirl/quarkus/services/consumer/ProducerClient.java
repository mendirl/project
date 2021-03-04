package io.mendirl.quarkus.services.consumer;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/api/position")
@RegisterRestClient(configKey = "producer-api")
public interface ProducerClient {

    @GET
    Position position();
}


