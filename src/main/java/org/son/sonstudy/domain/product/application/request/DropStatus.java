package org.son.sonstudy.domain.product.application.request;

import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;

public enum DropStatus {
    SCHEDULED;

    public static DropStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException(ErrorCode.PARAMETER_TYPE_MISMATCH);
        }
        try {
            return DropStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.PARAMETER_TYPE_MISMATCH);
        }
    }
}
