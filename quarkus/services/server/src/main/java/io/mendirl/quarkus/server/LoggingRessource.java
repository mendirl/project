package io.mendirl.quarkus.server;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("/logging")
public class LoggingRessource {

    private static final Logger LOG = Logger.getLogger(LoggingRessource.class);

    @GET
    public void log() {
        LOG.info("Some useful log message");
    }
}
