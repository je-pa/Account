package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)// 확장팩
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountSuccess(){
        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("438241200").build())); // 여기서 리턴된값
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("2").build());

        // 이 캡터는 accountRepository의 save 메서드에 전달되는 인수를 캡처하는 데 사용됩니다.
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        // when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);
        // then
        // accountRepository의 save 메서드가 정확히 한 번 호출되었는지 확인하는 데 사용됩니다.
        //captor.capture() 부분은 save 메서드에 전달된 인수를 캡처합니다.
        verify(accountRepository,times(1)).save(captor.capture());




        assertEquals(12L, accountDto.getUserId());
        assertEquals("2", accountDto.getAccountNumber());
        assertEquals("438241201", captor.getValue().getAccountNumber());
    }

    @Test
    void createFirstAccountSuccess(){
        // given
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty()); // 없는 경우
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("2").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        // when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);
        // then
        verify(accountRepository,times(1)).save(captor.capture());

        assertEquals(15L, accountDto.getUserId());
        assertEquals("2", accountDto.getAccountNumber());
        assertEquals("1000000000", captor.getValue().getAccountNumber());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계죄 생성 실패")
    void createAccount_userNotFound(){
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));
        // then

        assertEquals(ErrorCode.USER_NOT_FOUNT, accountException.getErrorCode());
    }

    @Test
    @DisplayName("유저당 최대 계좌 10개")
    void createAccount_maxAccountIs10(){
        // given
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        // then
        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, accountException.getErrorCode());
    }

    @Test
    void deleteAccountSuccess(){
        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(0L)
                        .accountNumber("438241200").build()));
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        // when
        AccountDto accountDto = accountService.deleteAccount(1L, "0987654321");
        // then
        verify(accountRepository,times(1)).save(captor.capture());

        assertEquals(12L, accountDto.getUserId());
        assertEquals("438241200", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계죄 해지 실패")
    void deleteAccount_userNotFound(){
        // given
        given( accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));
        // then

        assertEquals(ErrorCode.USER_NOT_FOUNT, accountException.getErrorCode());
    }
    @Test
    @DisplayName("해당 계좌 없음 - 계죄 해지 실패")
    void deleteAccount_AccountNotFound(){
        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));
        // then

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUNT, accountException.getErrorCode());
    }

    @Test
    @DisplayName("해당 계좌 소유주 다름 - 계죄 해지 실패")
    void deleteAccount_userUnMatch(){
        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        AccountUser otherUser = AccountUser.builder()
                .id(13L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(otherUser)
                        .balance(0L)
                        .accountNumber("438241200").build()));
        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));
        // then

        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, accountException.getErrorCode());
    }

    @Test
    @DisplayName("잔액 남음 - 해지 실패")
    void deleteAccount_balanceNotEmpty(){
        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(100L)
                        .accountNumber("438241200").build()));
        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));
        // then

        assertEquals(ErrorCode.BALANCE_NOT_EMPTY, accountException.getErrorCode());
    }

    @Test
    @DisplayName("이미 해지 계좌 - 해지 실패")
    void deleteAccount_accountAlreadyUnregistered(){
        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(0L)
                        .accountNumber("438241200").build()));
        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));
        // then

        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, accountException.getErrorCode());
    }

    @Test
    void successGetAccountsByUserId(){
        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Pobi").build();
        List<Account> accounts =
                Arrays.asList(Account.builder()
                                .accountNumber("1234567890")
                                .balance(1000L)
                                .accountUser(user)
                                .build(),
                        Account.builder()
                                .accountNumber("1234567891")
                                .balance(2000L)
                                .accountUser(user)
                                .build(),
                        Account.builder()
                                .accountNumber("1234567892")
                                .balance(3000L)
                                .accountUser(user)
                                .build()
                );
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);
        // when
        List<AccountDto> accountDtos = accountService.getAccountByUserId(1L);

        assertEquals(3, accountDtos.size());
        assertEquals("1234567890", accountDtos.get(0).getAccountNumber());
        assertEquals(1000, accountDtos.get(0).getBalance());
        assertEquals(3, accountDtos.size());
        assertEquals("1234567891", accountDtos.get(1).getAccountNumber());
        assertEquals(2000, accountDtos.get(1).getBalance());
        assertEquals(3, accountDtos.size());
        assertEquals("1234567892", accountDtos.get(2).getAccountNumber());
        assertEquals(3000, accountDtos.get(2).getBalance());
        // then
    }

    @Test
    void failedToGetAccounts(){
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        // when
        // then
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.getAccountByUserId(1L));
        // then

        assertEquals(ErrorCode.USER_NOT_FOUNT, accountException.getErrorCode());
    }
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

}