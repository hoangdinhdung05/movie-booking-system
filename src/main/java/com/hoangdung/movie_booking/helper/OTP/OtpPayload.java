package com.hoangdung.movie_booking.helper.OTP;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents OTP data including the code,
 * number of resend attempts, verification attempts, and creation timestamp.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpPayload {
    private String code;
    private int resendCount;
    private int attempts;
    private long createdAt;

    /** Increments the number of verification attempts. */
    public void incrementAttempts() {
        this.attempts++;
    }

    /** Increments the number of times OTP was resent. */
    public void incrementResend() {
        this.resendCount++;
    }
}
