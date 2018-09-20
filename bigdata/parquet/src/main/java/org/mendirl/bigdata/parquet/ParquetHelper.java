package org.mendirl.bigdata.parquet;


import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.hadoop.util.HadoopOutputFile;

import java.io.IOException;

public class ParquetHelper<T> {

    public Schema schemaFromObject(Class<T> clazz) {
        return ReflectData.get().getSchema(clazz);
    }

    public Schema schemaFromObjectWithNull(Class<T> clazz) {
        return ReflectData.AllowNull.get().getSchema(clazz);
    }

    public ParquetWriter<T> writer(Schema schema, String filename, Configuration conf) throws IOException {
        return AvroParquetWriter.<T>builder(HadoopOutputFile.fromPath(new Path(filename), conf))
            .withSchema(schema)
            .withDataModel(ReflectData.get())
            .withConf(conf)
            .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
            .withCompressionCodec(CompressionCodecName.SNAPPY)
            .build();
    }

    public ParquetReader<T> reader(String filename, Configuration conf) throws IOException {
        return AvroParquetReader.<T>builder(HadoopInputFile.fromPath(new Path(filename), conf))
            .withDataModel(ReflectData.get())
            .disableCompatibility()
            .withConf(conf)
            .build();
    }
}
