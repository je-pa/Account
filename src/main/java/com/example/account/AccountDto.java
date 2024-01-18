package com.example.account;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountDto {
    private String accountNumber;
    private String nickname;
    private LocalDateTime registeredAt;
}
