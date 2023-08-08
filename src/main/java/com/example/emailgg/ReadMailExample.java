package com.example.emailgg;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeMessage;
import javax.mail.search.*;

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
            Store store = emailSession.getStore("imaps");

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
            Folder inbox = store.getFolder("Vietdq");
            inbox.open(Folder.READ_WRITE);

// create a search term for all "unseen" messages
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

// create a search term for all messages received in the last 30 minutes
            ReceivedDateTerm receivedDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, thirtyMinutesAgo);

// create a search term that combines the two
            SearchTerm searchTerm = new AndTerm(unseenFlagTerm, receivedDateTerm);

// perform the search and get the results
            Message[] messages = inbox.search(searchTerm);

            System.out.println("messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                message.setFlag(Flag.SEEN, true);
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                if (message.getContent() instanceof Multipart) {
                    Multipart multipart = (Multipart) message.getContent();
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        if (bodyPart.isMimeType("text/plain")) {
                            System.out.println("Text: " + bodyPart.getContent().toString());
                        } else if (bodyPart.isMimeType("text/html")) {
                            System.out.println("HTML Text: " + bodyPart.getContent().toString());
                        }
                    }
                } else {
                    System.out.println("Text: " + message.getContent().toString());
                }
                String messageId = ((MimeMessage) message).getMessageID();
                System.out.println("Message-ID: " + messageId);

            }

            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String host = "imap.gmail.com";
        String mailStoreType = "imap";
        String username = "hienquoc412018@gmail.com";
        String password = "vumpmssqlqjrysxf";

        check(host, mailStoreType, username, password);

    }
}
