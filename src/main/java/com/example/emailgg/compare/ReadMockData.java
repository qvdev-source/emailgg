package com.example.emailgg.compare;

import com.alibaba.excel.EasyExcel;

public class ReadMockData {
    public static void main(String[] args) {
        String fileName = "mock_data.xlsx";
        EasyExcel.read(fileName, MockDataRow.class, new MockDataListener()).sheet().doRead();
    }
}

