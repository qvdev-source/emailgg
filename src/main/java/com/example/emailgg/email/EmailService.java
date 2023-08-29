package com.example.emailgg.email;

import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailStore;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.search.SearchTerm;

import java.util.Properties;

public class EmailService {

    public Message[] fetchEmails(String host, String user, String password, SearchTerm searchTerm) {
        try {
            Properties properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");
            properties.put("mail.imap.ssl.trust", host);

            Session emailSession = Session.getDefaultInstance(properties);
            GmailStore store = new GmailStore(emailSession, null);
            store.connect(host, user, password);

            GmailFolder inbox = (GmailFolder) store.getFolder("Vietdq");
            inbox.open(Folder.READ_WRITE);

            return inbox.search(searchTerm);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message[0];
        }
    }
}

