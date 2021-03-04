package io.mendirl.quarkus.services.consumer;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class SchedulingConfiguration {

    @Inject
    Logger logger;

    @Inject
    @RestClient
    private ProducerClient client;

    @Scheduled(delay = 1, delayUnit = TimeUnit.SECONDS, every = "1s")
    public void scheduledWeb() {
        var position = client.position();
//            .subscribe {
        logger.infof("position retrieved: %s", position);
//        }
    }
}
