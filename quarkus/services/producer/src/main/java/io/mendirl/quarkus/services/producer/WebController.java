package io.mendirl.quarkus.services.producer;

import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

@Path("/api/position")
public class WebController {

    @Inject
    Logger logger;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"position"})
    public Position position() throws NoSuchAlgorithmException {
        var position = new Position("position " + SecureRandom.getInstanceStrong().nextInt() % 1000, Instant.now());
        logger.infof("position generated: %s", position);
        return position;
    }

}

class Position {

    private final String name;
    private final Instant date;

    public Position(String name, Instant date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public Instant getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Position{" +
            "name='" + name + '\'' +
            ", date=" + date +
            '}';
    }
}
