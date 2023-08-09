package com.example.emailgg;

import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailMessage;
import com.sun.mail.gimap.GmailStore;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

            // create the imap store object and connect to the imap server
            GmailStore store = new GmailStore(emailSession, new URLName("993", "imap.gmail.com", -1, null, user, password));

            store.connect(host, user, password);

            // create the inbox object and open it
            // get today's date
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, 0); // set hour to midnight
//            calendar.set(Calendar.MINUTE, 0); // set minute in hour
//            calendar.set(Calendar.SECOND, 0); // set second in minute
//            calendar.set(Calendar.MILLISECOND, 0); // set millisecond in second
//
//            Date today = calendar.getTime(); // get current date
//
//// create the inbox object and open it
//            Folder inbox = store.getFolder("Vietdq");
//            inbox.open(Folder.READ_WRITE);
//
//// create a search term for all "unseen" messages
//            Flags seen = new Flags(Flags.Flag.SEEN);
//            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
//
//// create a search term for all recent messages
//            ReceivedDateTerm receivedDateTerm = new ReceivedDateTerm(ComparisonTerm.EQ, today);
//
//// create a search term that combines the two
//            SearchTerm searchTerm = new AndTerm(unseenFlagTerm, receivedDateTerm);
//
//// perform the search and get the results
//            Message[] messages = inbox.search(searchTerm);
            // get current time
            Date now = new Date();

// get 30 minutes ago
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.MINUTE, -30);
            Date thirtyMinutesAgo = calendar.getTime();

// create the inbox object and open it

            GmailFolder inbox = (GmailFolder) store.getFolder("Vietdq");
            inbox.open(Folder.READ_WRITE);

//// create a search term for all "unseen" messages
//            Flags seen = new Flags(Flags.Flag.SEEN);
//            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

// create a search term for all messages received in the last 30 minutes
            ReceivedDateTerm receivedDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, thirtyMinutesAgo);

// create a search term that combines the two
            SearchTerm searchTerm = new ReceivedDateTerm(ComparisonTerm.EQ, today);

// perform the search and get the results
//            inbox.doCommand()
            Message[] messages = inbox.search(searchTerm);



            System.out.println("messages.length---" + messages.length);
            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
//                System.out.println("---------------------------------");
//                System.out.println("Email Number " + (i + 1));
//                System.out.println("Subject: " + message.getSubject());
//                System.out.println("From: " + message.getFrom()[0]);

                GmailMessage gmsg = (GmailMessage) message;
                gmsg.setLabels(new String[]{"[Vietdq]/done"}, true);
//                GmailMessage gmsg = (GmailMessage) message;
//                gmsg.setLabels(new String[]{"Vietdq"}, false);


                if (message.getContent() instanceof Multipart) {
                    Multipart multipart = (Multipart) message.getContent();
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        try {
                            if (bodyPart.isMimeType("text/plain")) {
                                long start = System.currentTimeMillis();
                                System.out.println(bodyPart.getContent().toString());
                                Topup topup = checkFrom(bodyPart.getContent().toString(), ((MimeMessage) message).getMessageID());
                                System.out.println(topup);
                                long end = System.currentTimeMillis();
                                System.out.println("Total =====>" + (end-start));
                                if (topup.getContent() != null && topup.getAmount() != null && topup.getTransactionId() != null && topup.getUsername() != null) {
                                    break;
                                }
                            }
                        }
                        catch (Exception e) {
                            System.out.println("Parse sai");;
                        }
                    }
                }
            }

            inbox.close(false);
            store.close();

        }
         catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Topup fromTextPingPong(String text, String messageId) {
        Double amountString = null;
        String username = null;
        String transactionId = null;
        String[] lines = text.split("\n");
        try{
            for (String line : lines) {
                if (line.startsWith("Amount:")) {
                    amountString = Double.parseDouble(line.split(" ")[3].replace("*", ""));
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
                    amountString = Double.parseDouble(line.substring(line.indexOf("$") + 1).trim());
                } else if (line.startsWith("Được gửi bởi ")) {
                    username = line.substring(14).trim();
                } else if (line.startsWith("ID Thanh toán ")) {
                    transactionId = line.substring(15).trim();
                } else if (line.startsWith("Thanh toán cho ")) {
                     line.substring(15).trim();
                }
            }
        }catch (Exception e) {
            return Topup.builder().hasBug(1).build();
        }

        return Topup.builder().amount(amountString).username(username).transactionId(transactionId).content(null).messageId(messageId).build();
    }

    public static Topup fromTextLianLian(String text, String messageId) {
        Double amountString = null;
        String username = null;
        String transactionId = null;
        String content  = null;
        String[] lines = text.split("\n");
        try{
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith("Số tiền")) {
                    String[] parts = lines[i+2].split(" ");
                amountString = Double.parseDouble(parts[0].trim());
//                    amountString = Double.parseDouble("145");
                } else if (lines[i].startsWith("Mục đích thanh toán")) {
                    content = lines[i+2].trim();
                } else if (lines[i].startsWith("Mã giao dịch")) {
                    transactionId = lines[i+2].trim();
                } else if (lines[i].startsWith("Quý khách đã nhận được thanh toán từ Người dùng")) {
                    int startIndex = lines[i].indexOf('*') + 1;
                    int endIndex = lines[i].indexOf('*', startIndex);
                    username = lines[i].substring(startIndex, endIndex).trim();
                }
            }
        }catch (Exception e) {
            return Topup.builder().hasBug(1).messageId(messageId).build();
        }

        return Topup.builder().amount(amountString).username(username).transactionId(transactionId).content(content).messageId(messageId).build();
    }



    private static Topup checkFrom(String text, String messageId) {
        String[] lines = text.split("\n");
        if (lines[1].contains("PingPong")) {
            return fromTextPingPong(text, messageId);
        } else if (lines[1].contains("Payoneer")) {
            return fromTextPayOneer(text, messageId);
        } else if (lines[1].contains("LianLian")) {
            return fromTextLianLian(text, messageId);
        }
        return Topup.builder().build();
    }

    public static void main(String[] args) {

        String host = "imap.gmail.com";
        String mailStoreType = "imap";
        String username = "hienquoc412018@gmail.com";
        String password = "vumpmssqlqjrysxf";

        check(host, mailStoreType, username, password);

    }
}
