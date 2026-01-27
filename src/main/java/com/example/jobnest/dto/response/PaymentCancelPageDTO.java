package com.example.jobnest.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * Single view-model for payment cancel page.
 */
@Data
@Builder
public class PaymentCancelPageDTO {
    private Integer jobId;
}

