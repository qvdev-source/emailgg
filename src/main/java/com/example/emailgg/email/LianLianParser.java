package com.example.emailgg.email;

import static com.example.emailgg.email.ReadMailExample.handleAmount;

public class LianLianParser implements EmailParser {
    @Override
    public Topup parse(String text, String messageId) {
        Double amountString = null;
        String username = null;
        String transactionId = null;
        String content = null;
        String[] lines = text.split("\n");
        try {
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith("Kính chào Quý khách LEADSGEN MEDIA AND ONLINE SOLUTION COMPANY LIMITED")) {
                    if (amountString == null){
                        amountString =Double.parseDouble(handleAmount(lines[i + 7].trim()));
                    }
                } else if (lines[i].startsWith("Mục đích thanh toán")) {
                    content = lines[i + 2].trim();
                } else if (lines[i].startsWith("Mã giao dịch")) {
                    transactionId = lines[i + 2].trim();
                } else if (lines[i].startsWith("Quý khách đã nhận được thanh toán từ Người dùng")) {
                    int startIndex = lines[i].indexOf('*') + 1;
                    int endIndex = lines[i].indexOf('*', startIndex);
                    username = lines[i].substring(startIndex, endIndex).trim();
                }
            }
            return Topup.builder().amount(amountString).username(username).transactionId(transactionId).content(content).messageId(messageId).build();
        } catch (Exception e) {
            return Topup.builder().hasBug(1).build();
        }
    }
}