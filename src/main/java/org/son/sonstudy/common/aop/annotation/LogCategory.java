package org.son.sonstudy.common.aop.annotation;

public enum LogCategory {
    AUTH("인증"),
    PRODUCT("상품"),
    ORDER("주문"),
    PAYMENT("결제"),
    USER("사용자");

    private final String description;

    LogCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
