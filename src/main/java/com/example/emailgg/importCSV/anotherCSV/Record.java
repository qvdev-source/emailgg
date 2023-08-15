package com.example.emailgg.importCSV.anotherCSV;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;

@Data
public class Record {
    private String imageUrl;
    // ... các thuộc tính khác

    public Object[] toObjectArray() {
        return new Object[]{imageUrl /*, ... các giá trị khác*/};
    }
}

    // Trong phương thức processCsvFile


