package com.example.emailgg.useSpringBatch;

import org.springframework.batch.item.ItemProcessor;

public class ImageProcessor implements ItemProcessor<Record, Record> {
    @Override
    public Record process(Record record) throws Exception {
        // Download ảnh từ oldUrl và upload lên S3
        String newUrl = uploadToS3(downloadImage(record.getOldUrl()));
        record.setNewUrl(newUrl);
        return record;
    }
}

