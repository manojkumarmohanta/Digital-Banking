package com.jtbank.backend.model;

import com.jtbank.backend.constant.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Embeddable
@Data
public class Credential {
    @Column(unique = true, length = 50, nullable = false)
    private String accountEmail;
    @Column(nullable = false)
    private String accountPassword;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
}
