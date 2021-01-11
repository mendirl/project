package io.mendirl.spring.server;


import io.mendirl.spring.server.domain.Energie;
import io.mendirl.spring.server.domain.Puissance;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DataExtractionTest {

    @Test
    void test_extraction_energie_by_year() throws IOException {
        var resource = new ClassPathResource("/samples/eCO2mix_RTE_energie_A.zip", getClass());
        var bytes = Files.readAllBytes(Path.of(resource.getFile().getAbsolutePath()));
        var dataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(ByteBuffer.wrap(bytes));
        var extraction = new DataExtraction<Energie>();

        var results = extraction.extract(dataBuffer, Energie.class);

        assertThat(results).hasSize(92);
        assertThat(results.get(0)).hasFieldOrPropertyWithValue("year", "2012");
        assertThat(results.get(0)).hasFieldOrPropertyWithValue("quality", "Données définitives");
    }

    @Test
    void test_extraction_energie_by_month() throws IOException {
        var resource = new ClassPathResource("/samples/eCO2mix_RTE_energie_M.zip", getClass());
        var bytes = Files.readAllBytes(Path.of(resource.getFile().getAbsolutePath()));
        var dataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(ByteBuffer.wrap(bytes));
        var extraction = new DataExtraction<Energie>();

        var results = extraction.extract(dataBuffer, Energie.class);

        assertThat(results).hasSize(1247);
        assertThat(results.get(0)).hasFieldOrPropertyWithValue("date", "2012-01");
    }

    @Test
    void test_extraction_puissance_by_date() throws IOException {
        var resource = new ClassPathResource("/samples/eCO2mix_RTE_2021-01-04.zip", getClass());
        var bytes = Files.readAllBytes(Path.of(resource.getFile().getAbsolutePath()));
        var dataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(ByteBuffer.wrap(bytes));
        var extraction = new DataExtraction<Puissance>();

        var results = extraction.extract(dataBuffer, Puissance.class);

        assertThat(results).hasSize(96);
        assertThat(results.get(0)).hasFieldOrPropertyWithValue("perimeter", "France");
        assertThat(results.get(0)).hasFieldOrPropertyWithValue("origin", "Données temps réel");
    }


}
