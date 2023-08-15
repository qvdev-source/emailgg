package com.example.emailgg.importCSV.anotherCSV;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class ImageService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int THREAD_POOL_SIZE = 100;
    private final Executor executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final AmazonS3 s3client = AmazonS3ClientBuilder.standard().build();
    private final String bucketName = "your_bucket_name";

    public void processCsvFile(String csvFilePath) throws Exception {
        // Tạo file tạm để chứa kết quả
        Path tempFile = Files.createTempFile("updated", ".csv");

        // Đọc CSV
        try (CSVParser parser = new CSVParser(new FileReader(csvFilePath), CSVFormat.DEFAULT);
             CSVPrinter printer = new CSVPrinter(new FileWriter(tempFile.toFile()), CSVFormat.DEFAULT)) {

            List<CompletableFuture<Void>> futures = parser.getRecords().stream()
                    .map(record -> CompletableFuture.runAsync(() -> {
                        try {
                            processRecord(record, printer);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor))
                    .toList();

            // Chờ tất cả công việc hoàn thành
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        // Insert vào DB
//        try (CSVParser parser = new CSVParser(new FileReader(tempFile.toFile()), CSVFormat.DEFAULT)) {
//            for (CSVRecord record : parser.getRecords()) {
//                String imageUrl = record.get(0); // Giả sử URL là cột đầu tiên
//                // ... các cột khác
//                jdbcTemplate.update("INSERT INTO your_table (image_url, ...) VALUES (?, ...)", imageUrl /*, ...*/);
//            }
//        }

        List<Object[]> batchArgs = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new FileReader(tempFile.toFile()), CSVFormat.DEFAULT)) {
            for (CSVRecord csvRecord : parser.getRecords()) {
                Record record = new Record();
                record.setImageUrl(csvRecord.get(0));
                // ... thiết lập các thuộc tính khác
                batchArgs.add(record.toObjectArray());
            }
        }

        jdbcTemplate.batchUpdate("INSERT INTO your_table (image_url, ...) VALUES (?, ...)", batchArgs);
    }

    private void processRecord(CSVRecord record, CSVPrinter printer) throws IOException {
        String oldUrl = record.get(0); // Giả sử URL là cột đầu tiên
        String newUrl = downloadAndUpload(oldUrl);
        // In vào CSV mới
        printer.printRecord(newUrl /*, ... các cột khác*/);
    }

    private String downloadAndUpload(String oldUrl) {
        return null;
    }

//    private String downloadAndUpload(String url) {
//        // Logic download và upload như trước
//        // ...
//        com.example.emailgg.importCSV.ImageService.ImageResult result = new com.example.emailgg.importCSV.ImageService.ImageResult();
//        result.setOldUrl(url);
//
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            HttpGet request = new HttpGet(url);
//            try (CloseableHttpResponse response = httpClient.execute(request)) {
//                if (response.getStatusLine().getStatusCode() == 200) {
//                    try (InputStream inputStream = response.getEntity().getContent()) {
//                        String keyName = url.substring(url.lastIndexOf('/') + 1);
//                        s3client.putObject(bucketName, keyName, inputStream, null);
//                        result.setNewUrl("https://" + bucketName + ".s3.amazonaws.com/" + keyName);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            // Log lỗi nếu cần
//            e.printStackTrace();
//            result.setNewUrl(null); // Đánh dấu URL này bằng null, biểu thị lỗi
//        }
//
//        return result;
//    }
}

