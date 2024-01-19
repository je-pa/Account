package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest//di
class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @BeforeEach
    void init() {
        accountService.createAccount();
    }
    @Test
    @DisplayName("Test 이름 변경")
    void testGetAccount(){
        // given

        Account account = accountService.getAccount(1L);
        // when
        // then
        assertEquals("40000",account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE,account.getAccountStatus());
    }
    @Test
    void testGetAccount2(){
        // given

        Account account = accountService.getAccount(2L);
        // when
        // then
        assertEquals("40000",account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE,account.getAccountStatus());
    }
}