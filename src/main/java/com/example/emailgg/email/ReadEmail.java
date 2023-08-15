package com.example.emailgg.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;

import java.util.Properties;

public class ReadEmail {

    public static void main(String[] args) {
        // Set email properties
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.ssl.enable", "true"); // required for Gmail
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", "993");

        // Set email credentials
        String email = "your-email@gmail.com"; // Your email
        String password = "your-password"; // Your password

        try {
            // Connect to the Gmail IMAP server
            Session session = Session.getInstance(properties);
            Store store = session.getStore("imaps");
            store.connect(email, password);

            // Open the Inbox folder
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);

            // Retrieve messages
            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + InternetAddress.toString(message.getFrom()));
            }

            // Close the connection
            inbox.close(false);
            store.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
