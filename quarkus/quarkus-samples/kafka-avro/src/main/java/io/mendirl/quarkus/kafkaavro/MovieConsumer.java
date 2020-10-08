package io.mendirl.quarkus.kafkaavro;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieConsumer {

    private static final Logger LOGGER = Logger.getLogger("MovieConsumer");

    @Incoming("movies")
    public void receive(Movie movie) {
        LOGGER.infof("Received movie: %s (%d)", movie.getTitle(), movie.getYear());
    }

}
