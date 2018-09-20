package com.mendirl.bigdata.spark;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class SparkRunnerDelegate implements Serializable {

    private SparkSession sparkSession;

    public SparkRunnerDelegate(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    public boolean apply(SparkSession sparkSession, Row value) {
        return value.<String>getAs("assureur").equals("Mon SUPER assureur");
    }
}
