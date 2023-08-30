package com.example.emailgg.email;

import java.util.Arrays;
import java.util.List;

public class TopupParser {

    private List<EmailParser> parsers;

    public TopupParser() {
        parsers = Arrays.asList(new PingPongParser(), new PayoneerParser(), new LianLianParser());
    }

    public Topup parseFromEmail(String text, String messageId) {
        for (EmailParser parser : parsers) {
            Topup topup = parser.parse(text, messageId);
            if (topup != null) {
                return topup;
            }
        }
        return Topup.builder().build();
    }
}
