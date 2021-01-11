package io.mendirl.spring.server;

import io.mendirl.spring.server.domain.Energie;
import io.mendirl.spring.server.domain.Puissance;
import io.mendirl.spring.server.properties.Eco2MixProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Log4j2
public class RetrieverService {

    private final WebClient client;
    private final Eco2MixProperties properties;
    private final DataExtraction<Energie> energieDataExtraction = new DataExtraction<>();
    private final DataExtraction<Puissance> puissanceDataExtraction = new DataExtraction<>();


    public RetrieverService(WebClient.Builder builder, Eco2MixProperties properties) {
        this.properties = properties;
        client = builder.baseUrl(properties.getUrl()).build();
    }


    public void showProperties() {
        log.info("{}", properties);

        client.get().uri(properties.getEnergie().getPathYear())
            .exchangeToMono(clientResponse -> clientResponse.bodyToMono(DataBuffer.class))
            .map(databuffer -> energieDataExtraction.extract(databuffer, Energie.class))
            .log()
            .subscribe(content -> log.info("{}", content));

        client.get().uri(properties.getEnergie().getPathYear())
            .exchangeToMono(clientResponse -> clientResponse.bodyToMono(DataBuffer.class))
            .map(databuffer -> energieDataExtraction.extract(databuffer, Energie.class))
            .log()
            .subscribe(content -> log.info("{}", content));

        client.get().uri(properties.getPuissance().getPathday())
            .exchangeToMono(clientResponse -> clientResponse.bodyToMono(DataBuffer.class))
            .map(databuffer -> puissanceDataExtraction.extract(databuffer, Puissance.class))
            .log()
            .subscribe(content -> log.info("{}", content));
    }


}
