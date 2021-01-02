package org.mendirl.bigdata.parquet;

import java.util.Map;
import java.util.Objects;

public class Position {

    private long id;
    private double value;
    private String name;
    private Map<String, String> riskfactors;

    public Position() {
    }

    public Position(long id, double value, String name, Map<String, String> riskfactors) {
        this.id = id;
        this.value = value;
        this.name = name;
        this.riskfactors = riskfactors;
    }


    public long getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getRiskfactors() {
        return riskfactors;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return id == position.id &&
            Double.compare(position.value, value) == 0 &&
            Objects.equals(name, position.name) &&
            Objects.equals(riskfactors, position.riskfactors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, name, riskfactors);
    }

    @Override
    public String toString() {
        return "Position{" +
            "id=" + id +
            ", value=" + value +
            ", name='" + name + '\'' +
            ", riskfactors=" + riskfactors +
            '}';
    }
}
