package com.mendirl.bigdata.spark;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.mendirl.common.Constant;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static org.apache.spark.sql.functions.avg;
import static org.apache.spark.sql.functions.desc;

@Component
public class SparkRunner implements Serializable /*implements ApplicationRunner */ {

    private SparkSession sparkSession;

    private SparkRunnerDelegate sparkRunnerDelegate;

    public SparkRunner(SparkSession sparkSession, SparkRunnerDelegate sparkRunnerDelegate) {
        this.sparkSession = sparkSession;
        this.sparkRunnerDelegate = sparkRunnerDelegate;
    }

    public void run(ApplicationArguments args) throws Exception {
        Dataset<Row> tarifs = sparkSession.read()
            .option("header", true)
            .option("inferSchema", true)
            .option("spark.sql.warehouse.dir", "./target")
            .csv(new ClassPathResource("classpath:" + Constant.PATH_TARIFS_CSV).getURL().getPath())
            .filter((FilterFunction<Row>) value ->
                sparkRunnerDelegate.apply(sparkSession, value))
            .groupBy("formule")
            .agg(avg("prime").as("average"))
            .orderBy(desc("average"));
        tarifs.show();
    }


}
