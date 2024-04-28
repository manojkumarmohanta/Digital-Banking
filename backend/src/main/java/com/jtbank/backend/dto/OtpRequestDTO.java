package com.jtbank.backend.dto;

import jakarta.validation.constraints.NotNull;

public record OtpRequestDTO(
        @NotNull(message = "Otp should not be Null")
        String otp
) {
}
