package io.mendirl.quarkus.kafkarestclient;

import io.smallrye.reactive.messaging.annotations.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TransactionProcessor {

    private static final Logger LOGGER = Logger.getLogger("TransactionProcessor");

    @Inject
    @RestClient
    TransactionService service;

    @Incoming("in") // The first channel - we read from it
    @Outgoing("out") // The second channel - we write to it
    @Blocking
    public TransactionResult sendToTransactionService(Transaction transaction) {
        LOGGER.infof("Sending %s transaction service", transaction);
        return service.postSync(transaction);
    }

//    @Incoming("in")
//    @Outgoing("out")
//    public Uni<TransactionResult> sendToTransactionService(Transaction transaction) {
//        LOGGER.infof("Sending %s transaction service", transaction);
//        return service.postAsync(transaction);
//    }

}
