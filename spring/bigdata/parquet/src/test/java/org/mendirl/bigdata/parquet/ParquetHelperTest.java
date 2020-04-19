package org.mendirl.bigdata.parquet;

import org.apache.avro.Schema;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.IntStream;

public class ParquetHelperTest {

    @Test
//    @Disabled
    @ExtendWith(TempDirectory.class)
    public void test_1(@TempDir Path folder) throws IOException {
        String filename = folder.resolve("position.parquet").toString();
        Configuration conf = new Configuration();

        ParquetHelper<Position> parquetHelper = new ParquetHelper<>();

        Schema avroSchema = parquetHelper.schemaFromObjectWithNull(Position.class);

        try (ParquetWriter<Position> parquetWriter = parquetHelper.writer(avroSchema, filename, conf)) {
            IntStream.range(0, 10).boxed()
                .map(ParquetHelperTest::record)
                .forEach(r -> {
                    try {
                        parquetWriter.write(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }

        try (ParquetReader<Position> parquetReader = parquetHelper.reader(filename, conf)) {
            Position read;
            while ((read = parquetReader.read()) != null) {
                System.err.println("--------");
                System.err.println(read);

            }
        }
    }

    private static Position record(int id) {
        Position position = new Position(id, id * id * 1000, "name" + id, new HashMap<>());
        position.getRiskfactors().put("riskafactorKey1", "riskfactorValue1");
        position.getRiskfactors().put("riskafactorKey2", "riskfactorValue2");
        position.getRiskfactors().put("riskafactorKey3", "riskfactorValue3");

        return position;
    }
}
