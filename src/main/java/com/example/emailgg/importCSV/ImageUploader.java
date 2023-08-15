package com.example.emailgg.importCSV;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ImageUploader {

    private static final String BUCKET_NAME = "your-bucket-name";
    private AmazonS3 s3Client;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public ImageUploader() {
        s3Client = AmazonS3ClientBuilder.standard().build();
    }

//    @Async("taskExecutor")
//    public CompletableFuture<List<String>> uploadImagesFromCSV(String csvPath) throws IOException {
//        List<String> imageUrls = readImageUrlsFromCSV(csvPath);
//        List<String> newUrls = Collections.synchronizedList(new ArrayList<>());
//        AtomicInteger id = new AtomicInteger(1);
//        // Sử dụng phương thức parallelStream và forEachOrdered để xử lý song song và duy trì thứ tự
//        imageUrls.parallelStream().forEachOrdered(imageUrl -> {
//            try {
//                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
//                File file = new File(fileName);
//                FileUtils.copyURLToFile(new URL(imageUrl), file);
//                // ở đây cần xử lý thêm convert ảnh từ webp sang img
//                s3Client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, file));
//                String newUrl = getS3Url(fileName);
//                newUrls.add(newUrl);
//                updateProgressTable(id.get(), imageUrl, newUrl, "COMPLETED");
//                file.delete();
//                id.incrementAndGet();
//            } catch (IOException e) {
//                updateProgressTable(id.get(), imageUrl, null, "ERROR");
//                e.printStackTrace();
//            }
//        });
//        return CompletableFuture.completedFuture(newUrls); // Trả về danh sách URL mới
//    }

    @Async("taskExecutor")
    public CompletableFuture<Map<Integer, Map<String, String>>> uploadImagesFromCSV(String csvPath) throws IOException {
        List<String> imageUrls = readImageUrlsFromCSV(csvPath);
        Map<Integer, Map<String, String>> resultMap = new ConcurrentHashMap<>();
        resultMap.put(1, new ConcurrentHashMap<>()); // URLs thành công
        resultMap.put(0, new ConcurrentHashMap<>()); // URLs lỗi

        // Chia danh sách thành các phần nhỏ
        List<List<String>> subLists = Lists.partition(imageUrls, 10);

        for (List<String> subList : subLists) {
            executorService.submit(() -> {
                for (String imageUrl : subList) {
                    try {
                        // Logic để tải xuống và tải lên ảnh
                        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                        File file = new File(fileName);
                        FileUtils.copyURLToFile(new URL(imageUrl), file);
                        // ở đây cần xử lý thêm convert ảnh từ webp sang img
                        s3Client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, file));
                        String newUrl = getS3Url(fileName); // Lấy URL mới từ S3
                        resultMap.get(1).put(imageUrl, newUrl); // Thêm vào kết quả thành công
                    } catch (Exception e) {
                        resultMap.get(0).put(imageUrl, null); // Thêm vào kết quả lỗi
                        e.printStackTrace();
                    }
                }
            });
        }

        executorService.shutdown();

        return CompletableFuture.completedFuture(resultMap); // Trả về kết quả bất đồng bộ
    }




    // Phương thức để đọc URL ảnh từ CSV
    private List<String> readImageUrlsFromCSV(String csvPath) {
        // Cài đặt mã để đọc URL ảnh từ CSV
        // ...
        return null;
    }

    public String getS3Url(String fileName) {
        return s3Client.getUrl(BUCKET_NAME, fileName).toString();
    }

    private void updateProgressTable(int id, String imageUrl, String newUrl, String status) {
        try (Connection connection = DriverManager.getConnection("your-jdbc-url", "username", "password")) {
            PreparedStatement ps = connection.prepareStatement("UPDATE TEMP_UPLOAD_PROGRESS SET IMAGE_URL = ?, NEW_URL = ?, STATUS = ? WHERE ID = ?");
            ps.setString(1, imageUrl);
            ps.setString(2, newUrl);
            ps.setString(3, status);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCsvWithNewUrls(String csvPath, List<String> newUrls) throws IOException {
        // Đọc tệp CSV và thay thế cột URL cũ bằng URL mới từ danh sách newUrls
        // ...
    }



}

