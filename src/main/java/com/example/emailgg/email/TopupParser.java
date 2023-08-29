package com.example.emailgg.email;

import java.util.Arrays;
import java.util.List;

public class TopupParser {

    public Topup parseFromEmail(String text, String messageId) {
        String[] lines = text.split("\n");
        List<String> list = Arrays.asList(lines);
        for (String s : list) {
            if (s.contains("PingPong")) {
                return fromTextPingPong(text, messageId);
            } else if (s.contains("Payoneer")) {
                return fromTextPayOneer(text, messageId);
            } else if (s.contains("LianLian")) {
                return fromTextLianLian(text, messageId);
            }
        }
        return Topup.builder().build();
    }

    private Topup fromTextPingPong(String text, String messageId) {
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
        } catch (Exception e) {
            return Topup.builder().hasBug(1).build();
        }
        return Topup.builder().amount(amountString).username(username).transactionId(transactionId).content(null).messageId(messageId).build();
    }

    private Topup fromTextPayOneer(String text, String messageId) {
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
                }
            }
        } catch (Exception e) {
            return Topup.builder().hasBug(1).build();
        }

        return Topup.builder().amount(amountString).username(username).transactionId(transactionId).content(null).messageId(messageId).build();
    }

    private Topup fromTextLianLian(String text, String messageId) {
        Double amountString = null;
        String username = null;
        String transactionId = null;
        String content = null;
        String[] lines = text.split("\n");
        try {
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith("Kính chào Quý khách LEADSGEN MEDIA AND ONLINE SOLUTION COMPANY LIMITED")) {
                    if (amountString == null){
                        amountString = Double.parseDouble(handleAmount(lines[i + 7].trim()));
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
        } catch (Exception e) {
            return Topup.builder().hasBug(1).messageId(messageId).build();
        }
        return Topup.builder().amount(amountString).username(username).transactionId(transactionId).content(content).messageId(messageId).build();
    }

    private String handleAmount(String str) {
        int firstDigitIndex = -1;
        int lastDigitIndex = -1;

        // Tìm index của ký tự số đầu tiên
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                firstDigitIndex = i;
                break;
            }
        }

        // Tìm index của ký tự số cuối cùng
        for (int i = str.length() - 1; i >= 0; i--) {
            if (Character.isDigit(str.charAt(i))) {
                lastDigitIndex = i;
                break;
            }
        }
        return str.substring(firstDigitIndex, lastDigitIndex+1).replace(",", "");
    }
}
