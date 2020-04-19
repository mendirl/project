package com.mendirl.bigdata.spark;

import org.apache.spark.sql.SparkSession;
import org.mendirl.common.Constant;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class SparkParquetRunner implements ApplicationRunner {

    private SparkSession sparkSession;

    public SparkParquetRunner(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String input = new ClassPathResource(Constant.PATH_TARIFS_CSV).getFile().toString();

        sparkSession.read()
            .option("header", true)
            .option("inferSchema", true)
            .csv(input)
            .write()
            .parquet(Constant.PATH_TARIFS_PARQUET_OUTPUT);
    }
}
