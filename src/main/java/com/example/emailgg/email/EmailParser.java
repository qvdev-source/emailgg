package com.example.emailgg.email;


public interface EmailParser {
    Topup parse(String text, String messageId);
}
