package com.example.emailgg;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Topup {

    private Double amount;

    private String transactionId;

    private String username;

    private String content;

    private String messageId;

    private int hasBug = 0;
}
