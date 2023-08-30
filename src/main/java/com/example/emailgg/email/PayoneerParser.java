package com.example.emailgg.email;

import static com.example.emailgg.email.ReadMailExample.handleAmount;

public class PayoneerParser implements EmailParser{
    @Override
    public Topup parse(String text, String messageId) {
        Double amountString = null;
        String username = null;
        String transactionId = null;

        String[] lines = text.split("\n");
        try {
            for (String line : lines) {
                if (line.startsWith("Số tiền $")) {
                    amountString = Double.parseDouble(handleAmount(line.trim()));
                } else if (line.startsWith("Được gửi bởi ")) {
                    username = line.substring(14).trim();
                } else if (line.startsWith("ID Thanh toán ")) {
                    transactionId = line.substring(15).trim();
                } else if (line.startsWith("Thanh toán cho ")) {
                    line.substring(15).trim();
                }
            }
            return Topup.builder().amount(amountString).username(username).transactionId(transactionId).messageId(messageId).build();
        } catch (Exception e) {
            return Topup.builder().hasBug(1).build();
        }
    }
}
