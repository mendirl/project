package io.mendirl.quarkus.kafkarestclient;

public class Transaction {

    public String name;
    public int amount;

    @Override
    public String toString() {
        return "Transaction{" +
            "name='" + name + '\'' +
            ", amount=" + amount +
            '}';
    }
}
