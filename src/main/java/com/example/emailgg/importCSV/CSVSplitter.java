package com.example.emailgg.importCSV;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class CSVSplitter {

    private static final int ROWS_PER_FILE = 10000; // Số dòng trong mỗi tệp nhỏ

    public static void splitCSV(String inputPath, String outputDirectory) throws IOException {
        try (CSVParser parser = new CSVParser(new FileReader(inputPath), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            List<CSVRecord> records = parser.getRecords();
            int totalRecords = records.size();
            int fileCounter = 0;

            for (int start = 0; start < totalRecords; start += ROWS_PER_FILE) {
                int end = Math.min(start + ROWS_PER_FILE, totalRecords);
                List<CSVRecord> subList = records.subList(start, end);

                String outputPath = Paths.get(outputDirectory, "output_" + fileCounter + ".csv").toString();
                try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath), CSVFormat.DEFAULT)) {
                    printer.printRecords(subList);
                }

                fileCounter++;
            }
        }
    }



    public static void main(String[] args) throws IOException {
        String inputPath = "path/to/your/largefile.csv";
        String outputDirectory = "path/to/output/directory";
        splitCSV(inputPath, outputDirectory);
        System.out.println("CSV files were successfully split!");
    }
}

