package com.example.account.service;

import com.example.account.domain.Account;
import com.example.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)// 확장팩
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("계좌 조회 성공")
    void testXXX(){
        // given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));
        // when
        Account account = accountService.getAccount(4555L);

        // then
        // verify
        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, times(0)).save(any());

        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());

        // ArgumentCaptor 사용
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(accountRepository, times(1)).findById(captor.capture());
        assertEquals(4555L, captor.getValue());
        assertNotEquals(45551L, captor.getValue());
        assertTrue(4555L == captor.getValue());
    }

    @Test
    @DisplayName("계좌 조회 실패 - 음수 조회")
    void testFailedToSearchAccount(){
        // given
        // when
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> accountService.getAccount(-10L));

        // then
        // verify
        assertEquals("Minus", runtimeException.getMessage());
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