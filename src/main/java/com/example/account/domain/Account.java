package com.example.account.domain;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    private String accountNumber;


    @Enumerated(EnumType.STRING) // 0,1,2,3 저장이 아닌 문자 그대로 저장
    private AccountStatus accountStatus;
}
