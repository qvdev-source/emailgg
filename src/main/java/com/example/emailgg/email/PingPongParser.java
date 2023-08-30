package com.example.emailgg.email;

import static com.example.emailgg.email.ReadMailExample.handleAmount;

public class PingPongParser implements EmailParser {
    @Override
    public Topup parse(String text, String messageId) {
        Double amountString = null;
        String username = null;
        String transactionId = null;
        String[] lines = text.split("\n");
        try {
            for (String line : lines) {
                if (line.startsWith("Amount:")) {
                    amountString = Double.parseDouble(handleAmount(line.trim()));
                } else if (line.startsWith("From:")) {
                    username = line.substring(line.indexOf(' ') + 1).trim();
                } else if (line.startsWith("Transaction ID:")) {
                    transactionId = line.split(" ")[2].trim();
                }
            }
            return Topup.builder().amount(amountString).username(username).transactionId(transactionId).messageId(messageId).build();
        } catch (Exception e) {
            return Topup.builder().hasBug(1).build();
        }
    }
}
