package com.mendirl.bigdata.spark;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SparkParquetApplicationTest {

    @Autowired
    private SparkParquetRunner sparkRunner;

    @Test
    @Disabled
    public void test1() throws Exception {
        sparkRunner.run(null);
    }
}
