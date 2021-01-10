package io.mendirl.spring.server;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Log4j2
public class DataExtraction<T> {

    public List<T> extract(DataBuffer dataBuffer, Class<T> clazz) {
        log.info("lets go to extract data");
        try {
            return convertData(unzip(persistResponse(dataBuffer)), clazz);
        } catch (IOException e) {
            log.error("impossible to extract data", e);
        }

        return Collections.emptyList();
    }

//    private Path clean(Path path) throws IOException {
//        var temporaryCsvFile = createTemporaryCsvFile();
//
//        try (var in = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1);
//             var out = Files.newBufferedWriter(temporaryCsvFile)) {
//
//            var collect = in.lines().collect(Collectors.toList());
//
//            in.lines().map(String::trim).forEach(line -> {
//                try {
//                    out.write(line);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//
////        return temporaryCsvFile;
//        return path;
//    }

    private Path createTemporaryDownloadedFile() throws IOException {
        return Files.createTempFile("eco2mix-", ".zip");
    }

    private Path createTemporaryExcelFolder() throws IOException {
        return Files.createTempDirectory("eco2mix-");
    }

    private Path createTemporaryCsvFile() throws IOException {
        return Files.createTempFile("eco2mix-", ".csv");
    }

    private Path persistResponse(DataBuffer dataBuffer) throws IOException {
        var path = createTemporaryDownloadedFile();
        try (var out = Files.newOutputStream(path)) {
            StreamUtils.copy(dataBuffer.asInputStream(), out);
        }

        return path;
    }

    private Path unzip(Path path) throws IOException {
        var temporaryExcelFolder = createTemporaryExcelFolder();

        try (var fis = Files.newInputStream(path);
             var bis = new BufferedInputStream(fis);
             var stream = new ZipInputStream(bis)) {

            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {

                var filePath = temporaryExcelFolder.resolve(entry.getName());

                try (var fos = Files.newOutputStream(filePath);
                     var bos = new BufferedOutputStream(fos, StreamUtils.BUFFER_SIZE)) {
                    StreamUtils.copy(stream, bos);
                }
            }
        }
        return firstFile(temporaryExcelFolder);
    }


    private Path firstFile(Path folder) {
        return Path.of(Objects.requireNonNull(folder.toFile().listFiles())[0].getAbsolutePath());
    }

    private List<T> convertData(Path file, Class<T> clazz) throws IOException {
        try (var reader = Files.newBufferedReader(file, StandardCharsets.ISO_8859_1)) {
            return new CsvToBeanBuilder<T>(reader)
                .withType(clazz)
                .withSeparator('\t')
                .build().parse();
        }
    }
}
