package com.example.account.repository;

import com.example.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> { //<레파지토리가 활용하게될 entity, pk 타입>
    Optional<Account> findFirstByOrderByIdDesc();
}
