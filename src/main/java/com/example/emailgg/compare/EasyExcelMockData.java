package com.example.emailgg.compare;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EasyExcelMockData {
    public static void main(String[] args) {
        String fileName = "mock_data.xlsx";
        EasyExcel.write(fileName, MockDataRow.class).sheet("Mock Data").doWrite(data());
    }

    public static List<MockDataRow> data() {
        List<MockDataRow> list = new ArrayList<>();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();

        for (int i = 0; i < 600000; i++) {
            MockDataRow row = new MockDataRow();
            row.setColumn1(generateRandomString(random, alphabet));
            row.setColumn2(generateRandomString(random, alphabet));
            row.setColumn3(generateRandomString(random, alphabet));
            row.setColumn4(generateRandomString(random, alphabet));
            row.setColumn5(generateRandomString(random, alphabet));
            row.setColumn6(generateRandomString(random, alphabet));
            row.setColumn7(generateRandomString(random, alphabet));
            row.setColumn8(generateRandomString(random, alphabet));
            row.setColumn9(generateRandomString(random, alphabet));
            row.setColumn10(generateRandomString(random, alphabet));
            row.setColumn11(generateRandomString(random, alphabet));
            row.setColumn12(generateRandomString(random, alphabet));
            row.setColumn13(generateRandomString(random, alphabet));
            row.setColumn14(generateRandomString(random, alphabet));
            row.setColumn15(generateRandomString(random, alphabet));
            row.setColumn16(generateRandomString(random, alphabet));
            row.setColumn17(generateRandomString(random, alphabet));
            row.setColumn18(generateRandomString(random, alphabet));
            row.setColumn19(generateRandomString(random, alphabet));
            row.setColumn20(generateRandomString(random, alphabet));
            // Thêm cột 3 đến 20
            list.add(row);
        }

        return list;
    }

    public static String generateRandomString(Random random, String alphabet) {
        StringBuilder cellValue = new StringBuilder(20);
        for (int charIdx = 0; charIdx < 20; charIdx++) {
            cellValue.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return cellValue.toString();
    }
}

