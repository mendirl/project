package io.mendirl.quarkus.services.producer;

import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.time.Instant;
import java.util.Random;

@Path("/api/position")
public class WebController {

    @Inject
    Logger logger;

    @GET
    @RolesAllowed({"position"})
    public Position hello(@Context SecurityContext ctx) {
        var position = new Position("position " + new Random().nextInt() % 1000, Instant.now());
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
