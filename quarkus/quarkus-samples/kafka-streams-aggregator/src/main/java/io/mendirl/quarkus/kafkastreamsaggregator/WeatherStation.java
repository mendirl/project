package io.mendirl.quarkus.kafkastreamsaggregator;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class WeatherStation {

    public int id;
    public String name;
}
