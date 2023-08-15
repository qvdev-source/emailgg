package com.example.emailgg.importCSV;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private final AmazonS3 s3client = AmazonS3ClientBuilder.standard().build();
    private final String bucketName = "your_bucket_name";

    private static final int THREAD_POOL_SIZE = 100; // Số lượng thread trong pool, có thể điều chỉnh
    private final Executor executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

//    public List<ImageResult> processUrls(List<String> urls) {
//        List<CompletableFuture<ImageResult>> futures = urls.stream()
//                .map(url -> CompletableFuture.supplyAsync(() -> downloadAndUpload(url), executor))
//                .collect(Collectors.toList());
//
//        return futures.stream()
//                .map(CompletableFuture::join)
//                .collect(Collectors.toList());
//    }

    private ImageResult downloadAndUpload(String url) {
        ImageResult result = new ImageResult();
        result.setOldUrl(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    try (InputStream inputStream = response.getEntity().getContent()) {
                        String keyName = url.substring(url.lastIndexOf('/') + 1);
                        s3client.putObject(bucketName, keyName, inputStream, null);
                        result.setNewUrl("https://" + bucketName + ".s3.amazonaws.com/" + keyName);
                    }
                }
            }
        } catch (Exception e) {
            // Log lỗi nếu cần
            e.printStackTrace();
            result.setNewUrl(null); // Đánh dấu URL này bằng null, biểu thị lỗi
        }

        return result;
    }

    public List<ImageResult> processUrls(List<String> urls) {
        List<CompletableFuture<ImageResult>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> downloadAndUpload(url), executor))
                .toList();

        List<ImageResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        List<ImageResult> successList = results.stream()
                .filter(result -> result.getNewUrl() != null)
                .toList();

        List<ImageResult> failureList = results.stream()
                .filter(result -> result.getNewUrl() == null)
                .toList();
        return successList;
        // Trả về hoặc xử lý successList và failureList tùy ý
    }



    @Data
    public static class ImageResult {
        private String oldUrl;
        private String newUrl;

        // Getters và Setters
    }
}

