package io.mendirl.quarkus.services.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Position {

    private String name;
    private Instant date;

    public Position() {
    }

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
