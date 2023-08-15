package com.example.emailgg.email;

import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailMessage;
import com.sun.mail.gimap.GmailRawSearchTerm;
import com.sun.mail.gimap.GmailStore;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.SearchTerm;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;


public class ReadMailExample {


    public static void check(String host, String storeType, String user, String password) {
        try {

            // create properties
            Properties properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");
            properties.put("mail.imap.ssl.trust", host);

            Session emailSession = Session.getDefaultInstance(properties);
            // create the Gimap store object and connect to the imap server
            GmailStore store = new GmailStore(emailSession, new URLName("993", "imap.gmail.com", -1, null, user, password));
            store.connect(host, user, password);
            Date now = new Date();

            // create the inbox object and open it
            GmailFolder inbox = (GmailFolder) store.getFolder("Vietdq");
            inbox.open(Folder.READ_WRITE);

            // Build the query
            long fiveMinutesAgo = Instant.now().minusSeconds(5 * 60).getEpochSecond();
            String query = "after:" + fiveMinutesAgo;
//            SearchTerm searchTerm = new GmailRawSearchTerm("label:Vietdq -label:vietdq/done "+ query);
            SearchTerm searchTerm = new GmailRawSearchTerm("label:Vietdq");

            // perform the search and get the results
            Message[] messages = inbox.search(searchTerm);

            System.out.println("messages.length---" + messages.length);
            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                GmailMessage gmsg = (GmailMessage) message;
                gmsg.setLabels(new String[]{"Vietdq/done"}, true);
                if (message.getContent() instanceof Multipart) {
                    Multipart multipart = (Multipart) message.getContent();
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        try {
                            if (bodyPart.isMimeType("text/plain")) {
                                Topup topup = checkFrom(bodyPart.getContent().toString(), ((MimeMessage) message).getMessageID());
                                System.out.println(topup);
                            }
                        } catch (Exception e) {
                            System.out.println("Parse sai");
                            ;
                        }
                    }
                }
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Topup fromTextPingPong(String text, String messageId) {
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

    public static Topup fromTextPayOneer(String text, String messageId) {
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
        } catch (Exception e) {
            return Topup.builder().hasBug(1).build();
        }

        return Topup.builder().amount(amountString).username(username).transactionId(transactionId).content(null).messageId(messageId).build();
    }

    public static Topup fromTextLianLian(String text, String messageId) {
        System.out.println(text);
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
        } catch (Exception e) {
            return Topup.builder().hasBug(1).messageId(messageId).build();
        }
        return Topup.builder().amount(amountString).username(username).transactionId(transactionId).content(content).messageId(messageId).build();
    }


    private static Topup checkFrom(String text, String messageId) {
        String[] lines = text.split("\n");
        List<String> list = Arrays.asList(lines);
        for (String s:list) {
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

    private static String handleAmount(String str) {
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

    public static void main(String[] args) {

        String host = "imap.gmail.com";
        String mailStoreType = "imap";
        String username = "hienquoc412018@gmail.com";
        String password = "vumpmssqlqjrysxf";

        check(host, mailStoreType, username, password);


    }
}
