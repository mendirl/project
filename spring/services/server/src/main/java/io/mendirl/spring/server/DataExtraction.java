package io.mendirl.spring.server;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.zip.ZipInputStream;

@Service
@Log4j2
public class DataExtraction<T> {

    public List<T> extract(DataBuffer dataBuffer, Class<T> clazz) {
        log.info("lets go to extract data");
        try {
            return unzip(dataBuffer, reader -> convertData(reader, clazz));
        } catch (IOException e) {
            log.error("impossible to extract data", e);
        }

        return Collections.emptyList();
    }

    private List<T> unzip(DataBuffer dataBuffer, Function<Reader, List<T>> function) throws IOException {
        var result = new ArrayList<T>();

        try (var fis = dataBuffer.asInputStream();
             var bis = new BufferedInputStream(fis);
             var stream = new ZipInputStream(bis);
             var reader = reader(stream, StandardCharsets.ISO_8859_1)) {

            while (stream.getNextEntry() != null) {
                result.addAll(function.apply(reader));
            }
        }
        return result;
    }

    private List<T> convertData(Reader reader, Class<T> clazz) {
        return new CsvToBeanBuilder<T>(reader)
            .withType(clazz)
            .withSeparator('\t')
            .build().parse();
    }

    private Reader reader(InputStream inputStream, Charset cs) {
        CharsetDecoder decoder = cs.newDecoder();
        Reader reader = new InputStreamReader(inputStream, decoder);
        return new Eco2MixBufferedReader(reader);
    }


    /**
     * Content of the file needs to be fixed before parsed.
     */
    static class Eco2MixBufferedReader extends BufferedReader {
        public Eco2MixBufferedReader(Reader in) {
            super(in);
        }

        @Override
        public String readLine() throws IOException {
            var oldLine = super.readLine();

            // line can contain a last '\t' irrelevant
            var lastChar = oldLine.charAt(oldLine.length() - 1);
            if (lastChar == '\t') {
                return oldLine.substring(0, oldLine.length() - 1);
            }

            // the last line is not data, but warning
            if (oldLine.startsWith("RTE ne pourra")) {
                return super.readLine();
            }

            return oldLine;
        }
    }

}
