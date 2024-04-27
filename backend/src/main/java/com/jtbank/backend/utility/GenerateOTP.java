package com.jtbank.backend.utility;

import java.util.Random;

public class GenerateOTP {
    private GenerateOTP() {
    }

    // Generate 6 digit OTP
    public static String generateOtp() {
        Random random = new Random();
        int otpLength = 6;
        int min = (int) Math.pow(10, otpLength - 1);
        int max = (int) Math.pow(10, otpLength) - 1;

        // Generate a random number within the specified range
        int otp = random.nextInt(max - min + 1) + min;

        return String.format("%06d", otp);
    }
}
