package com.example.emailgg.compare;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

public class MockDataListener extends AnalysisEventListener<MockDataRow> {

    @Override
    public void invoke(MockDataRow data, AnalysisContext context) {
        // Xử lý từng dòng dữ liệu tại đây
        System.out.println("Đọc dòng: " + data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // Có thể thực hiện một số hoạt động sau khi đọc xong file
    }
}

